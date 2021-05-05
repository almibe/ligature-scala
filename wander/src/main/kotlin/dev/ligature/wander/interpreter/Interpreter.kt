/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.wander.error.InterpreterError
import dev.ligature.wander.parser.*

class Interpreter {
    fun runCommmand(script: Script): Either<InterpreterError, Primitive> {
        return run(script)
    }

    fun runQuery(script: Script): Either<InterpreterError, Primitive> {
        return run(script)
    }

    private fun run(script: Script): Either<InterpreterError, Primitive> {
        val topScope = Scope(null)
        var result: Either<InterpreterError, Primitive> = Either.Right(UnitPrimitive)
        script.lines.forEach {
            result = runStatement(it, topScope)
        }
        return result
    }

    private fun runStatement(statement: WanderStatement, scope: Scope): Either<InterpreterError, Primitive> {
        return when (statement) {
            is LetStatement -> TODO("evaluate and then store symbol")
            is Symbol -> TODO()//scope.lookupSymbol(statement.name)
            is Primitive -> Either.Right(statement)
        }
    }
}
