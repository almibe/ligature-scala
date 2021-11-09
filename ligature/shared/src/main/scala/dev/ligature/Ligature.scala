/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import fs2.Stream
import cats.effect.IO
import cats.data.EitherT
import cats.effect.kernel.Resource

final case class Dataset private (val name: String) extends Ordered[Dataset] {
  private def copy(): Unit = ()

  override def compare(that: Dataset): Int = this.name.compare(that.name)
}

object Dataset {
  private val pattern = "^([a-zA-Z_][a-zA-Z0-9_]*)(/[a-zA-Z_][a-zA-Z0-9_]*)*$".r

  def fromString(name: String): Either[LigatureError, Dataset] = {
    if (pattern.matches(name)) {
      Right(Dataset(name))
    } else {
      Left(LigatureError(s"Could not make Dataset from $name"))
    }
  }
}

final case class Identifier private (val name: String) extends Value {
  private def copy(): Unit = ()
}

object Identifier {
  private val pattern = "^[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]+$".r

  def fromString(name: String): Either[LigatureError, Identifier] = {
    if (pattern.matches(name)) {
      Right(Identifier(name))
    } else {
      Left(LigatureError(s"Invalid Identifier $name"))
    }
  }
}

final case class LigatureError(val message: String)

sealed trait Value
final case class StringLiteral(val value: String) extends Value
final case class IntegerLiteral(val value: Long) extends Value

sealed trait Range
final case class StringLiteralRange(val start: String, val end: String) extends Range
final case class IntegerLiteralRange(val start: Long, val end: Long) extends Range

final case class Statement(val entity: Identifier, val attribute: Identifier, val value: Value, val context: Identifier)

enum WriteResult:
  case WriteError(val message: String)
  case WriteSuccess

/** A trait that all Ligature implementations implement. */
trait Ligature {
  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Stream[IO, Dataset]

  /** Check if a given Dataset exists. */
  def datasetExists(dataset: Dataset): IO[Boolean]

  /** Returns all Datasets in a Ligature instance that start with the given prefix. */
  def matchDatasetsPrefix(
                           prefix: String,
                         ): Stream[IO, Dataset]

  /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
  def matchDatasetsRange(
                          start: String,
                          end: String,
                        ): Stream[IO, Dataset]

  /** Creates a dataset with the given name.
   * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
  def createDataset(dataset: Dataset): IO[Unit]

  /** Deletes a dataset with the given name.
   * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
  def deleteDataset(dataset: Dataset): IO[Unit]

  /** Initializes a QueryTx
   * TODO should probably return its own error type CouldNotInitializeQueryTx */
  def query(dataset: Dataset): Resource[IO, QueryTx]

  /** Initializes a WriteTx
   * TODO should probably return its own error type CouldNotInitializeWriteTx */
  def write(dataset: Dataset): Resource[IO, WriteTx] //TODO maybe switch back to WriteResult

  def close(): IO[Unit]
}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
trait QueryTx {
  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Stream[IO, Statement]

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
  def matchStatements(
                       entity: Option[Identifier],
                       attribute: Option[Identifier],
                       value: Option[Value]
                     ): Stream[IO, Statement]

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all. */
  def matchStatementsRange(
                            entity: Option[Identifier],
                            attribute: Option[Identifier],
                            value: Range
                          ): Stream[IO, Statement]

  /** Returns the PersistedStatement for the given context. */
  def statementForContext(
                           context: Identifier,
                         ): EitherT[IO, LigatureError, Option[Statement]]
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
trait WriteTx {
  /** Creates a new, unique Entity within this Dataset by combining a UUID and an optional prefix.
   * Note: Entities are shared across named graphs in a given Dataset. */
  def newIdentifier(prefix: String = ""): IO[Identifier]

  /** Adds a given Statement to this Dataset.
   * If the Statement already exists nothing happens (TODO maybe add it with a new context?).
   * Note: Potentially could trigger a ValidationError if the Statement's Context already exists
   * for a different Statement. */
  def addStatement(statement: Statement): IO[Unit]

  /** Removes a given PersistedStatement from this Dataset.
   * If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
   * This function returns Ok(true) only if the given PersistedStatement was found and removed. */
  def removeStatement(
                       statement: Statement,
                     ): IO[Unit]
}
