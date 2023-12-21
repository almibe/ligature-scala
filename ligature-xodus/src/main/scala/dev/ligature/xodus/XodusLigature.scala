/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.{
  Ligature,
  LigatureError,
  QueryTx,
  Value,
}
import scala.collection.immutable.TreeMap
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import jetbrains.exodus.ByteIterable

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
import jetbrains.exodus.bindings.{LongBinding, StringBinding}

import scala.jdk.CollectionConverters.*
import java.nio.file.Path
import jetbrains.exodus.env.Environment
import dev.ligature.Graph
import dev.ligature.Edge
import jetbrains.exodus.entitystore.PersistentEntityStores

trait XodusOperations {
  def nextID(tx: Transaction): ByteIterable
}

enum LigatureValueType(val id: Byte):
  case Identifier extends LigatureValueType(0)
  case Integer extends LigatureValueType(1)
  case String extends LigatureValueType(2)
  case Bytes extends LigatureValueType(3)

object LigatureValueType:
  def getValueType(id: Byte): LigatureValueType = id match
    case LigatureValueType.Identifier.id => LigatureValueType.Identifier
    case LigatureValueType.Integer.id    => LigatureValueType.Integer
    case LigatureValueType.String.id     => LigatureValueType.String
    case LigatureValueType.Bytes.id      => LigatureValueType.Bytes

def createXodusLigature(path: Path): Ligature =
  val environment = Environments.newInstance(path.toFile(), new EnvironmentConfig)
  val ligatureInstance = XodusLigature(environment)
  ligatureInstance

//TODO below should accept an Xodus instance not a File
private final class XodusLigature(environment: Environment) extends Ligature with XodusOperations {

  override def allGraphs(): Iterator[Graph] =
    val buffer = ListBuffer[Graph]()
    val store = PersistentEntityStores.newInstance(environment, "__META")
    store.computeInReadonlyTransaction(tx =>
      tx.getAll("graph").forEach(entity => 
        val graph = Graph(entity.getProperty("name").asInstanceOf[String])
        buffer.append(graph)
      )
    )
    store.close()
    buffer.iterator

  override def addEdges(graph: Graph, edges: Iterator[Edge]): Unit = ???

  override def matchGraphsRange(start: String, end: String): Iterator[Graph] = ???

  override def close(): Unit = environment.close()

  override def allEdges(graph: Graph): Iterator[Edge] = ???


  override def createGraph(graph: Graph): Unit = 
    val store = PersistentEntityStores.newInstance(environment, "__META")
    store.executeInExclusiveTransaction(tx => 
      val entity = tx.newEntity("graph")
      entity.setProperty("name", graph.name)
      tx.saveEntity(entity)
    )
    store.close()

  override def deleteGraph(graph: Graph): Unit =
    val store = PersistentEntityStores.newInstance(environment, "__META")
    store.executeInExclusiveTransaction(tx => 
      val res = tx.find("graph", "name", graph.name)
      res.forEach(res =>
        res.delete()
      )
    )
    store.close()

  override def removeEdges(graph: Graph, edges: Iterator[Edge]): Unit = ???

  override def nextID(tx: Transaction): ByteIterable = ???

  override def matchGraphsPrefix(prefix: String): Iterator[Graph] = ???

  override def graphExists(graph: Graph): Boolean =
    val store = PersistentEntityStores.newInstance(environment, "__META")
    val res = store.computeInReadonlyTransaction(tx => 
      val res = tx.find("graph", "name", graph.name)
      !res.isEmpty()
    )
    store.close()
    res

  override def query[T](graph: Graph)(fn: QueryTx => T): T = ???

  // def readOnlyTransaction(): Stream[IO, Transaction] =
  //   Stream.bracket(IO(environment.beginReadonlyTransaction())){
  //     tx => IO {
  //       if !tx.isFinished() then
  //         tx.abort()
  //     }
  //   }

  // def readWriteTransaction(): Stream[IO, Transaction] =
  //   Stream.bracket(IO(environment.beginTransaction())) {
  //     tx => IO {
  //       if !tx.isFinished() then
  //         tx.commit()
  //     }
  //   }

//  override def allStatements(dataset: Dataset): Stream[IO, Statement] = ???
    // readOnlyTransaction().flatMap { tx =>
    //   fetchDatasetID(dataset, tx) match
    //     case None => ???
    //     case Some(datasetId) =>
    //       val queryTx = XodusQueryTx(tx, this, datasetId)
    //       queryTx.allStatements()
    // }

  // override def addStatements(dataset: Dataset, statements: Stream[cats.effect.IO, Statement]): IO[Unit] = ???
    // readWriteTransaction().flatMap { tx =>
    //   fetchDatasetID(dataset, tx) match
    //     case None => ???
    //     case Some(datasetId) =>
    //       val writeTx = XodusWriteTx(tx, this, datasetId)
    //       statements.foreach { statement =>
    //         writeTx.addStatement(statement)
    //       }
    // }.compile.drain

  // override def removeStatements(dataset: Dataset, statements: Stream[cats.effect.IO, Statement]): IO[Unit] = ???
    // readWriteTransaction().flatMap { tx =>
    //   fetchDatasetID(dataset, tx) match
    //     case None => ???
    //     case Some(datasetId) =>
    //       val writeTx = XodusWriteTx(tx, this, datasetId)
    //       statements.foreach { statement =>
    //         writeTx.removeStatement(statement)
    //       }
    // }.compile.drain

  // /** This method is ran once at the start to make sure all Stores exist.
  //   * This is done so that Stores can be opened in readonly mode.
  //   *  It also checks and initializes Counters
  //   * TODO: This method could probably check if the Environment exists and check the status of the Environment first.
  //   */
  // def setupStores(): Unit = {
  //   // create all Stores
  //   val createStoresTC: TransactionalComputable[Unit] = tx =>
  //     LigatureStore.values.foreach(openStore(tx, _))
  //   environment.computeInTransaction(createStoresTC)
  //   // set up counters -- for now it's just a single counter
  //   val checkCountersTC: TransactionalComputable[Unit] = tx => {
  //     val counterStore = openStore(tx, LigatureStore.CountersStore)
  //     val currentCount = counterStore.get(tx, StringBinding.stringToEntry("counter"))
  //     if (currentCount == null) {
  //       counterStore.put(tx, StringBinding.stringToEntry("counter"), LongBinding.longToEntry(0))
  //     }
  //   }
  //   environment.computeInTransaction(checkCountersTC)
  // }

  /** Used to uniformly open Stores.
    * This is mainly so that StoreConfigs can be consistent.
    * Right now all stores use the same config but that is likely to change.
    */
  // override def openStore(tx: Transaction, store: LigatureStore): Store =
  //   environment.openStore(store.storeName, StoreConfig.WITHOUT_DUPLICATES, tx)

  /** Computes the next ID.
    * Right now IDs are shared between all Stores but that might change.
    * Also, right now IDs are positive Long values and that might also change.
    */
  // override def nextID(tx: Transaction): ByteIterable = {
  //   val counterStore = openStore(tx, LigatureStore.CountersStore)
  //   val nextCount =
  //     LongBinding.entryToLong(counterStore.get(tx, StringBinding.stringToEntry("counter"))) + 1L
  //   counterStore.put(tx, StringBinding.stringToEntry("counter"), LongBinding.longToEntry(nextCount))
  //   LongBinding.longToEntry(nextCount)
  // }

  /** Returns all Datasets in a Ligature instance. */
  // override def allDatasets(): Stream[IO, Dataset] = ??? //Stream.emits {
  //   val tc: TransactionalComputable[List[String]] = tx => {
  //     val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
  //     val datasetsCursor = datasetToIdStore.openCursor(tx)
  //     val datasets: ListBuffer[String] = ListBuffer()
  //     while (datasetsCursor.getNext)
  //       datasets.append(StringBinding.entryToString(datasetsCursor.getKey))
  //     datasetsCursor.close() // TODO should use bracket
  //     datasets.toList
  //   }
  //   environment.computeInReadonlyTransaction(tc).map(Dataset.fromString(_).getOrElse(???))
  // }

  // private def fetchDatasetID(dataset: Dataset, tx: Transaction): Option[ByteIterable] =
  //   val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
  //   val res = datasetToIdStore.get(tx, StringBinding.stringToEntry(dataset.name))
  //   res match {
  //     case null                => None
  //     case bytes: ByteIterable => Some(bytes)
  //   }

  /** Check if a given Dataset exists. */
  // override def datasetExists(dataset: Dataset): IO[Boolean] = ???
    // readOnlyTransaction()
    //   .map { tx => fetchDatasetID(dataset, tx).isDefined }
    //   .compile
    //   .onlyOrError

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  // override def matchDatasetsPrefix(prefix: String): Stream[IO, Dataset] = ??? // Stream.emits {
  //   val tc: TransactionalComputable[Seq[Dataset]] = tx => {
  //     val results: ArrayBuffer[Dataset] = ArrayBuffer()
  //     val cursor = openStore(tx, LigatureStore.DatasetToIdStore).openCursor(tx)
  //     val prefixBytes = StringBinding.stringToEntry(prefix)
  //     cursor.getSearchKeyRange(prefixBytes)
  //     var r = cursor.getKey
  //     var continue = true
  //     while (continue && r != null && StringBinding.entryToString(r).startsWith(prefix)) {
  //       results.append(Dataset.fromString(StringBinding.entryToString(r)).getOrElse(???))
  //       continue = cursor.getNext
  //       r = cursor.getKey
  //     }
  //     results.toSeq
  //   }
  //   environment.computeInReadonlyTransaction(tc)
  // }

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  // override def matchDatasetsRange(start: String, end: String): Stream[IO, Dataset] = ??? /// Stream.emits {
  //   val tc: TransactionalComputable[Seq[Dataset]] = tx => {
  //     val results: ArrayBuffer[Dataset] = ArrayBuffer()
  //     val cursor = openStore(tx, LigatureStore.DatasetToIdStore).openCursor(tx)
  //     val prefixBytes = StringBinding.stringToEntry(start)
  //     cursor.getSearchKeyRange(prefixBytes)
  //     var r = cursor.getKey
  //     var continue = true
  //     while (continue && r != null && stringInRange(StringBinding.entryToString(r), start, end)) {
  //       results.append(Dataset.fromString(StringBinding.entryToString(r)).getOrElse(???))
  //       continue = cursor.getNext
  //       r = cursor.getKey
  //     }
  //     results.toSeq
  //   }
  //   environment.computeInReadonlyTransaction(tc)
  // }

  // private def stringInRange(toTest: String, start: String, end: String): Boolean =
  //   toTest.compareTo(start) >= 0 && toTest.compareTo(end) < 0

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  // override def createDataset(dataset: Dataset): IO[Unit] = ??? //IO {
  //   val tc: TransactionalExecutable = tx => {
  //     val nameEntry = StringBinding.stringToEntry(dataset.name)
  //     val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
  //     val datasetExists = datasetToIdStore.get(tx, nameEntry)
  //     if (datasetExists == null) {
  //       val nextId = nextID(tx)
  //       val idToDatasetStore = openStore(tx, LigatureStore.IdToDatasetStore)
  //       datasetToIdStore.put(tx, nameEntry, nextId)
  //       idToDatasetStore.putRight(tx, nextId, nameEntry)
  //       tx.commit()
  //     } else {
  //       ()
  //     }
  //   }
  //   environment.executeInTransaction(tc)
  // }

  // /** Deletes a dataset with the given name. TODO should probably return its own
  //   * error type { InvalidDataset, CouldNotDeleteDataset }
  //   */
  // override def deleteDataset(dataset: Dataset): IO[Unit] = IO {
  //   val tc: TransactionalExecutable = tx => {
  //     val nameEntry = StringBinding.stringToEntry(dataset.name)
  //     val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
  //     val datasetId = datasetToIdStore.get(tx, nameEntry)
  //     if (datasetId != null) {
  //       val idToDatasetStore = openStore(tx, LigatureStore.IdToDatasetStore)
  //       datasetToIdStore.delete(tx, nameEntry)
  //       idToDatasetStore.delete(tx, datasetId)
  //       deleteAllStatements(tx, datasetId)
  //       tx.commit()
  //     } else {
  //       ()
  //     }
  //   }
  //   environment.executeInTransaction(tc)
  // }

  // /** This method iterates through all of the index stores and removes all Statements
  //   * in a given Dataset.
  //   * TODO this method also needs to clean up all Identifiers, Strings, and Bytes
  //   */
  // private def deleteAllStatements(tx: Transaction, datasetId: ByteIterable) =
  //   val stores = List(
  //     LigatureStore.EAVStore,
  //     LigatureStore.EVAStore,
  //     LigatureStore.AEVStore,
  //     LigatureStore.AVEStore,
  //     LigatureStore.VEAStore,
  //     LigatureStore.VAEStore,
  //     LigatureStore.IdentifierToIdStore,
  //     LigatureStore.IdToIdentifierStore,
  //     LigatureStore.StringToIdStore,
  //     LigatureStore.IdToStringStore,
  //     LigatureStore.BytesToIdStore,
  //     LigatureStore.IdToBytesStore
  //   )
  //   stores.foreach { store =>
  //     val cursor = openStore(tx, store).openCursor(tx)
  //     var continue = cursor.getSearchKeyRange(datasetId) != null
  //     while (continue) {
  //       val key = cursor.getKey
  //       if (key.subIterable(0, 8) == datasetId) {
  //         cursor.deleteCurrent()
  //         continue = cursor.getNext
  //       } else {
  //         continue = false
  //       }
  //     }
  //   }

  // /** Initializes a QueryTx TODO should probably return its own error type
  //   * CouldNotInitializeQueryTx
  //   */
  // override def query[T](dataset: Dataset)(fn: QueryTx => IO[T]): IO[T] =
  //   IO {
  //     environment.beginReadonlyTransaction()
  //   }.bracket { tx =>
  //     IO.defer {
  //       fetchDatasetID(dataset, tx) match {
  //         case None => ???
  //         case Some(datasetId) =>
  //           val queryTx = XodusQueryTx(tx, this, datasetId)
  //           fn(queryTx)
  //       }
  //     }
  //   } { tx =>
  //     IO.defer {
  //       if (!tx.isFinished) {
  //         tx.abort()
  //       }
  //       IO.unit
  //     }
  //   }

  // override def close(): IO[Unit] = IO {
  //   environment.close()
  //   ()
  // }
}
