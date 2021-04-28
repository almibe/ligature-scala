/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.IntegerLiteral
import dev.ligature.rakkoon.Action
import dev.ligature.rakkoon.Rakkoon
import dev.ligature.rakkoon.Rule
import dev.ligature.rakkoon.stringPattern

class Reader {
    private val toIntegerAction = Action<IntegerPrimitive> {
        Either.Right(IntegerPrimitive(IntegerLiteral(it.toString().toLong())))
    }

    fun read(script: String): Either<WanderError, Script> {
        val rakkoon = Rakkoon(script)
        val wanderStatement = readWanderStatement(rakkoon)
        return when (wanderStatement) {
            is Either.Left  -> wanderStatement
            is Either.Right -> Either.Right(Script(listOf(wanderStatement.value)))
        }
    }

    private fun readWanderStatement(rakkoon: Rakkoon): Either<WanderError, WanderStatement> {

        return rakkoon.bite(Rule(stringPattern("5"), toIntegerAction)).mapLeft { ParsingError(it) }
    }
}
