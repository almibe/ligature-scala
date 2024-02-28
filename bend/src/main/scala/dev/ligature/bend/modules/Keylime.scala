/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.Environment
import dev.ligature.bend.BendValue
import dev.ligature.bend.Field
import dev.ligature.bend.FieldPath
import dev.ligature.bend.HostFunction
import dev.ligature.bend.Tag
import dev.ligature.bend.TaggedField
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
  val home = System.getProperty("user.home") + "/.bend"
  openStore(Paths.get(home))

def openStore(path: Path): jetbrains.exodus.env.Environment =
  val environment = Environments.newInstance(path.toFile(), EnvironmentConfig())
  environment

def createKeylimeModule(env: jetbrains.exodus.env.Environment): BendValue.Module =
  BendValue.Module(
    Map(
      Field("stores") -> BendValue.Function(
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
                    .map(name => BendValue.String(name.asInstanceOf[String]))
                    .toArray()
                    .toSeq
                    .asInstanceOf[Seq[BendValue]]
                )
                Right((BendValue.Array(stores), environment))
              case _ => ???
            }
        )
      ),
      Field("addStore") -> BendValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("addStore"))),
          "Add a new Store.",
          Seq(TaggedField(Field("storeName"), Tag.Untagged)),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(BendValue.String(storeName)) =>
                val stores = env.executeInTransaction(tx =>
                  env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                )
                Right((BendValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("removeStore") -> BendValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("removeStore"))),
          "Remove a Store.",
          Seq(TaggedField(Field("storeName"), Tag.Untagged)),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(BendValue.String(storeName)) =>
                val stores = env.executeInTransaction(tx => env.removeStore(storeName, tx))
                Right((BendValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("set") -> BendValue.Function(
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
                    BendValue.String(storeName),
                    BendValue.Bytes(key),
                    BendValue.Bytes(value)
                  ) =>
                env.executeInTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  store.put(tx, ArrayByteIterable(key.toArray), ArrayByteIterable(value.toArray))
                  tx.commit()
                )
                Right((BendValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("setAll") -> BendValue.Function(
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
              case Seq(BendValue.String(storeName), BendValue.Array(entries)) =>
                env.executeInTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  entries.foreach { entry =>
                    entry match {
                      case BendValue.Array(
                            Seq(BendValue.Bytes(key), BendValue.Bytes(value))
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
                Right((BendValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("get") -> BendValue.Function(
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
              case Seq(BendValue.String(storeName), BendValue.Bytes(key)) =>
                val result = env.computeInReadonlyTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  store.get(tx, ArrayByteIterable(key.toArray))
                )
                Right((BendValue.Bytes(result.getBytesUnsafe().toSeq), environment))
              case _ => ???
            }
        )
      ),
      Field("delete") -> BendValue.Function(
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
              case Seq(BendValue.String(storeName), BendValue.Bytes(key)) =>
                val result = env.executeInTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  store.delete(tx, ArrayByteIterable(key.toArray))
                  tx.commit()
                )
                Right((BendValue.Module(Map()), environment))
              case _ => ???
            }
        )
      ),
      Field("entries") -> BendValue.Function(
        HostFunction(
          // FieldPath(Seq(Field("Keylime"), Field("entries"))),
          "Retrieve all values in this store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged)
          ),
          Tag.Untagged,
          (args, environment) =>
            args match {
              case Seq(BendValue.String(storeName)) =>
                val results = env.computeInReadonlyTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  val cursor = store.openCursor(tx)
                  val results: ListBuffer[BendValue.Array] = scala.collection.mutable.ListBuffer()
                  while (cursor.getNext()) {
                    val key = cursor.getKey().getBytesUnsafe().toSeq
                    val value = cursor.getValue().getBytesUnsafe().toSeq
                    results += BendValue.Array(
                      Seq(BendValue.Bytes(key), BendValue.Bytes(value))
                    )
                  }
                  results
                )
                Right((BendValue.Array(results.toSeq), environment))
              case _ => ???
            }
        )
      ),
      Field("range") -> BendValue.Function(
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
                    BendValue.String(storeName),
                    BendValue.Bytes(start),
                    BendValue.Bytes(end)
                  ) =>
                val results = env.computeInReadonlyTransaction(tx =>
                  val store =
                    env.openStore(storeName, StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, tx)
                  val cursor = store.openCursor(tx)
                  cursor.getSearchKey(ArrayByteIterable(start.toArray))
                  cursor.getPrev()
                  val results: ListBuffer[BendValue.Array] = scala.collection.mutable.ListBuffer()
                  while (
                    cursor.getNext() && (cursor
                      .getValue()
                      .compareTo(ArrayByteIterable(end.toArray)) != 1)
                  ) {
                    val key = cursor.getKey().getBytesUnsafe().toSeq
                    val value = cursor.getValue().getBytesUnsafe().toSeq
                    results += BendValue.Array(
                      Seq(BendValue.Bytes(key), BendValue.Bytes(value))
                    )
                  }
                  results
                )
                Right((BendValue.Array(results.toSeq), environment))
              case _ => ???
            }
        )
      )
    )
  )
