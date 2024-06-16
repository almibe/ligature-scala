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
import dev.ligature.wander.Statement
import scala.collection.mutable.HashSet

trait LigatureStore {
  def networks(): Seq[String]
  def addNetwork(name: String): Unit
  def removeNetwork(name: String): Unit
  def add(name: String, network: Seq[Statement]): Unit
  def remove(name: String, network: Seq[Statement]): Unit
  def query(name: String, network: Seq[Statement]): Seq[Statement]
}

class InMemoryStore extends LigatureStore {
  private val store = scala.collection.mutable.HashMap[String, HashSet[Statement]]()

  override def networks(): Seq[String] =
    store.keys.toSeq

  override def addNetwork(name: String): Unit =
    val _ = store.put(name, HashSet())
    ()

  override def removeNetwork(name: String): Unit =
    val _ = store.remove(name)
    ()

  override def add(name: String, network: Seq[Statement]): Unit =
    store.get(name) match {
      case None             => ???
      case Some(statements) => statements.addAll(network)
    }

  override def remove(name: String, network: Seq[Statement]): Unit =
    store.get(name) match {
      case None             => ???
      case Some(statements) => statements.subtractAll(network)
    }

  override def query(name: String, network: Seq[Statement]): Seq[Statement] =
    store.get(name) match {
      case None => ???
      case Some(value) =>
        value.toSeq
    }
}

val inMemoryModule: WanderValue.Module =
  val instance = InMemoryStore()
  WanderValue.Module(
    Map(
      Field("networks") -> WanderValue.Function(
        HostFunction(
          "Get all Network names.",
          Seq(
            TaggedField(Field("_"), Tag.Untagged)
          ),
          Tag.Untagged,
          (_arguments, environment) =>
            val names = instance.networks().map(name => WanderValue.String(name))
            Right((WanderValue.Array(names), environment))
        )
      ),
      Field("addNetwork") -> WanderValue.Function(
        HostFunction(
          "Add a new Network.",
          Seq(
            TaggedField(Field("networkName"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name)) =>
                instance.addNetwork(name)
                Right((WanderValue.Module(Map.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("removeNetwork") -> WanderValue.Function(
        HostFunction(
          "Delete a Network.",
          Seq(
            TaggedField(Field("networkName"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name)) =>
                instance.removeNetwork(name)
                Right((WanderValue.Module(Map.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("add") -> WanderValue.Function(
        HostFunction(
          "Add Statements to an existing Network.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("network"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name), WanderValue.Network(network)) =>
                instance.add(name, network.toSeq)
                Right((WanderValue.Network(Set.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("remove") -> WanderValue.Function(
        HostFunction(
          "Remove Statements from an existing Network.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("network"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name), WanderValue.Network(network)) =>
                instance.add(name, network.toSeq)
                Right((WanderValue.Network(Set.empty), environment))
              case _ => ???
            }
        )
      ),
      Field("query") -> WanderValue.Function(
        HostFunction(
          "Query a Store.",
          Seq(
            TaggedField(Field("storeName"), Tag.Untagged),
            TaggedField(Field("network"), Tag.Untagged)
          ),
          Tag.Untagged,
          (arguments, environment) =>
            arguments match {
              case Seq(WanderValue.String(name), WanderValue.Network(network)) =>
                val res = instance.query(name, network.toSeq)
                Right((WanderValue.Network(res.toSet), environment))
              case _ => ???
            }
        )
      )
    )
  )
