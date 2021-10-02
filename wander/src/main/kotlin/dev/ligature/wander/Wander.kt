/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.QueryTx
import dev.ligature.WriteTx
import dev.ligature.wander.interpreter.*

class Wander {
    private val interpreter = Interpreter()

    fun runCommand(writeTx: WriteTx, input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, createCommandScope(writeTx))
    }

    fun runQuery(queryTx: QueryTx, input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, createQueryScope(queryTx))
    }

    fun run(input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, Scope(null))
    }

    private fun createCommandScope(writeTx: WriteTx): Scope {
        val scope = Scope(null)
        scope.addSymbol("addStatement", WanderFunction(listOf(StatementWanderValue::class)) { args ->
            if (args.size == 1 && args[0] is StatementWanderValue) {
                val statement = (args[0] as StatementWanderValue).value
                writeTx.addStatement(statement)
                Either.Right(UnitWanderValue)
            } else {
                Either.Left(ArgumentError("addStatement accepts 1 Statement, found $args"))
            }
        })
        scope.addSymbol("generateEntity", WanderFunction(listOf(IdentifierWanderValue::class)) { args ->
            if (args.size == 1 && args[0] is IdentifierWanderValue) {
                val identifierPrefix = (args[0] as IdentifierWanderValue).value
                writeTx.generateIdentifier(identifierPrefix.id)
                Either.Right(UnitWanderValue)
            } else {
                Either.Left(ArgumentError("generateEntity accepts 1 Entity, found $args"))
            }
        })
        scope.addSymbol("removeStatement", WanderFunction(listOf(StatementWanderValue::class)) { args ->
            if (args.size == 1 && args[0] is StatementWanderValue) {
                val statement = (args[0] as StatementWanderValue).value
                writeTx.removeStatement(statement)
                Either.Right(UnitWanderValue)
            } else {
                Either.Left(ArgumentError("removeStatement accepts 1 Statement, found $args"))
            }
        })
        return scope
    }

    private fun createQueryScope(queryTx: QueryTx): Scope {
        val scope = Scope(null)
        scope.addSymbol("matchStatements", WanderFunction(listOf(StatementQueryValue::class)) { args ->
            if (args.size == 1 && args[0] is StatementQueryValue) {
                TODO()
            } else {
                Either.Left(ArgumentError("matchStatements accepts 1 StatementQuery, found $args"))
            }
        })
        return scope
    }
}
