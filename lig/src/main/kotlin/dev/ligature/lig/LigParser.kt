/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.Either
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.none
import dev.ligature.*
import dev.ligature.rakkoon.*

class LigParser {
    fun parse(input: String): Iterator<Statement> {
        println("xxx")
        val rakkoon = Rakkoon(input)
        val statements = mutableListOf<Statement>()
        var previousStatement: Statement? = null //used for handling wildcards
        while (!rakkoon.isComplete()) {
            println("a")
            val resStatement = parseStatement(rakkoon, previousStatement)
            println(resStatement)
            statements.add(resStatement)
        }
        return statements.iterator()
    }

    fun parseStatement(rakkoon: Rakkoon, previousStatement: Statement?): Statement {
        val entity = parseEntity(rakkoon, previousStatement?.entity)
        val attribute = parseAttribute(rakkoon, previousStatement?.attribute)
        val value = parseValue(rakkoon, previousStatement?.value)
        val context = parseEntity(rakkoon, previousStatement?.context)

        return Statement(entity, attribute, value, context)
    }

    fun parseWildcard(rakkoon: Rakkoon): Boolean {
        TODO()
    }

    fun parseEntity(rakkoon: Rakkoon, previousEntity: Entity?): Entity {
        //TODO need to also check for _
        val res = rakkoon.bite(Rule(stringPattern("<"), ignoreAction))
        println(res)
        //TODO error handling
        val entity: Either<RakkoonError, CharSequence> = rakkoon.bite(Rule(regexPattern("[a-zA-Z0-9_:]+".toRegex()), valueAction))
        //TODO error handling
        val res2 = rakkoon.bite(Rule(stringPattern(">"), ignoreAction))
        println(res2)
        //TODO error handling
        return Entity(entity.getOrElse { TODO() }.toString()) //TODO needs validation
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
    private val stringPattern = "\"[a-zA-Z0-9_ \t\n]\"".toRegex()

    fun parseValue(rakkoon: Rakkoon, previousValue: Value?): Value {
//        return when {
//            entityPattern.matches(value) -> parseEntity(value)
//            integerPattern.matches(value) -> IntegerLiteral(value.toLong())
//            floatPattern.matches(value) -> FloatLiteral(value.toDouble())
//            stringPattern.matches(value) -> StringLiteral(value.removePrefix("\"").removeSuffix("\""))
//            else -> TODO()
//        }
        //TODO need to check for _ first
        return parseEntity(rakkoon, null)
    }
}
