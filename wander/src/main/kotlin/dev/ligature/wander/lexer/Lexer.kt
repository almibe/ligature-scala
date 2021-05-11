/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import dev.ligature.FloatLiteral
import dev.ligature.IntegerLiteral
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

    private fun Char.isString(): Boolean {
        return this == '"'
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

    private fun readNewLine(rakkoon: Rakkoon): Either<LexerError, WanderToken> =
        when (rakkoon.peek()) {
            '\n' -> {
                rakkoon.bite(1U)
                Either.Right(WanderToken(rakkoon.currentOffset()-1, NewLineToken))
            }
            else -> Either.Left(LexerError("Invalid newline", rakkoon.currentOffset()))
        }

    @kotlin.ExperimentalUnsignedTypes
    private fun readOperator(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        val operatorOffset = rakkoon.currentOffset()
        return when (rakkoon.peek()) {
            '=' -> {
                rakkoon.bite(1U);
                Either.Right(WanderToken(operatorOffset, AssignmentOperator))
            } //TODO might need to handle == if I decide to handle equality that way
            '@' -> readAttribute(rakkoon)
            '<' -> readEntity(rakkoon) //TODO will probably need to handle less than as well eventually
            else -> Either.Left(LexerError("Unsupported operator, ${rakkoon.remainingText()}", rakkoon.currentOffset()))
        }
    }

    private fun readAttribute(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        val startingOffset = rakkoon.currentOffset()
        return when (val res = ligParser.parseAttribute(rakkoon)) {
            is Either.Left -> Either.Left(LexerError("Error reading Attribute, ${rakkoon.remainingText()}", rakkoon.currentOffset()))
            is Either.Right -> Either.Right(WanderToken(startingOffset, AttributePrimitive(res.value)))
        }
    }

    private fun readEntity(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        val startingOffset = rakkoon.currentOffset()
        return when (val res = ligParser.parseEntity(rakkoon)) {
            is Either.Left -> Either.Left(LexerError("Error reading Entity, ${rakkoon.remainingText()}", rakkoon.currentOffset()))
            is Either.Right -> Either.Right(WanderToken(startingOffset, EntityPrimitive(res.value)))
        }
    }

    private fun readNumber(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        val initialOffset = rakkoon.currentOffset()
        return when (val number = rakkoon.nibble(rangeNibbler('0'..'9'))) {
            None -> TODO()
            is Some ->
                when (rakkoon.nibble(charNibbler('.'))) {
                    None -> Either.Right(WanderToken(initialOffset, IntegerPrimitive(IntegerLiteral(number.value.value.toLong()))))
                    is Some -> {
                        when (val decimal = rakkoon.nibble(rangeNibbler('0'..'9'))) {
                            is None -> TODO("return error parsing float")
                            is Some -> {
                                val numberRepresentation = number.value.value + "." + decimal.value.value
                                Either.Right(WanderToken(initialOffset, FloatPrimitive(FloatLiteral(numberRepresentation.toDouble()))))
                            }
                        }
                    }
                }
        }
    }

    private fun readIdentifier(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        return when (val res = rakkoon.nibble(rangeNibbler('a'..'z', 'A'..'Z', '_'..'_', '0'..'9'))) {
            None -> Either.Left(LexerError("Illegal Identifier", rakkoon.currentOffset()))
            is Some -> {
                val match = res.value
                when (match.value) { //TODO check for keywords
                    "true" -> Either.Right(WanderToken(match.range.first, BooleanPrimitive(true)))
                    "false" -> Either.Right(WanderToken(match.range.first, BooleanPrimitive(false)))
                    "let" -> Either.Right(WanderToken(match.range.first, LetKeyword))
                    else -> Either.Right(WanderToken(match.range.first, Identifier(match.value)))
                }
            }
        }
    }

    private fun readString(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO("Use implementation from lig")
    }

    private fun readToken(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        rakkoon.nibble(charNibbler(' ', '\t')) //remove whitespace
        return when (rakkoon.peek()) {
            null -> Either.Right(WanderToken(rakkoon.currentOffset(), EndOfScriptToken))
            '\n' -> readNewLine(rakkoon)
            '"' -> readString(rakkoon)
            '=', '<', '@' -> readOperator(rakkoon)
            in '0'..'9' -> readNumber(rakkoon)
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier(rakkoon)
            else -> return Either.Left(LexerError("Unexpected input.\n${rakkoon.remainingText()}", rakkoon.currentOffset()))
        }
    }

    //TODO remove after testing all primitives
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
