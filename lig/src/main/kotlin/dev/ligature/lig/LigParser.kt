/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.*
import arrow.core.computations.either
import dev.ligature.*
import dev.ligature.rakkoon.*

data class LigError(val message: String)

class LigParser {
    fun parse(input: String): Iterator<Statement> {
        val rakkoon = Rakkoon(input)
        val statements = mutableListOf<Statement>()
        var previousStatement: Statement? = null //used for handling wildcards
        while (!rakkoon.isComplete()) {
            when (val resStatement = parseStatement(rakkoon, previousStatement)) {
                is Either.Left -> throw RuntimeException(resStatement.value.message)
                is Either.Right -> statements.add(resStatement.value)
            }
        }
        return statements.iterator()
    }

    fun parseStatement(rakkoon: Rakkoon, previousStatement: Statement?): Either<LigError, Statement> {
        ignoreWhitespaceAndNewLines(rakkoon)
        val entity = parseEntity(rakkoon, previousStatement?.entity)
        if (entity.isLeft()) return entity.map { TODO() }
        ignoreWhitespace(rakkoon)
        val attribute = parseAttribute(rakkoon, previousStatement?.attribute)
        if (attribute.isLeft()) return attribute.map { TODO() }
        ignoreWhitespace(rakkoon)
        val value = parseValue(rakkoon, previousStatement?.value)
        if (value.isLeft()) return value.map { TODO() }
        ignoreWhitespace(rakkoon)
        val context = parseEntity(rakkoon, previousStatement?.context)
        ignoreWhitespaceAndNewLines(rakkoon)

        return Either.Right(Statement(entity.getOrElse { TODO() }, attribute.getOrElse { TODO() }, value.getOrElse { TODO() }, context.getOrElse { TODO() }))
    }

    fun ignoreWhitespace(rakkoon: Rakkoon) {
        rakkoon.nibble { input, _ ->
            when (input) {
                ' ', '\t' -> Next
                null -> Complete()
                else -> Complete(1)
            }
        }
    }

    fun ignoreWhitespaceAndNewLines(rakkoon: Rakkoon) {
        rakkoon.nibble { input, _ ->
            when (input) {
                ' ', '\t', '\n' -> Next
                null -> Complete()
                else -> Complete(1)
            }
        }
    }

    fun parseWildcard(rakkoon: Rakkoon): Boolean {
        TODO("Rewrite to use new Rakkoon api")
//        return rakkoon.bite(Rule(stringNibbler("_"), ignoreAction)).isRight()
    }

    fun parseEntity(rakkoon: Rakkoon, previousEntity: Entity? = null): Either<LigError, Entity> {
        //TODO need to also check for _
        val entityPattern = "[a-zA-Z0-9_:]+".toRegex()
        val openEntityNibbler = stringNibbler("<")
        val entityNibbler = predicateNibbler { it.toString().matches(entityPattern) }
        val closeEntityNibbler = stringNibbler(">")

        return when (val entityRes = rakkoon.nibble(openEntityNibbler, entityNibbler, closeEntityNibbler)) {
            is None -> Either.Left(LigError("Could not parse Entity."))
            is Some -> Either.Right(Entity(entityRes.value[1].value))
        }
    }

    fun parseAttribute(rakkoon: Rakkoon, previousAttribute: Attribute? = null): Either<LigError, Attribute> {
        //TODO need to also check for _
        println(rakkoon.remainingText())
        val attributePattern = "[a-zA-Z0-9_:]".toRegex()
        val openAttributeNibbler = stringNibbler("@<")
        val attributeNibbler = predicateNibbler { it.toString().matches(attributePattern) }
        val closeAttributeNibbler = stringNibbler(">")

        return when (val attributeRes = rakkoon.nibble(openAttributeNibbler, attributeNibbler, closeAttributeNibbler)) {
            is None -> Either.Left(LigError("Could not parse Attribute.\n${rakkoon.remainingText()}"))
            is Some -> Either.Right(Attribute(attributeRes.value[1].value))
        }
    }

    fun parseValue(rakkoon: Rakkoon, previousValue: Value? = null): Either<LigError, Value> {
        //TODO need to check for _ first
        val entityRes = parseEntity(rakkoon, null)
        if (entityRes.isRight()) return entityRes

        val floatRes = parseFloatLiteral(rakkoon)
        if (floatRes.isRight()) return floatRes

        val integerRes = parseIntegerLiteral(rakkoon)
        if (integerRes.isRight()) return integerRes

        val stringRes = parseStringLiteral(rakkoon)
        if (stringRes.isRight()) return stringRes

        return Either.Left(LigError("Unsupported Value\n${rakkoon.remainingText()}"))
    }

    fun parseFloatLiteral(rakkoon: Rakkoon): Either<LigError, FloatLiteral> {
        val numberNibbler = rangeNibbler('0'..'9')
        val decimalNibbler = stringNibbler(".")

        return when (val res = rakkoon.nibble(numberNibbler, decimalNibbler, numberNibbler)) {
            is None -> Either.Left(LigError("Could not parse Float."))
            is Some -> Either.Right(FloatLiteral(res.value.map { it.value }.fold("") { x, y -> x + y }.toDouble() ))
        }
    }

    fun parseIntegerLiteral(rakkoon: Rakkoon): Either<LigError, IntegerLiteral> {
        val numberNibbler = rangeNibbler('0'..'9')

        return when (val res = rakkoon.nibble(numberNibbler)) {
            is None -> Either.Left(LigError("Could not parse Integer."))
            is Some -> Either.Right(IntegerLiteral(res.value.value.toLong() ))
        }
    }

    fun parseStringLiteral(rakkoon: Rakkoon): Either<LigError, StringLiteral> {
        val quote = stringNibbler("\"")
        val stringPattern = "[a-zA-Z0-9_ \t\n]".toRegex() //TODO too simplistic
        val content = predicateNibbler { it.toString().matches(stringPattern) }

        return when (val res = rakkoon.nibble(quote, content, quote)) {
            is None -> Either.Left(LigError("Could not parse String."))
            is Some -> Either.Right(StringLiteral(res.value[1].value))
        }
    }
}
