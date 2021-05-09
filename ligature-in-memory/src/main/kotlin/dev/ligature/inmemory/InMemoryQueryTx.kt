/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import arrow.core.Either
import dev.ligature.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
class InMemoryQueryTx(private val store: DatasetStore) : QueryTx {
    /** Returns all PersistedStatements in this Dataset. */
    override suspend fun allStatements(): Flow<Either<LigatureError, Statement>> {
        return store.statements.map { Either.Right(it) }.asFlow()
    }

    /** Returns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
    override suspend fun matchStatements(
            entity: Entity?,
            attribute: Attribute?,
            value: Value?,
            context: Entity?
    ): Flow<Either<LigatureError, Statement>> {
        var res = store.statements.asFlow()
        if (entity != null) {
            res = res.filter { it.entity == entity }
        }
        if (attribute != null) {
            res = res.filter { it.attribute == attribute }
        }
        if (value != null) {
            res = res.filter { it.value == value }
        }
        if (context != null) {
            res = res.filter { it.context == context }
        }
        return res.map { Either.Right(it) }
    }

    /** Retuns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all. */
    override suspend fun matchStatementsRange(
            entity: Entity?,
            attribute: Attribute?,
            range: Range,
            context: Entity?
    ): Flow<Either<LigatureError, Statement>> {
        var res = store.statements.asFlow()
        if (entity != null) {
            res = res.filter { it.entity == entity }
        }
        if (attribute != null) {
            res = res.filter { it.attribute == attribute }
        }
        res = res.filter { s ->
            val value = s.value
            when {
                value is StringLiteral && range is StringLiteralRange   -> value.value >= range.start && value.value < range.end
                value is FloatLiteral && range is FloatLiteralRange     -> value.value >= range.start && value.value < range.end
                value is IntegerLiteral && range is IntegerLiteralRange -> value.value >= range.start && value.value < range.end
                else                                                    -> false
            }
        }
        if (context != null) {
            res = res.filter { it.context == context }
        }
        return res.map { Either.Right(it) }
    }
}
