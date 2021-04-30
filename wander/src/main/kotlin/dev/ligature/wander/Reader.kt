/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.getOrHandle
import dev.ligature.IntegerLiteral
import dev.ligature.lig.LigParser
import dev.ligature.rakkoon.*

class Reader {
    private val ligParser = LigParser()
    private val truePattern = stringPattern("true")
    private val falsePattern = stringPattern("false")
    private val booleanRule = Rule(anyPattern(truePattern, falsePattern), valueAction)

    private val endOfLineRule = Rule(regexPattern("( *\n| *#.*\n| *$| *#.*$)".toRegex()), ignoreAction)

    private val toIntegerAction = Action<IntegerPrimitive> {
        Either.Right(IntegerPrimitive(IntegerLiteral(it.toString().toLong())))
    }

    fun read(script: String): Either<WanderError, Script> {
        val rakkoon = Rakkoon(script)
        return when (val wanderStatement = readWanderStatement(rakkoon)) {
            is Either.Left  -> wanderStatement
            is Either.Right -> Either.Right(Script(listOf(wanderStatement.value)))
        }
    }

    private fun readWanderStatement(rakkoon: Rakkoon): Either<WanderError, WanderStatement> {
        val attributeRes = ligParser.parseAttribute(rakkoon)
        if (attributeRes.isRight()) return attributeRes.map { AttributePrimitive(it) }.mapLeft { TODO() }

        val booleanRes = rakkoon.bite(booleanRule)
        if (booleanRes.isRight()) return booleanRes.map { BooleanPrimitive(it.toString().toBoolean()) }.mapLeft { TODO() }

        val entityRes = ligParser.parseEntity(rakkoon)
        if (entityRes.isRight()) return entityRes.map { EntityPrimitive(it) }.mapLeft { TODO() }

        val floatRes = ligParser.parseFloatLiteral(rakkoon)
        if (floatRes.isRight()) return floatRes.map { FloatPrimitive(it) }.mapLeft { TODO() }

        val integerRes = ligParser.parseIntegerLiteral(rakkoon)
        if (integerRes.isRight()) return integerRes.map { IntegerPrimitive(it) }.mapLeft { TODO() }

        val stringRes = ligParser.parseStringLiteral(rakkoon)
        if (stringRes.isRight()) return stringRes.map { StringPrimitive(it) }.mapLeft { TODO() }

        val endOfLineRes = rakkoon.bite(endOfLineRule)
        if (endOfLineRes.isRight()) return endOfLineRes.map { UnitPrimitive }.mapLeft { TODO() }

        return Either.Left(NotSupported("Error")) //TODO report an error
    }
}
