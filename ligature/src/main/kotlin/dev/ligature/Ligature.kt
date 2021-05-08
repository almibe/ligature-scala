/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import dev.ligature.rakkoon.Rakkoon
import kotlinx.coroutines.flow.Flow
import arrow.core.*

class Dataset private constructor(val name: String): Comparable<Dataset> {
    override fun compareTo(other: Dataset): Int = name.compareTo(other.name)

    override fun equals(other: Any?): Boolean {
        return if (other is Dataset) {
            this.name == other.name
        } else {
            false
        }
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "Dataset(name=$name)"

    operator fun component1(): String = name

    companion object {
        fun from(name: String): Option<Dataset> = from(Rakkoon(name))

        fun from(rakkoon: Rakkoon): Option<Dataset> {
            val res = rakkoon.nibble { lookAhead ->
                //private val pattern = "^([a-zA-Z_]{1}[a-zA-Z0-9_]*)(/[a-zA-Z_]{1}[a-zA-Z0-9_]*)*$".r
                TODO()
            }
            return res.map { Dataset(it.value) }
        }
    }
}

class Entity private constructor(val id: String): Value() {
    override fun equals(other: Any?): Boolean {
        return if (other is Entity) {
            this.id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Entity(id=$id)"

    operator fun component1(): String = id

    companion object {
        fun from(name: String): Option<Entity> = from(Rakkoon(name))

        fun from(rakkoon: Rakkoon): Option<Entity> {
            val res = rakkoon.nibble { lookAhead ->
                TODO()
            }
            return res.map { Entity(it.value) }
        }
    }
}

class Attribute private constructor(val name: String) {
    override fun equals(other: Any?): Boolean {
        return if (other is Attribute) {
            this.name == other.name
        } else {
            false
        }
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "Attribute(name=$name)"

    operator fun component1(): String = name

    companion object {
        fun from(name: String): Option<Attribute> = from(Rakkoon(name))

        fun from(rakkoon: Rakkoon): Option<Attribute> {
            val res = rakkoon.nibble { lookAhead ->
                TODO()
            }
            return res.map { Attribute(it.value) }
        }
    }
}

data class LigatureError(val message: String)

sealed class Value() //TODO make a sealed interface later
data class StringLiteral(val value: String): Value()
data class IntegerLiteral(val value: Long): Value()
data class FloatLiteral(val value: Double): Value()

sealed class Range //TODO make a sealed interface later
data class StringLiteralRange(val start: String, val end: String): Range()
data class IntegerLiteralRange(val start: Long, val end: Long): Range()
data class FloatLiteralRange(val start: Double, val end: Double): Range()

data class Statement(val entity: Entity, val attribute: Attribute, val value: Value, val context: Entity)

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
    /** Returns all Statements in this Dataset. */
    suspend fun allStatements(): Flow<Result<Statement>>

    /** Returns all Statements that match the given criteria.
     * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
    suspend fun matchStatements(
            entity: Entity? = null,
            attribute: Attribute? = null,
            value: Value? = null,
            context: Entity? = null
    ): Flow<Result<Statement>>

    /** Returns all Statements that match the given criteria.
     * If a parameter is None then it matches all. */
    suspend fun matchStatementsRange(
            entity: Entity?,
            attribute: Attribute?,
            range: Range,
            context: Entity? = null
    ): Flow<Result<Statement>>
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
interface WriteTx {
    /** Creates a new, unique Entity within this Dataset by combining a UUID and an optional prefix.
     * Note: Entities are shared across named graphs in a given Dataset. */
    suspend fun newAnonymousEntity(prefix: String = ""): Result<Entity>

    /** Adds a given Statement to this Dataset.
     * If the Statement already exists nothing happens.
     * Note: Potentially could trigger a ValidationError */
    suspend fun addStatement(statement: Statement): Result<Unit>

    /** Removes a given Statement from this Dataset.
     * If the Statement doesn't exist nothing happens and returns Ok(false).
     * This function returns Ok(true) only if the given Statement was found and removed.
     * Note: Potentially could trigger a ValidationError. */
    suspend fun removeStatement(
            statement: Statement,
    ): Result<Boolean>

    /** Cancels this transaction so that none of the changes made so far will be stored.
     * This also closes this transaction so no other methods can be called without returning a LigatureError. */
    suspend fun cancel(): Result<Unit>
}
