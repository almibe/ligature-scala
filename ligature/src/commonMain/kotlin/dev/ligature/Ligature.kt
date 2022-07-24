/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import arrow.core.Option
import arrow.core.none
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class Dataset(val name: String): Comparable<Dataset> {
  private val pattern = Regex("^([a-zA-Z_][a-zA-Z0-9_]*)(/[a-zA-Z_][a-zA-Z0-9_]*)*$")

  override fun compareTo(that: Dataset): Int = this.name.compareTo(that.name)

//  fun fromString(name: String): Either[LigatureError, Dataset] =
//    if (pattern.matches(name)) {
//      Right(Dataset(name))
//    } else {
//      Left(LigatureError(s"Could not make Dataset from $name"))
//    }
}
data class Identifier(val name: String): Value {
  private val pattern = Regex("^[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]+$")

//  def fromString(name: String): Either[LigatureError, Identifier] =
//  if (pattern.matches(name)) {
//    Right(Identifier(name))
//  } else {
//    Left(LigatureError(s"Invalid Identifier $name"))
//  }
}

data class LigatureError(val message: String)

sealed interface Value
data class StringLiteral(val value: String): Value
data class IntegerLiteral(val value: Long): Value

//sealed trait Range
//final case class StringLiteralRange(start: String, end: String) extends Range
//final case class IntegerLiteralRange(start: Long, end: Long) extends Range

data class Statement(
    val entity: Identifier,
    val attribute: Identifier,
    val value: Value
)

/** An interface that all Ligature implementations implement. */
interface Ligature {

  /** Returns all Datasets in a Ligature instance. */
  fun allDatasets(): Flow<Dataset>

  /** Check if a given Dataset exists. */
  suspend fun datasetExists(dataset: Dataset): Boolean

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  fun matchDatasetsPrefix(
      prefix: String
  ): Flow<Dataset>

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  fun matchDatasetsRange(
      start: String,
      end: String
  ): Flow<Dataset>

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  suspend fun createDataset(dataset: Dataset): Unit

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  suspend fun deleteDataset(dataset: Dataset): Unit

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  suspend fun <T>query(dataset: Dataset, fn: suspend (QueryTx) -> T): T

  /** Initializes a WriteTx TODO should probably return IO[Either] w/ its own
    * error type CouldNotInitializeWriteTx
    */
  suspend fun <T>write(dataset: Dataset, fn: suspend (WriteTx) -> T): T

  suspend fun close(): Unit
}

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
interface QueryTx {

  /** Returns all PersistedStatements in this Dataset. */
  fun allStatements(): Flow<Statement>

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  fun matchStatements(
      entity: Option<Identifier> = none(),
      attribute: Option<Identifier> = none(),
      value: Option<Value> = none()
  ): Flow<Statement>

//  /** Returns all PersistedStatements that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  def matchStatementsRange(
//      entity: Option[Identifier] = None,
//      attribute: Option[Identifier] = None,
//      value: Range
//  ): Stream[IO, Statement]
}

/** Represents a WriteTx within the context of a Ligature instance and a single
  * Dataset
  */
interface WriteTx {

  /** Creates a new, unique Entity within this Dataset by combining a UUID and
    * an optional prefix. Note: Entities are shared across named graphs in a
    * given Dataset.
    */
  suspend fun newIdentifier(prefix: String = ""): Identifier

  /** Adds a given Statement to this Dataset. If the Statement already exists
    * nothing happens.
    */
  suspend fun addStatement(statement: Statement): Unit

  /** Removes a given PersistedStatement from this Dataset. If the
    * PersistedStatement doesn't exist nothing happens and returns Ok(false).
    * This function returns Ok(true) only if the given PersistedStatement was
    * found and removed.
    */
  suspend fun removeStatement(
      statement: Statement
  ): Unit
}
