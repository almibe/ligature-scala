/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.Result.Companion.success

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
class InMemoryQueryTx(private val store: DatasetStore) : QueryTx {
    /** Returns all PersistedStatements in this Dataset. */
    override suspend fun allStatements(): Flow<Result<PersistedStatement>> {
        return store.statements.map { success(it) }.asFlow()
    }

    /** Returns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
    override suspend fun matchStatements(
            entity: Entity?,
            attribute: Attribute?,
            value: Value?,
    ): Flow<Result<PersistedStatement>> {
        var res = store.statements.asFlow()
        if (entity != null) {
            res = res.filter { it.statement.entity == entity }
        }
        if (attribute != null) {
            res = res.filter { it.statement.attribute == attribute }
        }
        if (value != null) {
            res = res.filter { it.statement.value == value }
        }
        return res.map { success(it) }
    }

    /** Retuns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all. */
    override suspend fun matchStatementsRange(
            entity: Entity?,
            attribute: Attribute?,
            range: Range,
    ): Flow<Result<PersistedStatement>> {
        var res = store.statements.asFlow()
        if (entity != null) {
            res = res.filter { it.statement.entity == entity }
        }
        if (attribute != null) {
            res = res.filter { it.statement.attribute == attribute }
        }
        res = res.filter { ps ->
            val value = ps.statement.value
            when {
                value is StringLiteral && range is StringLiteralRange   -> value.value >= range.start && value.value < range.end
                value is FloatLiteral && range is FloatLiteralRange     -> value.value >= range.start && value.value < range.end
                value is IntegerLiteral && range is IntegerLiteralRange -> value.value >= range.start && value.value < range.end
                else                                                    -> false
            }
        }
        return res.map { success(it) }
    }

    /** Returns the PersistedStatement for the given context. */
    override suspend fun statementForContext(context: Entity): Result<PersistedStatement?> =
        success(store.statements.find { it.context == context })
}
