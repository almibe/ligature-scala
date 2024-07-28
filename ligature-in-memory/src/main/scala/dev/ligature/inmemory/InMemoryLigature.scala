/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

// import dev.ligature.{Ligature, QueryTx, Triple}
// import scala.collection.mutable.{Set, TreeMap}
// import dev.ligature.DatasetName

// protected case class DatasetStore(var counter: Long = 0, val edges: Set[Triple] = Set())

case class Test(val x: Int)

// final class LigatureInMemory(
//     private val store: TreeMap[DatasetName, DatasetStore] = TreeMap[DatasetName, DatasetStore]()
// ) extends Ligature {
// //  private val store = AtomicCell[IO].of(TreeMap[Dataset, DatasetStore]())

//   /** Returns all Datasets in a Ligature instance. */
//   override def allDatasets(): Iterator[DatasetName] = store.keySet.iterator

//   /** Check if a given Dataset exists. */
//   override def networkExists(network: DatasetName): Boolean = store.contains(network)

//   /** Returns all Datasets in a Ligature instance that start with the given
//     * prefix.
//     */
//   override def matchDatasetsPrefix(prefix: String): Iterator[DatasetName] =
//     store.keys.filter(_.name.startsWith(prefix)).iterator

//   /** Returns all Datasets in a Ligature instance that are in a given range
//     * (inclusive, exclusive].
//     */
//   override def matchDatasetsRange(
//       start: String,
//       end: String
//   ): Iterator[DatasetName] =
//     store.keys.filter(k => k.name >= start && k.name < end).iterator

//   /** Creates a network with the given name. TODO should probably return its own
//     * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
//     */
//   override def createDataset(network: DatasetName): Unit =
//     if !this.store.contains(network) then this.store += (network -> DatasetStore())

//   /** Deletes a network with the given name. TODO should probably return its own
//     * error type { InvalidDataset, CouldNotDeleteDataset }
//     */
//   override def deleteDataset(network: DatasetName): Unit =
//     val _ = this.store.remove(network)

//   override def allTriples(network: DatasetName): Iterator[Triple] =
//     this.store.get(network) match
//       case None        => Iterator.empty
//       case Some(value) => value.edges.iterator

//   /** Initializes a QueryTx TODO should probably return its own error type
//     * CouldNotInitializeQueryTx
//     */
//   override def query[T](network: DatasetName)(fn: QueryTx => T): T =
//     store.get(network) match
//       case None => ???
//       case Some(networkStore) =>
//         val tx = InMemoryQueryTx(networkStore)
//         fn(tx)

//   override def addTriples(network: DatasetName, edges: Iterator[Triple]): Unit =
//     this.store.get(network) match {
//       case None =>
//         this.store.addOne((network, DatasetStore(0, Set.from(edges.toSet))))
//       case Some(store) => store.edges.addAll(edges)
//     }
//     // store.get(network) match
//     //   case None => ???
//     //   case Some(networkStore) =>
//     //     edges
//     //       .fold(networkStore) { (networkStore, edge) =>
//     //         ???
//     //         //networkStore.copy(edges = networkStore.edges + edge)
//     //       }
//     //       .map { networkStore =>
//     //         store.updated(network, networkStore)
//     //       }

//   override def removeTriples(network: DatasetName, edges: Iterator[Triple]): Unit =
//     this.store.get(network) match {
//       case None        => ???
//       case Some(store) => store.edges.subtractAll(edges)
//     }
//     // store.get(network) match
//     //   case None => ???
//     //   case Some(networkStore) =>
//     //     edges
//     //       .fold(networkStore) { (networkStore, edge) =>
//     //         networkStore.copy(edges = networkStore.edges - edge)
//     //       }
//     //       .map { networkStore =>
//     //         store.updated(network, networkStore)
//     //       }

//   override def close(): Unit =
//     ()
// }
