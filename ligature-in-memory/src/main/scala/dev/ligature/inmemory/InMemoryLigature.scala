/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Ligature, QueryTx, Statement}
import scala.collection.mutable.{Set, TreeMap}
import dev.ligature.DatasetName

protected case class DatasetStore(var counter: Long = 0, val edges: Set[Statement] = Set())

final class LigatureInMemory(
    private val store: TreeMap[DatasetName, DatasetStore] = TreeMap[DatasetName, DatasetStore]()
) extends Ligature {
//  private val store = AtomicCell[IO].of(TreeMap[Dataset, DatasetStore]())

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Iterator[DatasetName] = store.keySet.iterator

  /** Check if a given Dataset exists. */
  override def graphExists(graph: DatasetName): Boolean = store.contains(graph)

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Iterator[DatasetName] =
    store.keys.filter(_.name.startsWith(prefix)).iterator

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override def matchDatasetsRange(
      start: String,
      end: String
  ): Iterator[DatasetName] =
    store.keys.filter(k => k.name >= start && k.name < end).iterator

  /** Creates a graph with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(graph: DatasetName): Unit =
    if !this.store.contains(graph) then this.store += (graph -> DatasetStore())

  /** Deletes a graph with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(graph: DatasetName): Unit =
    val _ = this.store.remove(graph)

  override def allStatements(graph: DatasetName): Iterator[Statement] =
    this.store.get(graph) match
      case None        => Iterator.empty
      case Some(value) => value.edges.iterator

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override def query[T](graph: DatasetName)(fn: QueryTx => T): T =
    store.get(graph) match
      case None => ???
      case Some(graphStore) =>
        val tx = InMemoryQueryTx(graphStore)
        fn(tx)

  override def addStatements(graph: DatasetName, edges: Iterator[Statement]): Unit =
    this.store.get(graph) match {
      case None =>
        this.store.addOne((graph, DatasetStore(0, Set.from(edges.toSet))))
      case Some(store) => store.edges.addAll(edges)
    }
    // store.get(graph) match
    //   case None => ???
    //   case Some(graphStore) =>
    //     edges
    //       .fold(graphStore) { (graphStore, edge) =>
    //         ???
    //         //graphStore.copy(edges = graphStore.edges + edge)
    //       }
    //       .map { graphStore =>
    //         store.updated(graph, graphStore)
    //       }

  override def removeStatements(graph: DatasetName, edges: Iterator[Statement]): Unit =
    this.store.get(graph) match {
      case None        => ???
      case Some(store) => store.edges.subtractAll(edges)
    }
    // store.get(graph) match
    //   case None => ???
    //   case Some(graphStore) =>
    //     edges
    //       .fold(graphStore) { (graphStore, edge) =>
    //         graphStore.copy(edges = graphStore.edges - edge)
    //       }
    //       .map { graphStore =>
    //         store.updated(graph, graphStore)
    //       }

  override def close(): Unit =
    ()
}
