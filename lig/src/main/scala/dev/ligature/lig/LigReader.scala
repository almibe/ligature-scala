/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

case class LigError(val message: String)

class LigReader {
    private val whiteSpaceNibbler = charNibbler(' ', '\t')
    private val whiteSpaceAndNewLineNibbler = charNibbler(' ', '\t', '\n')

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
        rakkoon.nibble(whiteSpaceAndNewLineNibbler)
        val entity = parseIdentifier(rakkoon, previousStatement?.entity)
        if (entity.isLeft()) return entity.map { TODO() }
        rakkoon.nibble(whiteSpaceNibbler)
        val attribute = parseIdentifier(rakkoon, previousStatement?.attribute)
        if (attribute.isLeft()) return attribute.map { TODO() }
        rakkoon.nibble(whiteSpaceNibbler)
        val value = parseValue(rakkoon, previousStatement?.value)
        if (value.isLeft()) return value.map { TODO() }
        rakkoon.nibble(whiteSpaceNibbler)
        val context = parseIdentifier(rakkoon, previousStatement?.context)
        rakkoon.nibble(whiteSpaceAndNewLineNibbler)

        return Either.Right(Statement(entity.getOrElse { TODO() }, attribute.getOrElse { TODO() }, value.getOrElse { TODO() }, context.getOrElse { TODO() }))
    }

    fun parseWildcard(rakkoon: Rakkoon): Boolean {
        TODO("Rewrite to use new Rakkoon api")
//        return rakkoon.bite(Rule(stringNibbler("_"), ignoreAction)).isRight()
    }

    fun parseIdentifier(rakkoon: Rakkoon, previousIdentifier: Identifier? = null): Either<LigError, Identifier> {
        //TODO need to also check for _
        val openIdentifierNibbler = stringNibbler("<")
        val closeIdentifierNibbler = stringNibbler(">")

        return when (val entityRes = rakkoon.nibble(openIdentifierNibbler, identifierNibbler, closeIdentifierNibbler)) {
            is None -> Either.Left(LigError("Could not parse Identifier."))
            is Some -> createIdentifier(entityRes.value[1].value)
        }
    }

    fun createIdentifier(id: String): Either<LigError, Identifier> =
        when (val res = Identifier(id)) {
            is None -> Either.Left(LigError("Invalid Identifier Id - $id"))
            is Some -> Either.Right(res.value)
        }

//    fun parseAttribute(rakkoon: Rakkoon, previousAttribute: Attribute? = null): Either<LigError, Attribute> {
//        //TODO need to also check for _
//        val openAttributeNibbler = stringNibbler("@<")
//        val closeAttributeNibbler = stringNibbler(">")
//
//        return when (val attributeRes = rakkoon.nibble(openAttributeNibbler, identifierNibbler, closeAttributeNibbler)) {
//            is None -> Either.Left(LigError("Could not parse Attribute.\n${rakkoon.remainingText()}"))
//            is Some -> createAttribute(attributeRes.value[1].value)
//        }
//    }
//
//    fun createAttribute(name: String): Either<LigError, Attribute> =
//        when (val res = Attribute.from(name)) {
//            is None -> Either.Left(LigError("Invalid Attribute name - $name"))
//            is Some -> Either.Right(res.value)
//        }

    fun parseValue(rakkoon: Rakkoon, previousValue: Value? = null): Either<LigError, Value> {
        //TODO need to check for _ first
        val entityRes = parseIdentifier(rakkoon, null)
        if (entityRes.isRight()) return entityRes

//        val floatRes = parseFloatLiteral(rakkoon)
//        if (floatRes.isRight()) return floatRes

        val integerRes = parseIntegerLiteral(rakkoon)
        if (integerRes.isRight()) return integerRes

        val stringRes = parseStringLiteral(rakkoon)
        if (stringRes.isRight()) return stringRes

        return Either.Left(LigError("Unsupported Value\n${rakkoon.remainingText()}"))
    }

//    fun parseFloatLiteral(rakkoon: Rakkoon): Either<LigError, FloatLiteral> {
//        val numberNibbler = rangeNibbler('0'..'9')
//        val decimalNibbler = stringNibbler(".")
//
//        return when (val res = rakkoon.nibble(numberNibbler, decimalNibbler, numberNibbler)) {
//            is None -> Either.Left(LigError("Could not parse Float."))
//            is Some -> Either.Right(FloatLiteral(res.value.map { it.value }.fold("") { x, y -> x + y }.toDouble() ))
//        }
//    }

    fun parseIntegerLiteral(rakkoon: Rakkoon): Either<LigError, IntegerLiteral> {
        val numberNibbler = rangeNibbler('0'..'9')

        return when (val res = rakkoon.nibble(numberNibbler)) {
            is None -> Either.Left(LigError("Could not parse Integer."))
            is Some -> Either.Right(IntegerLiteral(res.value.value.toLong() ))
        }
    }

    fun parseStringLiteral(rakkoon: Rakkoon): Either<LigError, StringLiteral> {
        val quote = charNibbler('"')

        return when (val res = rakkoon.nibble(quote, stringContentNibbler, quote)) {
            is None -> Either.Left(LigError("Could not parse String."))
            is Some -> Either.Right(StringLiteral(res.value[1].value))
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private val stringContentNibbler = Nibbler { lookAhead ->
        //Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
        val commandChars = 0x00.toChar()..0x1F.toChar()
        val validHexChar = { c: Char? -> c != null && ( c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F' ) }

        var offset = 0U
        var fail = false
        while (lookAhead.peek(offset) != null) {
            val c = lookAhead.peek(offset)!!
            if (commandChars.contains(c)) {
                fail = true
                break
            } else if (c == '"') {
                break
            } else if (c == '\\') {
                when (lookAhead.peek(offset + 1U)) {
                    '\\', '"', 'b', 'f', 'n', 'r', 't' -> offset = offset + 2U
                    'u' -> {
                        for (i in 1U..4U) {
                            if (!validHexChar(lookAhead.peek(offset + 1U + i))) {
                                fail = true
                                break
                            }
                        }
                        if (fail) {
                            break
                        } else {
                            offset = offset + 5U
                        }
                    }
                    else -> {
                        fail = true
                        break
                    }
                }
            } else {
                offset++
            }
        }
        if (fail || offset == 0U) {
            Cancel
        } else {
            Complete(offset.toInt())
        }
    }
}