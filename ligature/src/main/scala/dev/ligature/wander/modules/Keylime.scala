/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import scala.collection.mutable.HashMap
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Tag

trait KeylimeEditTx {
  def get(key: Seq[Byte]): Option[Seq[Byte]]
  def put(key: Seq[Byte], value: Seq[Byte]): Unit
  def delete(key: Seq[Byte]): Unit
}

trait KeylimeReadTx {
  def get(key: Seq[Byte]): Option[Seq[Byte]]
  def prefix(prefix: Seq[Byte]): Seq[Seq[Byte]]
}

trait Keylime {
  def addStore(name: String): Unit
  def removeStore(name: String): Unit
  def stores(): Seq[String]
  def read(name: String): KeylimeReadTx
  def edit(name: String): KeylimeEditTx
}

class InMemoryKeylime extends Keylime {
  private val store = scala.collection.mutable.HashMap[String, HashMap[Seq[Byte], Seq[Byte]]]()

  override def addStore(name: String): Unit = 
    val _ = store.put(name, HashMap())
    ()

  override def edit(name: String): KeylimeEditTx =
    store.get(name) match {
      case Some(value) => InMemoryKeylimeEditTx(value)
      case None => ???
    }

  override def removeStore(name: String): Unit = 
    val _ = store.remove(name)
    ()

  override def read(name: String): KeylimeReadTx =
    store.get(name) match {
      case Some(value) => InMemoryKeylimeReadTx(value)
      case None => ???
    }

  override def stores(): Seq[String] =
    store.keys.toSeq
}

class InMemoryKeylimeReadTx(store: HashMap[Seq[Byte], Seq[Byte]]) extends KeylimeReadTx {
  override def get(key: Seq[Byte]): Option[Seq[Byte]] =
    store.get(key)

  override def prefix(prefix: Seq[Byte]): Seq[Seq[Byte]] =
    store.filter((key, _) => key.startsWith(prefix)).values.toSeq
}

class InMemoryKeylimeEditTx(store: HashMap[Seq[Byte], Seq[Byte]]) extends KeylimeEditTx {
  override def get(key: Seq[Byte]): Option[Seq[Byte]] = 
    store.get(key)

  override def put(key: Seq[Byte], value: Seq[Byte]): Unit =
    val _ = store.put(key, value)

  override def delete(key: Seq[Byte]): Unit =
    val _ = store.remove(key)
}

def keylimeQueryModule(instance: KeylimeReadTx): WanderValue.Module =
    WanderValue.Module(
    Map(
      Field("get") -> WanderValue.Function(
        HostFunction(
          "Read a value from the Store.",
          Seq(
            TaggedField(Field("key"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.Bytes(key)) => 
                instance.get(key) match {
                  case Some(value) => Right((WanderValue.Bytes(value), environment))
                  case None => ???
                }
              case _ => ???
            }
        )
      ),
      Field("prefix") -> WanderValue.Function(
        HostFunction(
          "Read a prefix from the Store.",
          Seq(
            TaggedField(Field("prefix"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.Bytes(prefix)) =>
                Right((WanderValue.Array(instance.prefix(prefix).map(res => WanderValue.Bytes(res))), environment))
              case _ => ???
            }
        )
      ),
    ))

def keylimeEditModule(instance: KeylimeEditTx): WanderValue.Module =
    WanderValue.Module(
    Map(
      Field("get") -> WanderValue.Function(
        HostFunction(
          "Read a value from the Store.",
          Seq(
            TaggedField(Field("key"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.Bytes(key)) => 
                instance.get(key) match {
                  case Some(value) => Right((WanderValue.Bytes(value), environment))
                  case None => ???
                }
              case _ => ???
            }
        )
      ),
      Field("put") -> WanderValue.Function(
        HostFunction(
          "Set a value in the Store.",
          Seq(
            TaggedField(Field("key"), Tag.Untagged),
            TaggedField(Field("value"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.Bytes(key), WanderValue.Bytes(value)) =>
                instance.put(key, value)
                Right((WanderValue.Module(Map.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("delete") -> WanderValue.Function(
        HostFunction(
          "Delete a value from the Store.",
          Seq(
            TaggedField(Field("key"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.Bytes(key)) =>
                instance.delete(key)
                Right((WanderValue.Module(Map.empty), environment))
              case _ => ???
            }
        )
      ),
    ))

val keylimeModule: WanderValue.Module =
  val instance = InMemoryKeylime()
  WanderValue.Module(
    Map(
      Field("stores") -> WanderValue.Function(
        HostFunction(
          "Get all Store names.",
          Seq(
            TaggedField(Field("_"), Tag.Untagged)
          ),
          Tag.Untagged,
          (_arguments, environment) =>
            val names = instance.stores().map(name => WanderValue.String(name))
            Right((WanderValue.Array(names), environment))
        )
      ),
      Field("addStore") -> WanderValue.Function(
        HostFunction(
          "Add a new Store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name)) => 
                instance.addStore(name)
                Right((WanderValue.Module(Map.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("deleteStore") -> WanderValue.Function(
        HostFunction(
          "Delete a Store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name)) => 
                instance.removeStore(name)
                Right((WanderValue.Module(Map.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("edit") -> WanderValue.Function(
        HostFunction(
          "Edit a Store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("fn"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name), WanderValue.Function(fn)) => {
                val readModule = keylimeEditModule(instance.edit(name))
                fn.call(Seq(readModule), environment).map((_, environment))
              }
              case _ => ???
            }
        )
      ),
      Field("query") -> WanderValue.Function(
        HostFunction(
          "Query a Store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("fn"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name), WanderValue.Function(fn)) => {
                val readModule = keylimeQueryModule(instance.read(name))
                fn.call(Seq(readModule), environment).map((_, environment))
              }
              case _ => ???
            }
        )
      )
  )
)
