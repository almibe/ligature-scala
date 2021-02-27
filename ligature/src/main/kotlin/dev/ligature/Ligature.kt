/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import kotlinx.coroutines.flow.Flow

data class Dataset(val name: String): Comparable<Dataset> {
    override fun compareTo(other: Dataset): Int = name.compareTo(other.name)
}

//final case class Dataset private (val name: String) extends Ordered[Dataset] {
//    private def copy(): Unit = ()
//
//    override def compare(that: Dataset): Int = this.name.compare(that.name)
//}
//
//object Dataset {
//    private def apply(name: String): Dataset = ???
//
//    private val pattern = "^([a-zA-Z_]{1}[a-zA-Z0-9_]*)(/[a-zA-Z_]{1}[a-zA-Z0-9_]*)*$".r
//
//    def fromString(name: String): Option[Dataset] = {
//        if (pattern.matches(name)) {
//            Some(new Dataset(name))
//        } else {
//            None
//        }
//    }
//}

data class Entity(val id: Long): Value()

data class Attribute(val name: String)

//data class Attribute private (val name: String) {
//    private def copy(): Unit = ()
//}
//
//object Attribute {
//    private def apply(name: String): Attribute = ???
//
//    private val pattern = "^[a-zA-Z_]{1}[a-zA-Z0-9_]*$".r
//
//    def fromString(name: String): Option[Attribute] = {
//        if (pattern.matches(name)) {
//            Some(new Attribute(name))
//        } else {
//            None
//        }
//    }
//}

data class LigatureError(val message: String)

sealed class Value() //TODO make a sealed interface later
data class StringLiteral(val value: String): Value()
data class IntegerLiteral(val value: Long): Value()
data class FloatLiteral(val value: Double): Value()

sealed class Range //TODO make a sealed interface later
data class StringLiteralRange(val start: String, val end: String): Range()
data class IntegerLiteralRange(val start: Long, val end: Long): Range()
data class FloatLiteralRange(val start: Double, val end: Double): Range()

data class Statement(val entity: Entity, val attribute: Attribute, val value: Value)

data class PersistedStatement(val statement: Statement, val context: Entity)

/** A interface that all Ligature implementations implement. */
interface Ligature {
    /** Returns all Datasets in a Ligature instance. */
    suspend fun allDatasets(): Flow<Result<Dataset>>

    /** Check if a given Dataset exists. */
    suspend fun datasetExists(dataset: Dataset): Result<Boolean>

    /** Returns all Datasets in a Ligature instance that start with the given prefix. */
    suspend fun matchDatasetsPrefix(
            prefix: String,
    ): Flow<Result<Dataset>>

    /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
    suspend fun matchDatasetsRange(
            start: String,
    end: String,
    ): Flow<Result<Dataset>>

    /** Creates a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
    suspend fun createDataset(dataset: Dataset): Result<Unit>

    /** Deletes a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
    suspend fun deleteDataset(dataset: Dataset): Result<Unit>

    /** Initiazes a QueryTx
     * TODO should probably return its own error type CouldNotInitializeQueryTx */
    suspend fun <T> query(dataset: Dataset, fn: suspend (QueryTx) -> T): T

    /** Initiazes a WriteTx
     * TODO should probably return its own error type CouldNotInitializeWriteTx */
    suspend fun <T> write(dataset: Dataset, fn: suspend (WriteTx) -> T): T
}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
interface QueryTx {
    /** Returns all PersistedStatements in this Dataset. */
    suspend fun allStatements(): Flow<Result<PersistedStatement>>

    /** Returns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
    suspend fun matchStatements(
            entity: Entity?,
            attribute: Attribute?,
            value: Value?,
    ): Flow<Result<PersistedStatement>>

    /** Retuns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all. */
    suspend fun matchStatementsRange(
            entity: Entity?,
            attribute: Attribute?,
            range: Range,
    ): Flow<Result<PersistedStatement>>

    /** Returns the PersistedStatement for the given context. */
    suspend fun statementForContext(
            context: Entity,
    ): Result<PersistedStatement?>
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
interface WriteTx {
    /** Creates a new, unique Entity within this Dataset.
     * Note: Entities are shared across named graphs in a given Dataset. */
    suspend fun newEntity(): Result<Entity>

    /** Adds a given Statement to this Dataset.
     * If the Statement already exists nothing happens (TODO maybe add it with a new context?).
     * Note: Potentally could trigger a ValidationError */
    suspend fun addStatement(statement: Statement): Result<PersistedStatement>

    /** Removes a given PersistedStatement from this Dataset.
     * If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
     * This function returns Ok(true) only if the given PersistedStatement was found and removed.
     * Note: Potentally could trigger a ValidationError. */
    suspend fun removeStatement(
            persistedStatement: PersistedStatement,
    ): Result<Boolean>

    /** Cancels this transaction so that none of the changes made so far will be stored.
     * This also closes this transaction so no other methods can be called without returning a LigatureError. */
    suspend fun cancel(): Result<Unit>
}
