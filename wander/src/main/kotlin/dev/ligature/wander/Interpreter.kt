/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either

class Interpreter {
    fun run(script: Script): Either<WanderError, Primitive> {
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
