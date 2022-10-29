/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import kotlin.jvm.JvmInline
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

val datasetPattern = Regex("^([a-zA-Z_][a-zA-Z0-9_]*)(/[a-zA-Z_][a-zA-Z0-9_]*)*$")
val identifierPattern = Regex("^[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]+$")

@JvmInline value class InvalidDataset(val invalidName: String)

@JvmInline
value class Dataset /*private constructor*/(val name: String) :
    Comparable<Dataset> { // TODO make constructor private
  override fun toString(): String = "Dataset($name)"
  override fun compareTo(other: Dataset): Int = this.name.compareTo(other.name)

  companion object {
    fun create(name: String): Either<InvalidDataset, Dataset> =
        if (name.matches(datasetPattern)) Right(Dataset(name)) else Left(InvalidDataset(name))
  }
}

@JvmInline value class InvalidIdentifier(val invalidName: String)

@JvmInline
value class Identifier(val name: String) :
    Comparable<Identifier>, Value { // TODO make constructor private
  override fun toString(): String = "Identifier($name)"
  override fun compareTo(other: Identifier): Int = this.name.compareTo(other.name)

  companion object {
    fun create(name: String): Either<InvalidIdentifier, Identifier> =
        if (name.matches(identifierPattern)) Right(Identifier(name))
        else Left(InvalidIdentifier(name))
  }
}

data class LigatureError(val message: String) // TODO make interface or abstract class?

sealed interface Value

data class StringLiteral(val value: String) : Value

data class IntegerLiteral(val value: Long) : Value

data class BytesLiteral(val value: ByteArray) : Value {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as BytesLiteral

    if (!value.contentEquals(other.value)) return false

    return true
  }

  override fun hashCode(): Int {
    return value.contentHashCode()
  }
}

// sealed trait Range
// final case class StringLiteralRange(start: String, end: String) extends Range
// final case class IntegerLiteralRange(start: Long, end: Long) extends Range

data class Statement(val entity: Identifier, val attribute: Identifier, val value: Value)

/** An interface that all Ligature implementations implement. */
interface Ligature {

  /** Returns all Datasets in a Ligature instance. */
  fun allDatasets(): Flow<Dataset>

  /** Check if a given Dataset exists. */
  suspend fun datasetExists(dataset: Dataset): Boolean

  /** Returns all Datasets in a Ligature instance that start with the given prefix. */
  fun matchDatasetsPrefix(prefix: String): Flow<Dataset>

  /**
   * Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive].
   */
  fun matchDatasetsRange(start: String, end: String): Flow<Dataset>

  /**
   * Creates a dataset with the given name. TODO should probably return its own error type {
   * InvalidDataset, DatasetExists, CouldNotCreateDataset }
   */
  suspend fun createDataset(dataset: Dataset): Unit

  /**
   * Deletes a dataset with the given name. TODO should probably return its own error type {
   * InvalidDataset, CouldNotDeleteDataset }
   */
  suspend fun deleteDataset(dataset: Dataset): Unit

  /**
   * Initializes a QueryTx TODO should probably return its own error type CouldNotInitializeQueryTx
   */
  suspend fun <T> query(dataset: Dataset, fn: suspend (QueryTx) -> T): T

  /**
   * Initializes a WriteTx TODO should probably return IO[Either] w/ its own error type
   * CouldNotInitializeWriteTx
   */
  suspend fun <T> write(dataset: Dataset, fn: suspend (WriteTx) -> T): T

  suspend fun close(): Unit
}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
interface QueryTx {

  /** Returns all Statements in this Dataset. */
  fun allStatements(): Flow<Statement>

  /**
   * Returns all Statements that match the given criteria. If a parameter is None then it matches
   * all, so passing all Nones is the same as calling allStatements.
   */
  fun matchStatements(
      entity: Identifier? = null,
      attribute: Identifier? = null,
      value: Value? = null
  ): Flow<Statement>

  //  /** Returns all Statements that match the given criteria. If a
  //    * parameter is None then it matches all.
  //    */
  //  def matchStatementsRange(
  //      entity: Option[Identifier] = None,
  //      attribute: Option[Identifier] = None,
  //      value: Range
  //  ): Stream[IO, Statement]
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
interface WriteTx {

  /**
   * Creates a new, unique Entity within this Dataset by combining a UUID and an optional prefix.
   * Note: Entities are shared across named graphs in a given Dataset.
   */
  suspend fun newIdentifier(prefix: String = ""): Identifier

  /** Adds a given Statement to this Dataset. If the Statement already exists nothing happens. */
  suspend fun addStatement(statement: Statement): Unit

  /**
   * Removes a given Statement from this Dataset. If the Statement doesn't exist nothing happens and
   * returns Ok(false). This function returns Ok(true) only if the given Statement was found and
   * removed.
   */
  suspend fun removeStatement(statement: Statement): Unit
}
