/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.{Dataset, Identifier, Ligature, LigatureError, QueryTx, Statement, Value, WriteTx}
import cats.effect.IO
import fs2.Stream

import scala.collection.immutable.TreeMap
import scala.collection.mutable.ListBuffer
import cats.effect.kernel.Resource
import cats.effect.kernel.Ref
import jetbrains.exodus.ByteIterable

import java.io.File
import jetbrains.exodus.env.{EnvironmentConfig, Environments, ReadonlyTransaction, Store, StoreConfig, Transaction, TransactionalComputable}
import jetbrains.exodus.bindings.{LongBinding, StringBinding}

import scala.jdk.CollectionConverters.*

final class XodusLigature(dbDirectory: File) extends Ligature {
  private val environment = Environments.newInstance(dbDirectory, new EnvironmentConfig)
  setupStores()

  enum LigatureStore(val storeName: String):
    case CountersStore extends LigatureStore("Counters")
    case DatasetToIdStore extends LigatureStore("DatasetToID")
    case IdToDatasetStore extends LigatureStore("IDToDataset")
    case EAVStore extends LigatureStore("EAV")
    case EVAStore extends LigatureStore("EVA")
    case AEVStore extends LigatureStore("AEV")
    case AVEStore extends LigatureStore("AVE")
    case VEAStore extends LigatureStore("VEA")
    case VAEStore extends LigatureStore("VAE")
    case IdentifierToIdStore extends LigatureStore("IdentifierToID")
    case IdToIdentifierStore extends LigatureStore("IDToIdentifier")
    case StringToIdStore extends LigatureStore("StringToID")
    case IdToStringStore extends LigatureStore("IDToString")
    case BytesToIdStore extends LigatureStore("BytesToID")
    case IdToBytesStore extends LigatureStore("IDToBytes")

  /** This method is ran once at the start to make sure all Stores exist.
    * This is done so that Stores can be opened in readonly mode.
   *  It also checks and initializes Counters
    * TODO: This method could probably check if the Environment exists and check the status of the Environment first.
    */
  private def setupStores(): Unit = {
    //create all Stores
    val createStoresTC: TransactionalComputable[Unit] = tx => LigatureStore.values.foreach(openStore(tx, _))
    environment.computeInTransaction(createStoresTC)
    //set up counters -- for now it's just a single counter
    val checkCountersTC: TransactionalComputable[Unit] = tx => {
      val counterStore = openStore(tx, LigatureStore.CountersStore)
      val currentCount = counterStore.get(tx, StringBinding.stringToEntry("counter"))
      if (currentCount == null) {
        counterStore.put(tx, StringBinding.stringToEntry("counter"), LongBinding.longToEntry(0))
      }
    }
    environment.computeInTransaction(checkCountersTC)
  }

  /** Used to uniformly open Stores.
    * This is mainly so that StoreConfigs can be consistent.
    * Right now all stores use the same config but that is likely to change.
    */
  private def openStore(tx: Transaction, store: LigatureStore): Store =
    environment.openStore(store.storeName, StoreConfig.WITHOUT_DUPLICATES, tx)

  /**
   * Computes the next ID.
   * Right now IDs are shared between all Stores but that might change.
   * Also, right now IDs are positive Long values and that might also change.
   */
  private def nextID(tx: Transaction): ByteIterable = {
    val counterStore = openStore(tx, LigatureStore.CountersStore)
    val nextCount = LongBinding.entryToLong(counterStore.get(tx, StringBinding.stringToEntry("counter"))) + 1L
    counterStore.put(tx, StringBinding.stringToEntry("counter"), LongBinding.longToEntry(nextCount))
    LongBinding.longToEntry(nextCount)
  }

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = Stream.emits {
    val tc: TransactionalComputable[List[String]] = tx => {
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val datasetsCursor = datasetToIdStore.openCursor(tx)
      val datasets: ListBuffer[String] = ListBuffer()
      while (datasetsCursor.getNext)
        datasets.append(StringBinding.entryToString(datasetsCursor.getKey))
      datasetsCursor.close() // TODO should use bracket
      datasets.toList
    }
    environment.computeInReadonlyTransaction(tc).map(Dataset.fromString(_).getOrElse(???))
  }

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] = IO {
    val tc: TransactionalComputable[Boolean] = tx => {
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val res = datasetToIdStore.get(tx, StringBinding.stringToEntry(dataset.name))
      res != null
    }
    environment.computeInReadonlyTransaction(tc)
  }

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Stream[IO, Dataset] =

    // read cursor in datasetNamesDB
    ???

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override def matchDatasetsRange(start: String, end: String): Stream[IO, Dataset] =
    // read cursor in datasetNamesDB
    ???

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(dataset: Dataset): IO[Unit] = IO {
    val tc: TransactionalComputable[Unit] = tx => {
      val nameEntry = StringBinding.stringToEntry(dataset.name)
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val datasetExists = datasetToIdStore.get(tx, nameEntry)
      if (datasetExists == null) {
        val nextId = nextID(tx)
        val idToDatasetStore = openStore(tx, LigatureStore.IdToDatasetStore)
        datasetToIdStore.put(tx, nameEntry, nextId)
        idToDatasetStore.putRight(tx, nextId, nameEntry)
      } else {
        ()
      }
    }
    environment.computeInTransaction(tc)
  }

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(dataset: Dataset): IO[Unit] =
    // check if dataset exists
    // remove datasetName
    // remove all Statements in Dataset
    ???

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override def query[T](dataset: Dataset)(fn: QueryTx => IO[T]): IO[T] =
    ???

  /** Initializes a WriteTx TODO should probably return its own error type
    * CouldNotInitializeWriteTx
    */
  override def write(dataset: Dataset)(fn: WriteTx => IO[Unit]): IO[Unit] =
    ???

  override def close(): IO[Unit] = IO {
    environment.close()
    ()
  }
}
