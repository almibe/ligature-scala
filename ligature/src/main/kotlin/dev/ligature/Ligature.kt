/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import dev.ligature.rakkoon.Rakkoon
import kotlinx.coroutines.flow.Flow
import arrow.core.*
import dev.ligature.rakkoon.Cancel
import dev.ligature.rakkoon.Complete
import dev.ligature.rakkoon.Nibbler

@OptIn(ExperimentalUnsignedTypes::class)
val datasetNibbler = Nibbler {
    when (it.peek(0U)) {
        in 'a'..'z', in 'A'..'Z', '_' -> {
            var offset = 1U
            while (it.peek(offset) != null) {
                when (it.peek(offset)) {
                    in 'a'..'z', in 'A'..'Z', in '0'..'9', '_' -> {
                        offset++
                    }
                    else -> break
                }
            }
            Complete(offset.toInt())
        }
        else -> Cancel
    }
}

private val identifierSpecialChars = listOf('-', '.', '_', '~', ':', '/', '?', '#', '[', ']', '@', '!', '$', '&', '\'',
    '(', ')', '*', '+', ',', ';', '%', '=')

@OptIn(ExperimentalUnsignedTypes::class)
val identifierNibbler = Nibbler {
    when (it.peek(0U)) {
        in 'a'..'z', in 'A'..'Z', '_' -> {
            var offset = 1U
            while (it.peek(offset) != null) {
                when (it.peek(offset)) {
                    in 'a'..'z', in 'A'..'Z', in '0'..'9', in identifierSpecialChars -> {
                        offset++
                    }
                    else -> break
                }
            }
            Complete(offset.toInt())
        }
        else -> Cancel
    }
}

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
        /**
         * Attempts to create a Dataset with the given name.
         * If the entire String is a valid Dataset name then Some(dataset) is returned otherwise None is.
         */
        fun from(name: String): Option<Dataset> =
            when (val dataset = from(Rakkoon(name))) {
                is None -> dataset
                is Some -> if (dataset.value.name == name) dataset else none()
            }

        private fun from(rakkoon: Rakkoon): Option<Dataset> {
            val res = rakkoon.nibble(datasetNibbler)
            return res.map { Dataset(it.value) }
        }
    }
}

@JvmInline
value class Identifier private constructor(val id: String): Value {
    companion object {
        operator fun invoke(id: String): Option<Identifier> = //TODO should be an Either or a Result not Option
            when (val identifier = from(Rakkoon(id))) {
                is None -> identifier
                is Some -> if (identifier.value.id == id) identifier else none()
            }

        private fun from(rakkoon: Rakkoon): Option<Identifier> {
            val res = rakkoon.nibble(identifierNibbler)
            return res.map { Identifier(it.value) }
        }

    }
}

//class Entity private constructor(val id: String): Value() {
//    override fun equals(other: Any?): Boolean {
//        return if (other is Entity) {
//            this.id == other.id
//        } else {
//            false
//        }
//    }
//
//    override fun hashCode(): Int = id.hashCode()
//
//    override fun toString(): String = "Entity(id=$id)"
//
//    operator fun component1(): String = id
//
//    companion object {
//        /**
//         * Attempts to create an Entity with the given name.
//         * If the entire String is a valid Entity identifier then Some(entity) is returned otherwise None is.
//         */
//        fun from(id: String): Option<Entity> =
//            when (val entity = from(Rakkoon(id))) {
//                is None -> entity
//                is Some -> if (entity.value.id == id) entity else none()
//            }
//
//        private fun from(rakkoon: Rakkoon): Option<Entity> {
//            val res = rakkoon.nibble(identifierNibbler)
//            return res.map { Entity(it.value) }
//        }
//    }
//}
//
//class Attribute private constructor(val name: String) {
//    override fun equals(other: Any?): Boolean {
//        return if (other is Attribute) {
//            this.name == other.name
//        } else {
//            false
//        }
//    }
//
//    override fun hashCode(): Int = name.hashCode()
//
//    override fun toString(): String = "Attribute(name=$name)"
//
//    operator fun component1(): String = name
//
//    companion object {
//        /**
//         * Attempts to create an Attribute with the given name.
//         * If the entire String is a valid Attribute name then Some(attribute) is returned otherwise None is.
//         */
//        fun from(name: String): Option<Attribute> =
//            when (val attribute = from(Rakkoon(name))) {
//                is None -> attribute
//                is Some -> if (attribute.value.name == name) attribute else none()
//            }
//
//        private fun from(rakkoon: Rakkoon): Option<Attribute> {
//            val res = rakkoon.nibble(identifierNibbler)
//            return res.map { Attribute(it.value) }
//        }
//    }
//}

data class LigatureError(val message: String)

sealed interface Value //TODO make a sealed interface later
data class StringLiteral(val value: String): Value
data class IntegerLiteral(val value: Long): Value
//data class FloatLiteral(val value: Double): Value()

sealed class Range //TODO make a sealed interface later
data class StringLiteralRange(val start: String, val end: String): Range()
data class IntegerLiteralRange(val start: Long, val end: Long): Range()
//data class FloatLiteralRange(val start: Double, val end: Double): Range()

data class Statement(val entity: Identifier, val attribute: Identifier, val value: Value, val context: Identifier)

/** A interface that all Ligature implementations implement. */
interface Ligature {
    /** Returns all Datasets in a Ligature instance. */
    suspend fun allDatasets(): Flow<Either<LigatureError, Dataset>>

    /** Check if a given Dataset exists. */
    suspend fun datasetExists(dataset: Dataset): Either<LigatureError, Boolean>

    /** Returns all Datasets in a Ligature instance that start with the given prefix. */
    suspend fun matchDatasetsPrefix(
            prefix: String,
    ): Flow<Either<LigatureError, Dataset>>

    /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
    suspend fun matchDatasetsRange(
            start: String,
            end: String,
    ): Flow<Either<LigatureError, Dataset>>

    /** Creates a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
    suspend fun createDataset(dataset: Dataset): Either<LigatureError, Unit>

    /** Deletes a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
    suspend fun deleteDataset(dataset: Dataset): Either<LigatureError, Unit>

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
    suspend fun allStatements(): Flow<Either<LigatureError, Statement>>

    /** Returns all Statements that match the given criteria.
     * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
    suspend fun matchStatements(
            entity: Identifier? = null,
            attribute: Identifier? = null,
            value: Value? = null,
            context: Identifier? = null
    ): Flow<Either<LigatureError, Statement>>

    /** Returns all Statements that match the given criteria.
     * If a parameter is None then it matches all. */
    suspend fun matchStatementsRange(
            entity: Identifier?,
            attribute: Identifier?,
            range: Range,
            context: Identifier? = null
    ): Flow<Either<LigatureError, Statement>>
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
interface WriteTx {
    /** Creates a new, unique Identifier within this Dataset by combining a UUID and an optional prefix.
     * Note: Entities are shared across named graphs in a given Dataset. */
    suspend fun generateIdentifier(prefix: String = "_"): Either<LigatureError, Identifier>

    /** Adds a given Statement to this Dataset.
     * If the Statement already exists nothing happens.
     * Note: Potentially could trigger a ValidationError */
    suspend fun addStatement(statement: Statement): Either<LigatureError, Unit>

    /** Removes a given Statement from this Dataset.
     * If the Statement doesn't exist nothing happens and returns Ok(false).
     * This function returns Ok(true) only if the given Statement was found and removed.
     * Note: Potentially could trigger a ValidationError. */
    suspend fun removeStatement(
            statement: Statement,
    ): Either<LigatureError, Boolean>

    /** Cancels this transaction so that none of the changes made so far will be stored.
     * This also closes this transaction so no other methods can be called without returning a LigatureError. */
    suspend fun cancel(): Either<LigatureError, Unit>
}
