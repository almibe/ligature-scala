/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import dev.ligature.lig.LigParser
import dev.ligature.rakkoon.*
import dev.ligature.wander.error.*

class Lexer {
    private val ligParser = LigParser()

    fun read(script: String): Either<WanderError, List<WanderToken>> {
        val rakkoon = Rakkoon(script)
        val tokens = mutableListOf<WanderToken>()
        while(!rakkoon.isComplete()) {
            when (val tokenRes = readToken(rakkoon)) {
                is Either.Left  -> return tokenRes
                is Either.Right -> tokens.add(tokenRes.value)
            }
        }
        return Either.Right(tokens)
    }

    private fun Char.isNewLine(): Boolean {
        return this == '\n'
    }

    private fun Char.isOperator(): Boolean =
        when (this) {
            '=' -> true //TODO add all other operators
            else -> false
        }

    private fun Char.isIdentifier(): Boolean =
        when (this) {
            in 'a'..'z', in 'A'..'Z', '_' -> true
            else -> false
        }

    private fun readNewLine(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO()
    }

    @kotlin.ExperimentalUnsignedTypes
    private fun readOperator(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        when (rakkoon.peek(1U)) {
            '=' -> {
                rakkoon.bite(1U);
                return Either.Right(WanderToken(rakkoon.currentOffset(), AssignmentOperator))
            }
            else -> TODO()
        }
    }

    private fun readNumber(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO()
    }

    private fun readIdentifier(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        return when (val res = rakkoon.nibble(rangeNibbler('a'..'z', 'A'..'Z', '_'..'_', '0'..'9'))) {
            None -> Either.Left(LexerError("Illegal Identifier", rakkoon.currentOffset()))
            is Some -> {
                //TODO check for keywords
                val match = res.value
                Either.Right(WanderToken(match.range.first, Identifier(match.value)))
            }
        }
    }

    private fun readToken(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        rakkoon.nibble(charNibbler(' ', '\t')) //remove whitespace
        val next = rakkoon.peek()
        return when  {
            next == null -> TODO()
            next.isNewLine() -> readNewLine(rakkoon)
            next.isOperator() -> readOperator(rakkoon)
            next.isDigit() -> readNumber(rakkoon)
            next.isIdentifier() -> readIdentifier(rakkoon)
            else -> return Either.Left(LexerError("Unexpected input.\n${rakkoon.remainingText()}", rakkoon.currentOffset()))
        }
    }

    private fun readPrimitive(rakkoon: Rakkoon): Either<WanderError, Primitive> {
        val attributeRes = ligParser.parseAttribute(rakkoon)
        if (attributeRes.isRight()) return attributeRes.map { AttributePrimitive(it) }.mapLeft { TODO() }

//        val booleanRes = rakkoon.bite(booleanRule)
//        if (booleanRes.isRight()) return booleanRes.map { BooleanPrimitive(it.toString().toBoolean()) }.mapLeft { TODO() }

        val entityRes = ligParser.parseEntity(rakkoon)
        if (entityRes.isRight()) return entityRes.map { EntityPrimitive(it) }.mapLeft { TODO() }

        val floatRes = ligParser.parseFloatLiteral(rakkoon)
        if (floatRes.isRight()) return floatRes.map { FloatPrimitive(it) }.mapLeft { TODO() }

        val integerRes = ligParser.parseIntegerLiteral(rakkoon)
        if (integerRes.isRight()) return integerRes.map { IntegerPrimitive(it) }.mapLeft { TODO() }

        val stringRes = ligParser.parseStringLiteral(rakkoon)
        if (stringRes.isRight()) return stringRes.map { StringPrimitive(it) }.mapLeft { TODO() }

        TODO()
    }
}
