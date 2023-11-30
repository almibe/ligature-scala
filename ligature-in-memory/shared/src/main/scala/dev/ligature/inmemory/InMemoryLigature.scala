/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Dataset, Label, Ligature, LigatureError, QueryTx, Edge, Value}
import scala.collection.immutable.TreeMap

protected case class DatasetStore(counter: Long, edges: Set[Edge])

final class LigatureInMemory(private var store: TreeMap[Dataset, DatasetStore] = TreeMap[Dataset, DatasetStore]())
    extends Ligature {
//  private val store = AtomicCell[IO].of(TreeMap[Dataset, DatasetStore]())

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Iterator[Dataset] = store.keySet.iterator

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): Boolean = store.contains(dataset)

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Iterator[Dataset] = 
    store.keys.filter(_.name.startsWith(prefix)).iterator

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override def matchDatasetsRange(
      start: String,
      end: String
  ): Iterator[Dataset] =
    store.keys.filter(k => k.name >= start && k.name < end).iterator

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(dataset: Dataset): Unit =
      ???

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(dataset: Dataset): Unit =
    ???
    // for {
    //   _ <- store.evalUpdate(s => IO(s.removed(dataset)))
    //   s <- store.get
    // } yield ()

  override def allEdges(dataset: Dataset): Iterator[Edge] =
    ???
  //   store.get.map { store =>
  //     store.get(dataset) match
  //       case None => ???
  //       case Some(datasetStore) =>
  //         datasetStore.edges.toSeq
  //   }
  // }

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override def query[T](dataset: Dataset)(fn: QueryTx => T): T =
        store.get(dataset) match
          case None => ???
          case Some(datasetStore) =>
            val tx = InMemoryQueryTx(datasetStore)
            fn(tx)

  override def addEdges(dataset: Dataset, edges: Iterator[Edge]): Unit =
    ???
      // store.get(dataset) match
      //   case None => ???
      //   case Some(datasetStore) =>
      //     edges
      //       .fold(datasetStore) { (datasetStore, edge) =>
      //         ???
      //         //datasetStore.copy(edges = datasetStore.edges + edge)
      //       }
      //       .map { datasetStore =>
      //         store.updated(dataset, datasetStore)
      //       }

  override def removeEdges(dataset: Dataset, edges: Iterator[Edge]): Unit =
    ???
      // store.get(dataset) match
      //   case None => ???
      //   case Some(datasetStore) =>
      //     edges
      //       .fold(datasetStore) { (datasetStore, edge) =>
      //         datasetStore.copy(edges = datasetStore.edges - edge)
      //       }
      //       .map { datasetStore =>
      //         store.updated(dataset, datasetStore)
      //       }

  override def close(): Unit =
    ()
}
