/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not diStringibuted with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Ligature, Dataset, Entity, Attribute, Value, QueryTx, WriteTx, LigatureError, Statement, PersistedStatement}
import cats.effect.IO
import fs2.Stream

/** A trait that all Ligature implementations implement. */
final class InMemoryLigature extends Ligature {
  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Stream[IO, Either[LigatureError, Dataset]] = {
    ???
  }

  /** Check if a given Dataset exists. */
  def datasetExists(dataset: Dataset): IO[Either[LigatureError, Boolean]] = {
    ???
  }

  /** Returns all Datasets in a Ligature instance that start with the given prefix. */
  def matchDatasetsPrefix(
    prefix: String,
  ): Stream[IO, Either[LigatureError, Dataset]] = {
    ???
  }

  /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
  def matchDatasetsRange(
    start: String,
    end: String,
  ): Stream[IO, Either[LigatureError, Dataset]] = {
    ???
  }

  /** Creates a dataset with the given name.
    * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
  def createDataset(dataset: Dataset): IO[Either[LigatureError, Unit]] = {
    ???
  }

  /** Deletes a dataset with the given name.
   * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
  def delete_dataset(dataset: Dataset): IO[Either[LigatureError, Unit]] = {
    ???
  }

  /** Initiazes a QueryTx
   * TODO should probably return its own error type CouldNotInitializeQueryTx */
  def query[T](dataset: Dataset, f: (QueryTx) => Either[LigatureError, T]): IO[Either[LigatureError, T]] = {
    ???
  }

  /** Initiazes a WriteTx
   * TODO should probably return its own error type CouldNotInitializeWriteTx */
  def write[T](dataset: Dataset, f: (WriteTx) => Either[LigatureError, T]): IO[Either[LigatureError, T]] = {
    ???
  }
}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
trait InMemoryQueryTx {
  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Stream[IO, Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all, so passing all Nones is the same as calling all_statements. */
  def matchStatements(
    source: Option[Entity],
    arrow: Option[Attribute],
    target: Option[Value],
  ): Stream[IO, Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Retuns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all. */
  def matchStatementsRange(
    source: Option[Entity],
    arrow: Option[Attribute],
    target: Range,
  ): Stream[IO, Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Returns the PersistedStatement for the given context. */
  def statementForContext(
    context: Entity,
  ): IO[Either[LigatureError, Option[PersistedStatement]]] = {
    ???
  }
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
trait InMemoryWriteTx {
  /** Creates a new, unique Entity within this Dataset.
   * Note: Entities are shared across named graphs in a given Dataset. */
  def newEntity(): Either[LigatureError, Entity] = {
    ???
  }

  /** Adds a given Statement to this Dataset.
   * If the Statement already exists nothing happens (TODO maybe add it with a new context?).
   * Note: Potentally could trigger a ValidationError */
  def addStatement(statement: Statement): Either[LigatureError, PersistedStatement] = {
    ???
  }

  /** Removes a given PersistedStatement from this Dataset.
   * If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
   * This function returns Ok(true) only if the given PersistedStatement was found and removed.
   * Note: Potentally could trigger a ValidationError. */
  def removeStatement(
    persisted_statement: PersistedStatement,
  ): Either[LigatureError, Boolean] = {
    ???
  }

  /** Cancels this transaction so that none of the changes made so far will be stored.
   * This also closes this transaction so no other methods can be called without returning a LigatureError. */
  def cancel(): Either[LigatureError, Unit] = {
    ???
  }
}
