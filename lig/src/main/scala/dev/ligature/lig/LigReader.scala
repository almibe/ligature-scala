/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.{Gaze, Step, takeCharacters, takeString, takeWhile}
import dev.ligature.{Identifier, IntegerLiteral, Statement, Value}

case class LigError(val message: String)

class LigReader {
    private val whiteSpaceStep = takeCharacters(' ', '\t')
    private val whiteSpaceAndNewLineStep = takeCharacters(' ', '\t', '\n')
 
    def parse(input: String): Iterator[Statement] = {
        val gaze = Gaze.from(input)
        val statements: ArrayBuffer[Statement] = ArrayBuffer()
        while (!gaze.isComplete()) {
            parseStatement(gaze) match {
                case Left(resStatement) => throw RuntimeException(resStatement.message)
                case Right(resStatement) => statements.append(resStatement)
            }
        }
        return statements.iterator
    }

    def parseStatement(gaze: Gaze[Char]): Either[LigError, Statement] = {
        val res = for {
            _ <- gaze.attempt(whiteSpaceAndNewLineStep)
            entity <- parseIdentifier(gaze)
            _ <- gaze.attempt(whiteSpaceStep)
            attribute <- parseIdentifier(gaze)
            _ <- gaze.attempt(whiteSpaceStep)
            value <- parseValue(gaze)
            _ <- gaze.attempt(whiteSpaceStep)
            context <- parseIdentifier(gaze)
            _ <- gaze.attempt(whiteSpaceAndNewLineStep)
        } yield ((Statement(entity, attribute, value, context)))
        res.left.map(_ => LigError("Could not create Statement."))
    }

    def parseIdentifier(gaze: Gaze[Char]): Either[LigError, Identifier] = {
        val openIdentifierStep = takeString("<")
        val closeIdentifierStep = takeString(">")

        val id = for {
            _ <- gaze.attempt(openIdentifierStep)
            id <- gaze.attempt(identifierStep)
            _ <- gaze.attempt(closeIdentifierStep)
        } yield id//.left.map(_ => LigError("Could not create Identifier."))

        id match {
            case Right(id) => createIdentifier(id)
            case Left(_) => Left(LigError("Could not create Identifier."))
        }
    }

    def createIdentifier(id: String): Either[LigError, Identifier] =
        Identifier.fromString(id).left.map(_ => LigError("Invalid Identifier Id - $id"))

    def parseValue(gaze: Gaze[Char]): Either[LigError, Value] = {
        val entityRes = parseIdentifier(gaze)
        if (entityRes.isRight) return entityRes

        val integerRes = parseIntegerLiteral(gaze)
        if (integerRes.isRight) return integerRes

        val stringRes = parseStringLiteral(gaze)
        if (stringRes.isRight) return stringRes

        return Left(LigError("Unsupported Value\n${gaze.remainingText()}"))
    }

    def parseIntegerLiteral(gaze: Gaze[Char]): Either[LigError, IntegerLiteral] = {
        val numberStep = takeCharacters(Range(0,9).map(d => d.toChar).toList*)

        gaze.attempt(numberStep) match {
            case Left(_) => Left(LigError("Could not parse Integer."))
            case Right(i) => Right(IntegerLiteral(i.toLong)) //TODO toLong can throw
        }
    }

    def parseStringLiteral(gaze: Gaze[Char]): Either[LigError, StringLiteral] = {
        val quote = takeString("\"")

        return when (val res = gaze.nibble(quote, stringContentStep, quote)) {
            is None -> Either.Left(LigError("Could not parse String."))
            is Some -> Either.Right(StringLiteral(res.value[1].value))
        }
    }

    private val identifierStep = takeWhile { c =>
        true
    }

    private val stringContentStep = Step { lookAhead ->
        //Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
        val commandChars = 0x00.toChar() to 0x1F.toChar()
        val validHexChar = { c: Char? -> c != null && ( c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F' ) }

        var offset = 0
        var fail = false
        while (lookAhead.peek(offset) != null) {
            val c = lookAhead.peek(offset)!!
            if (commandChars.contains(c)) {
                fail = true
                break
            } else if (c == '"') {
                break
            } else if (c == '\\') {
                when (lookAhead.peek(offset + 1)) {
                    '\\', '"', 'b', 'f', 'n', 'r', 't' -> offset = offset + 2
                    'u' -> {
                        for (i in 1..4) {
                            if (!validHexChar(lookAhead.peek(offset + 1 + i))) {
                                fail = true
                                break
                            }
                        }
                        if (fail) {
                            break
                        } else {
                            offset = offset + 5
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
        if (fail || offset == 0) {
            Cancel
        } else {
            Complete(offset.toInt())
        }
    }
}
