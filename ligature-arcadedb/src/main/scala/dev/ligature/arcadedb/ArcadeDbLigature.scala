/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.arcadedb

import dev.ligature.{Dataset, Identifier, Ligature, LigatureError, QueryTx, Statement, Value}
import cats.effect.IO
import cats.effect.std.AtomicCell
import fs2.Stream
import scala.collection.immutable.TreeMap
import cats.effect.kernel.Resource

protected case class DatasetStore(counter: Long, statements: Set[Statement])

enum Location:
  case Temporary
  case Path(path: String)

def openLigature(location: Location): Resource[IO, ArcadeDbLigature] =
  Resource.make {
    location match
      case Temporary => ???
      case Path(path) =>
        val factory = DatabaseFactory(path)

        ???
  }(_.close())

final class ArcadeDbLigature(location: Location) extends Ligature {

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = ???

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] = ???

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Stream[IO, Dataset] = ???

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override def matchDatasetsRange(
      start: String,
      end: String
  ): Stream[IO, Dataset] = ???

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(dataset: Dataset): IO[Unit] = ???

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(dataset: Dataset): IO[Unit] = ???

  override def allStatements(dataset: Dataset): Stream[IO, Statement] = ???

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override def query[T](dataset: Dataset)(fn: QueryTx => IO[T]): IO[T] = ???

  override def addStatements(
      dataset: Dataset,
      statements: Stream[cats.effect.IO, Statement]
  ): IO[Unit] = ???

  override def removeStatements(
      dataset: Dataset,
      statements: Stream[cats.effect.IO, Statement]
  ): IO[Unit] = ???

  override def close(): IO[Unit] = ???
}
