/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import fs2.Stream
import cats.effect.IO

import scala.annotation.unused

final case class Dataset private (name: String) extends Ordered[Dataset] {
  @unused
  private def copy(): Unit = ()

  override def compare(that: Dataset): Int = this.name.compare(that.name)
}

object Dataset {
  private val pattern = "^([a-zA-Z_][a-zA-Z0-9_]*)(/[a-zA-Z_][a-zA-Z0-9_]*)*$".r

  def fromString(name: String): Either[LigatureError, Dataset] =
    if (pattern.matches(name)) {
      Right(Dataset(name))
    } else {
      Left(LigatureError(s"Could not make Dataset from $name"))
    }
}

final case class Identifier private (name: String) {
  @unused
  private def copy(): Unit = ()
}

object Identifier {
  private val pattern = "^[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]+$".r

  def fromString(name: String): Either[LigatureError, Identifier] =
    if (pattern.matches(name)) {
      Right(Identifier(name))
    } else {
      Left(LigatureError(s"Invalid Identifier $name"))
    }
}

case class LigatureError(val userMessage: String) extends Throwable

enum LigatureLiteral:
  case StringLiteral(value: String)
  case IntegerLiteral(value: Long)

type Value = LigatureLiteral | Identifier

//sealed trait Range
//final case class StringLiteralRange(start: String, end: String) extends Range
//final case class IntegerLiteralRange(start: Long, end: Long) extends Range

final case class Statement(
    entity: Identifier,
    attribute: Identifier,
    value: Value
)

/** A trait that all Ligature implementations implement. */
trait Ligature {

  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Stream[IO, Dataset]

  /** Check if a given Dataset exists. */
  def datasetExists(dataset: Dataset): IO[Boolean]

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  def matchDatasetsPrefix(
      prefix: String
  ): Stream[IO, Dataset]

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  def matchDatasetsRange(
      start: String,
      end: String
  ): Stream[IO, Dataset]

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  def createDataset(dataset: Dataset): IO[Unit]

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  def deleteDataset(dataset: Dataset): IO[Unit]

  def allStatements(dataset: Dataset): Stream[IO, Statement]

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  def query[T](dataset: Dataset)(fn: QueryTx => IO[T]): IO[T]

  def addStatements(dataset: Dataset, statements: Stream[IO, Statement]): IO[Unit]

  def removeStatements(dataset: Dataset, statements: Stream[IO, Statement]): IO[Unit]

  def close(): IO[Unit]
}

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
trait QueryTx {
  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  def matchStatements(
      entity: Option[Identifier] = None,
      attribute: Option[Identifier] = None,
      value: Option[Value] = None
  ): Stream[IO, Statement]

//  /** Returns all PersistedStatements that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  def matchStatementsRange(
//      entity: Option[Identifier] = None,
//      attribute: Option[Identifier] = None,
//      value: Range
//  ): Stream[IO, Statement]
}
