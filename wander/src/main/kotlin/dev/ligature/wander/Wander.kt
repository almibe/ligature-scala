/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.Dataset
import dev.ligature.Ligature
import dev.ligature.QueryTx
import dev.ligature.WriteTx
import dev.ligature.wander.interpreter.*

class Wander(private val ligature: Ligature) {
    private val interpreter = Interpreter()

    suspend fun runCommand(dataset: Dataset, input: String): Either<WanderError, WanderValue> {
        return ligature.write(dataset) {
            interpreter.run(input, createCommandScope(it))
        }
    }

    suspend fun runQuery(dataset: Dataset, input: String): Either<WanderError, WanderValue> {
        return ligature.query(dataset) {
            interpreter.run(input, createQueryScope(it))
        }
    }

    fun run(input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, Scope(null))
    }

    private fun createCommandScope(writeTx: WriteTx): Scope {
        val scope = Scope(null)
        scope.addSymbol("addStatement", WanderFunction(listOf(StatementWanderValue::class), TODO()))
        scope.addSymbol("generateEntity", WanderFunction(listOf(EntityWanderValue::class), TODO()))
        scope.addSymbol("removeStatement", WanderFunction(listOf(StatementWanderValue::class), TODO()))
        return scope
    }

    private fun createQueryScope(queryTx: QueryTx): Scope {
        val scope = Scope(null)
        scope.addSymbol("allStatements", WanderFunction(listOf(), TODO()))
        scope.addSymbol("matchStatements", WanderFunction(listOf(), TODO())) //TODO figure out what argument type should be
        scope.addSymbol("matchStatementsRange", WanderFunction(listOf(), TODO())) //TODO figure out what argument type should be
        return scope
    }
}
