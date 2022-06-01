/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lmdb

import dev.ligature.{
  Dataset,
  Identifier,
  Ligature,
  LigatureError,
  QueryTx,
  Statement,
  Value,
  WriteTx
}
import cats.effect.IO
import fs2.Stream
import scala.collection.immutable.TreeMap
import org.lmdbjava.Env.create
import org.lmdbjava.DbiFlags.MDB_CREATE
import cats.effect.kernel.Resource
import cats.effect.kernel.Ref
import java.io.File

final class LmdbLigature(dbFile: File) extends Ligature {
  private val env = create().setMapSize(10_458_760).setMaxDbs(11).open(dbFile)
  private val countersDB = env.openDbi("Counters", MDB_CREATE)
  private val datasetNameDB = env.openDbi("DatasetNames", MDB_CREATE)
  private val identifiersDB = env.openDbi("Identifiers", MDB_CREATE)
  private val stringValuesDB = env.openDbi("StringValues", MDB_CREATE)
  private val bytesValuesDB = env.openDbi("BytesValues", MDB_CREATE)
  // Statements <- One DB to start with then eventually add indexed ones
  private val statementsDB = env.openDbi("Statements", MDB_CREATE)

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = {
    //read all keys from datasetNameDB
    ???
  }

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] = IO {
    //check if key exists in datasetNameDB
    ???
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
  override def createDataset(dataset: Dataset): IO[Unit] =
    //check if dataset exists
    //get next id
    //store dataset in datasetNamesDB
    ???

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
    env.close()
    ()
  }
}
