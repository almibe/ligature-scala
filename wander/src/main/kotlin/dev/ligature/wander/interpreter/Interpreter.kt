/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.wander.WanderError
import dev.ligature.wander.parser.*

class Interpreter {
    fun runCommmand(script: Script): Either<WanderError, Primitive> {
        return run(script)
    }

    fun runQuery(script: Script): Either<WanderError, Primitive> {
        return run(script)
    }

    private fun run(script: Script): Either<WanderError, Primitive> {
        val topScope = Scope(null)
        var result: Either<WanderError, Primitive> = Either.Right(UnitPrimitive)
        script.lines.forEach {
            result = runStatement(it, topScope)
        }
        return result
    }

    private fun runStatement(statement: WanderStatement, scope: Scope): Either<WanderError, Primitive> {
        return when (statement) {
            is LetStatement -> TODO("evaluate and then store symbol")
            is Symbol -> scope.lookupSymbol(statement.name)
            is Primitive -> Either.Right(statement)
        }
    }
}
