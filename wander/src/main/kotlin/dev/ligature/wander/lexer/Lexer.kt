/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import dev.ligature.IntegerLiteral
import dev.ligature.lig.LigParser
import dev.ligature.rakkoon.*
import dev.ligature.wander.error.*
import dev.ligature.wander.parser.LetStatement

class Lexer {
    private val ligParser = LigParser()
//    private val truePattern = stringPattern("true")
//    private val falsePattern = stringPattern("false")
//    private val booleanRule = Rule(anyPattern(truePattern, falsePattern), valueAction)
//
//    private val endOfLineRule = Rule(regexPattern("( *\n| *#.*\n| *$| *#.*$)".toRegex()), ignoreAction)
//
//    private val whiteSpace = regexPattern("[ \t]+".toRegex())
//
//    private val toIntegerAction = Action<IntegerPrimitive> {
//        Either.Right(IntegerPrimitive(IntegerLiteral(it.toString().toLong())))
//    }

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

    private fun Char.isOperator(): Boolean {
        TODO()
    }

    private fun Char.isIdentifier(): Boolean {
        TODO()
    }

    private fun readNewLine(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO()
    }

    private fun readOperator(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO()
    }

    private fun readNumber(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO()
    }

    private fun readIdentifer(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        TODO()
    }

    private fun readToken(rakkoon: Rakkoon): Either<LexerError, WanderToken> {
        when (rakkoon.peek()) {
            ' ', '\t' -> TODO("remove white space")
        }
        val next = rakkoon.peek()
        return when  {
            next == null -> TODO()
            next.isNewLine() -> readNewLine(rakkoon)
            next.isOperator() -> readOperator(rakkoon)
            next.isDigit() -> readNumber(rakkoon)
            next.isIdentifier() -> readIdentifer(rakkoon)
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
