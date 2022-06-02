/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.{Dataset, Identifier, Ligature, LigatureError, QueryTx, Statement, Value, WriteTx}
import cats.effect.IO
import fs2.Stream

import scala.collection.immutable.TreeMap
import cats.effect.kernel.Resource
import cats.effect.kernel.Ref

import java.io.File
import jetbrains.exodus.env.{EnvironmentConfig, Environments, ReadonlyTransaction, Transaction, TransactionalComputable}
import scala.jdk.CollectionConverters._

final class XodusLigature(dbDirectory: File) extends Ligature {
  private val environment = Environments.newInstance(dbDirectory, new EnvironmentConfig())

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = Stream.emits {
    val tc: TransactionalComputable[java.util.List[String]] = tx => environment.getAllStoreNames(tx)
    environment.computeInReadonlyTransaction(tc).asScala.map(Dataset.fromString(_).getOrElse(???))
  }

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] = IO {
    val tc: TransactionalComputable[Boolean] = tx => environment.storeExists(dataset.name, tx)
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
    val tc: TransactionalComputable[Unit] = tx => {
      if (environment.storeExists(dataset.name, tx)) {
        ()
      } else {
        PersistentEntityStores.newInstance(environment, dataset.name).close()
        environment.getAllStoreNames(tx).forEach(println(_))
      }
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
