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

import java.io.File
import jetbrains.exodus.env.{EnvironmentConfig, Environments, ReadonlyTransaction, Transaction, TransactionalComputable, Store, StoreConfig}
import scala.jdk.CollectionConverters._

final class XodusLigature(dbDirectory: File) extends Ligature {
  private val environment = Environments.newInstance(dbDirectory, new EnvironmentConfig())
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

  /**
   * This method is ran once at the start to make sure all Stores exist.
   * This is done so that Stores can be opened in readonly mode.
   * TODO: This method could probably check if the Environment exists and check the status of the Environment first.
   */
  private def setupStores(): Unit = {
    val tc: TransactionalComputable[Unit] = tx => {
      LigatureStore.values.foreach(openStore(tx, _))
    }
    environment.computeInTransaction(tc)
  }

  /**
   * Used to uniformly open Stores.
   * This is mainly so that StoreConfigs can be consistent.
   * Right now all stores use the same config but that is likely to change.
   */
  private def openStore(tx: Transaction, store: LigatureStore): Store = {
    environment.openStore(store.storeName, StoreConfig.WITHOUT_DUPLICATES, tx)
  }

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = Stream.emits {
    val tc: TransactionalComputable[List[String]] = tx => {
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val datasetsCursor = datasetToIdStore.openCursor(tx)
      val datasets: ListBuffer[String] = ListBuffer()
      while (datasetsCursor.getNext) {
        datasets.append(datasetsCursor.getKey.toString)
      }
      datasetsCursor.close() //TODO should use bracket
      datasets.toList
    }
    environment.computeInReadonlyTransaction(tc).map(Dataset.fromString(_).getOrElse(???))
  }

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] = IO {
    val tc: TransactionalComputable[Boolean] = tx => {
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      ???
      //datasetToIdStore.get(tx, )
    }
    environment.computeInReadonlyTransaction(tc)
  }

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Stream[IO, Dataset] =
    //read cursor in datasetNamesDB
    ???

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override def matchDatasetsRange(start: String, end: String): Stream[IO, Dataset] =
    //read cursor in datasetNamesDB
    ???

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(dataset: Dataset): IO[Unit] = IO {
    //TODO open DatasetToIdStore
    //TODO check if Dataset exists if so return
    //TODO if not open IdToDatasetStore and CounterStore
    //TODO get next id
    val tc: TransactionalComputable[Unit] = tx => {
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      ???
      //datasetToIdStore.get(tx, )
    }
    environment.computeInTransaction(tc)
  }

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(dataset: Dataset): IO[Unit] =
    //check if dataset exists
    //remove datasetName
    //remove all Statements in Dataset
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
