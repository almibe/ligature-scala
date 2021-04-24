/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.*
import dev.ligature.*
import dev.ligature.rakkoon.*

class LigParser {
    fun parse(input: String): Iterator<Statement> {
        val rakkoon = Rakkoon(input)
        val statements = mutableListOf<Statement>()
        var previousStatement: Statement? = null //used for handling wildcards
        while (!rakkoon.isComplete()) {
            val resStatement = parseStatement(rakkoon, previousStatement)
            statements.add(resStatement)
        }
        return statements.iterator()
    }

    fun parseStatement(rakkoon: Rakkoon, previousStatement: Statement?): Statement {
        ignoreWhitespaceAndNewLines(rakkoon)
        val entity = parseEntity(rakkoon, previousStatement?.entity)
        ignoreWhitespace(rakkoon)
        val attribute = parseAttribute(rakkoon, previousStatement?.attribute)
        ignoreWhitespace(rakkoon)
        val value = parseValue(rakkoon, previousStatement?.value)
        ignoreWhitespace(rakkoon)
        val context = parseEntity(rakkoon, previousStatement?.context)
        ignoreWhitespaceAndNewLines(rakkoon)

        return Statement(entity.getOrElse { TODO() }, attribute, value, context.getOrElse { TODO() })
    }

    fun ignoreWhitespace(rakkoon: Rakkoon) {
        rakkoon.bite(Rule(regexPattern(" *".toRegex()), ignoreAction))
    }

    fun ignoreWhitespaceAndNewLines(rakkoon: Rakkoon) {
        rakkoon.bite(Rule(regexPattern("[ \n]*".toRegex()), ignoreAction))
    }

    fun parseWildcard(rakkoon: Rakkoon): Boolean {
        return rakkoon.bite(Rule(stringPattern("_"), ignoreAction)).isRight()
    }

    fun parseEntity(rakkoon: Rakkoon, previousEntity: Entity?): Either<RakkoonError, Entity> {
        //TODO need to also check for _
        val res = rakkoon.bite(Rule(stringPattern("<"), ignoreAction))
        if (res.isLeft()) { return Either.Left(NoMatch(rakkoon.currentOffset())) }
        //TODO error handling
        val entity: Either<RakkoonError, CharSequence> = rakkoon.bite(Rule(regexPattern("[a-zA-Z0-9_:]+".toRegex()), valueAction))
        //TODO error handling
        val res2 = rakkoon.bite(Rule(stringPattern(">"), ignoreAction))
        //TODO error handling
        if (res.isLeft() || entity.isLeft() || res2.isLeft()) {
            return Either.Left(NoMatch(rakkoon.currentOffset()))
        }
        return Either.Right(Entity(entity.getOrElse { TODO() }.toString())) //TODO needs validation
    }

    fun parseAttribute(rakkoon: Rakkoon, previousAttribute: Attribute?): Attribute {
        //TODO need to also check for _
        val res = rakkoon.bite(Rule(stringPattern("@<"), ignoreAction))
        //TODO error handling
        val attribute: Either<RakkoonError, CharSequence> = rakkoon.bite(Rule(regexPattern("[a-zA-Z0-9_:]+".toRegex()), valueAction))
        //TODO error handling
        val res2 = rakkoon.bite(Rule(stringPattern(">"), ignoreAction))
        //TODO error handling
        return Attribute(attribute.getOrElse { TODO() }.toString()) //TODO needs validation
    }

    //TODO all of the patterns below are overly simplistic
    private val entityPattern = "<[a-zA-Z0-9_]+>".toRegex()
    private val integerPattern = "\\d+".toRegex()
    private val floatPattern = "\\d+\\.\\d+".toRegex()
    private val stringPattern = "\"[a-zA-Z0-9_ \t\n]+\"".toRegex()

    fun parseValue(rakkoon: Rakkoon, previousValue: Value?): Value {
        //TODO need to check for _ first
        val entityRes = parseEntity(rakkoon, null)
        if (entityRes.isRight()) return entityRes.getOrElse { TODO() }

        val floatRes = parseFloatLiteral(rakkoon)
        if (floatRes.isRight()) return floatRes.getOrElse { TODO() }

        val integerRes = parseIntegerLiteral(rakkoon)
        if (integerRes.isRight()) return integerRes.getOrElse { TODO() }

        val stringRes = parseStringLiteral(rakkoon)
        if (stringRes.isRight()) return stringRes.getOrElse { TODO() }

        TODO("Unsupported Value")
    }

    fun parseFloatLiteral(rakkoon: Rakkoon): Either<RakkoonError, FloatLiteral> =
        rakkoon.bite(Rule(regexPattern(floatPattern), valueAction))
            .map { FloatLiteral(it.toString().toDouble()) }

    fun parseIntegerLiteral(rakkoon: Rakkoon): Either<RakkoonError, IntegerLiteral> =
        rakkoon.bite(Rule(regexPattern(integerPattern), valueAction))
            .map { IntegerLiteral(it.toString().toLong()) }

    fun parseStringLiteral(rakkoon: Rakkoon): Either<RakkoonError, StringLiteral> =
        rakkoon.bite(Rule(regexPattern(stringPattern), valueAction))
            .map { StringLiteral(it.toString().removeSurrounding("\"")) }
}
