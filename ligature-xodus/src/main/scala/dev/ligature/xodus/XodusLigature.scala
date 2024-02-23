/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.{Ligature, LigatureError, QueryTx, Value}
import scala.collection.immutable.TreeMap
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

import java.io.File
import jetbrains.exodus.env.{
  EnvironmentConfig,
  Environments,
  ReadonlyTransaction,
  Store,
  StoreConfig,
  Transaction,
  TransactionalComputable,
  TransactionalExecutable
}

import scala.jdk.CollectionConverters.*
import java.nio.file.Path
import jetbrains.exodus.env.Environment
import dev.ligature.Graph
import dev.ligature.Edge
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.StoreTransactionalComputable
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.StoreTransaction
import dev.ligature.LigatureValue
import dev.ligature.Label
import jetbrains.exodus.entitystore.PersistentEntityStore

val VERTEX = 0
val INT = 1
val STRING = 2

def createXodusLigature(path: Path): Ligature =
  val environment = Environments.newInstance(path.toFile(), EnvironmentConfig())
  val ligatureInstance = XodusLigature(environment)
  ligatureInstance

//TODO below should accept an Xodus instance not a File
private final class XodusLigature(environment: Environment) extends Ligature {
  private val stores = scala.collection.mutable.HashMap[String, PersistentEntityStore]()

  private def getStore(name: String): PersistentEntityStore =
    if stores.contains(name) then stores(name)
    else
      val store = PersistentEntityStores.newInstance(environment, name)
      stores += name -> store
      store

  override def allGraphs(): Iterator[Graph] =
    val buffer = ListBuffer[Graph]()
    val store = getStore("__META")
    store.computeInReadonlyTransaction(tx =>
      tx.getAll("graph")
        .forEach(entity =>
          val graph = Graph(entity.getProperty("name").asInstanceOf[String])
          buffer.append(graph)
        )
    )
    store.close()
    buffer.iterator

  override def createGraph(graph: Graph): Unit =
    val store = getStore("__META")
    store.executeInExclusiveTransaction(tx =>
      val entity = tx.newEntity("graph")
      entity.setProperty("name", graph.name)
      tx.saveEntity(entity)
    )
    store.close()

  override def deleteGraph(graph: Graph): Unit =
    val store = getStore("__META")
    store.executeInExclusiveTransaction(tx =>
      val res = tx.find("graph", "name", graph.name)
      res.forEach(res => res.delete())
    )
    store.close()

  override def matchGraphsPrefix(prefix: String): Iterator[Graph] =
    val buffer = ListBuffer[Graph]()
    val store = getStore("__META")
    store.computeInReadonlyTransaction(tx =>
      tx.findStartingWith("graph", "name", prefix)
        .forEach(entity =>
          val graph = Graph(entity.getProperty("name").asInstanceOf[String])
          buffer.append(graph)
        )
    )
    store.close()
    buffer.iterator

  override def graphExists(graph: Graph): Boolean =
    val store = getStore("__META")
    val res = store.computeInReadonlyTransaction(tx =>
      val res = tx.find("graph", "name", graph.name)
      !res.isEmpty()
    )
    store.close()
    res

  override def matchGraphsRange(start: String, end: String): Iterator[Graph] =
    val buffer = ListBuffer[Graph]()
    val store = getStore("__META")
    store.computeInReadonlyTransaction(tx =>
      tx.getAll("graph")
        .forEach(entity =>
          val name = entity.getProperty("name").asInstanceOf[String]
          if (name >= start && name < end) {
            val graph = Graph(name)
            buffer.append(graph)
          }
        )
    )
    store.close()
    buffer.iterator

  override def allEdges(graph: Graph): Iterator[Edge] =
    val store = getStore(graph.name)
    store.computeInReadonlyTransaction(tx => entitiesToEdges(tx.getAll("edge")).iterator)

  override def addEdges(graph: Graph, edges: Iterator[Edge]): Unit =
    val store = getStore(graph.name)
    store.executeInExclusiveTransaction(tx =>
      edges.foreach(edge =>
        if findEdge(edge, tx).isEmpty then
          val entity = tx.newEntity("edge")
          entity.setProperty("source", edge.source.text)
          entity.setProperty("label", edge.label.text)
          entity.setProperty("target", targetValue(edge.target))
          entity.setProperty("targetType", targetType(edge.target))
      )
    )

  override def removeEdges(graph: Graph, edges: Iterator[Edge]): Unit =
    val store = getStore(graph.name)
    store.executeInExclusiveTransaction(tx =>
      edges.foreach(edge =>
        findEdge(edge, tx) match
          case None       => ()
          case Some(edge) => edge.delete()
      )
    )

  private def findEdge(edge: Edge, tx: StoreTransaction): Option[Entity] =
    var res: Option[Entity] = None
    tx.find("edge", "source", edge.source.text)
      .forEach(entity =>
        if entity.getProperty("label") == edge.label.text &&
          entity.getProperty("targetType").asInstanceOf[Int] == targetType(edge.target) &&
          entity.getProperty("target") == targetValue(edge.target)
        then res = Some(entity)
      )
    res

  override def query[T](graph: Graph)(fn: QueryTx => T): T =
    val store = getStore(graph.name)
    store.computeInReadonlyTransaction(tx =>
      val queryTx = XodusQueryTx(tx)
      fn(queryTx)
    )

  override def close(): Unit = () // environment.close()
}

def targetValue(value: Value): Comparable[?] =
  value match
    case LigatureValue.IntegerLiteral(value) => value
    case LigatureValue.StringLiteral(value)  => value
    case Label(value)                          => value

def targetType(value: Value): Int =
  value match
    case LigatureValue.IntegerLiteral(_) => INT
    case LigatureValue.StringLiteral(_)  => STRING
    case Label(_)                          => VERTEX
