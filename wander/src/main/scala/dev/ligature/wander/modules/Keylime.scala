/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.Environment
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.FieldPath
import dev.ligature.wander.HostFunction
import dev.ligature.wander.Tag
import dev.ligature.wander.TaggedField
import java.nio.file.Path
import jetbrains.exodus.env.Environments
import jetbrains.exodus.env.EnvironmentConfig
import jetbrains.exodus.entitystore.PersistentEntityStore
import java.nio.file.Paths
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.ArrayByteIterable
import scala.collection.mutable.ListBuffer

def openDefault(): jetbrains.exodus.env.Environment =
  val home = System.getProperty("user.home") + "/.wander"
  openStore(Paths.get(home))

def openStore(path: Path): jetbrains.exodus.env.Environment =
  val environment = Environments.newInstance(path.toFile(), EnvironmentConfig())
  environment

def createKeylimeModule(env: jetbrains.exodus.env.Environment): WanderValue.Module =
  WanderValue.Module(
    Map(
      Field("stores") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("stores"))),
          "Get an Array of all Store names.",
          Seq(TaggedField(Field("_"), Tag.Untagged)),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(_) =>
                val stores = env.computeInReadonlyTransaction(tx =>
                  env
                    .getAllStoreNames(tx)
                    .stream()
                    .map(name => WanderValue.String(name.asInstanceOf[String]))
                    .toArray()
                    .toSeq
                    .asInstanceOf[Seq[WanderValue]]
                )
                Right((WanderValue.Array(stores), environment))
              case _ => ???
            }
        )
      ),
      Field("addStore") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("addStore"))),
          "Add a new Store.",
          Seq(TaggedField(Field("storeName"), Tag.Untagged)),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(WanderValue.String(storeName)) =>
                val stores = env.executeInTransaction(tx =>
                  env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                )
                Right((WanderValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("removeStore") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("removeStore"))),
          "Remove a Store.",
          Seq(TaggedField(Field("storeName"), Tag.Untagged)),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(WanderValue.String(storeName)) =>
                val stores = env.executeInTransaction(tx => env.removeStore(storeName, tx))
                Right((WanderValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("set") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("set"))),
          "Set a Value from a Store with the given Key.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("key"), Tag.Untagged),
            TaggedField(Field("value"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(
                    WanderValue.String(storeName),
                    WanderValue.Bytes(key),
                    WanderValue.Bytes(value)
                  ) =>
                env.executeInTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  store.put(tx, ArrayByteIterable(key.toArray), ArrayByteIterable(value.toArray))
                  tx.commit()
                )
                Right((WanderValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("setAll") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("setAll"))),
          "Set an Array of Key Value pairs.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("entries"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(WanderValue.String(storeName), WanderValue.Array(entries)) =>
                env.executeInTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  entries.foreach { entry =>
                    entry match {
                      case WanderValue.Array(
                            Seq(WanderValue.Bytes(key), WanderValue.Bytes(value))
                          ) =>
                        store.put(
                          tx,
                          ArrayByteIterable(key.toArray),
                          ArrayByteIterable(value.toArray)
                        )
                      case _ => ???
                    }
                  }
                  tx.commit()
                )
                Right((WanderValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("get") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("get"))),
          "Retrieve a Value from a Store with the given Key.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("key"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(WanderValue.String(storeName), WanderValue.Bytes(key)) =>
                val result = env.computeInReadonlyTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  store.get(tx, ArrayByteIterable(key.toArray))
                )
                Right((WanderValue.Bytes(result.getBytesUnsafe().toSeq), environment))
              case _ => ???
            }
        )
      ),
      Field("delete") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("delete"))),
          "Delete the entry with the given Key.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("key"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(WanderValue.String(storeName), WanderValue.Bytes(key)) =>
                val result = env.executeInTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  store.delete(tx, ArrayByteIterable(key.toArray))
                  tx.commit()
                )
                Right((WanderValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("entries") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("entries"))),
          "Retrieve all values in this store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(WanderValue.String(storeName)) =>
                val results = env.computeInReadonlyTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  val cursor = store.openCursor(tx)
                  val results: ListBuffer[WanderValue.Array] = scala.collection.mutable.ListBuffer()
                  while (cursor.getNext()) {
                    val key = cursor.getKey().getBytesUnsafe().toSeq
                    val value = cursor.getValue().getBytesUnsafe().toSeq
                    results += WanderValue.Array(
                      Seq(WanderValue.Bytes(key), WanderValue.Bytes(value))
                    )
                  }
                  results
                )
                Right((WanderValue.Array(results.toSeq), environment))
              case _ => ???
            }
        )
      ),
      Field("range") -> WanderValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("range"))),
          "Retrieve all values in this store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("start"), Tag.Untagged),
            TaggedField(Field("end"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(
                    WanderValue.String(storeName),
                    WanderValue.Bytes(start),
                    WanderValue.Bytes(end)
                  ) =>
                val results = env.computeInReadonlyTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  val cursor = store.openCursor(tx)
                  cursor.getSearchKey(ArrayByteIterable(start.toArray))
                  cursor.getPrev()
                  val results: ListBuffer[WanderValue.Array] = scala.collection.mutable.ListBuffer()
                  while (
                    cursor.getNext() && (cursor
                      .getValue()
                      .compareTo(ArrayByteIterable(end.toArray)) != 1)
                  ) {
                    val key = cursor.getKey().getBytesUnsafe().toSeq
                    val value = cursor.getValue().getBytesUnsafe().toSeq
                    results += WanderValue.Array(
                      Seq(WanderValue.Bytes(key), WanderValue.Bytes(value))
                    )
                  }
                  results
                )
                Right((WanderValue.Array(results.toSeq), environment))
              case _ => ???
            }
        )
      )
    )
  )
