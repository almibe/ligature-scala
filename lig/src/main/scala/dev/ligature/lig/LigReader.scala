/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.{Gaze, NoMatch, Step, takeCharacters, takeString, takeWhile}
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral, Value}

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
        val numberStep = takeCharacters(('0' to '9').toSeq*)

        gaze.attempt(numberStep) match {
            case Left(_) => Left(LigError("Could not parse Integer."))
            case Right(i) => Right(IntegerLiteral(i.toLong)) //TODO toLong can throw
        }
    }

    def parseStringLiteral(gaze: Gaze[Char]): Either[LigError, StringLiteral] = {
        val quote = takeString("\"")

        val res = gaze.attempt(quote, stringContentStep)

        res match {
            case Left(_) => Left(LigError("Could not parse String."))
            case Right(res) => Right(StringLiteral(res(1)))
        }
    }

    private val identifierStep = takeWhile { c =>
        "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
    }

    private val stringContentStep = (gaze: Gaze[Char]) => {
        //Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
        val commandChars = 0x00.toChar to 0x1F.toChar
        val validHexChar = (c: Char) => { ( ('0' to '9' contains c) || ('a' to 'f' contains c) || ('A' to 'F' contains c) ) }
        val hexStep = takeWhile(validHexChar)

        var sb = StringBuilder()
        var offset = 0 //TODO delete
        var fail = false
        var complete = false
        while (!complete && !fail && gaze.peek().isDefined) {
            val c = gaze.next().get
            if (commandChars.contains(c)) {
                fail = true
            } else if (c == '"') {
                complete = true
            } else if (c == '\\') {
                sb.append(c)
                gaze.next() match {
                    case None => fail = true
                    case Some(c) => {
                        c match {
                            case '\\' | '"' | 'b' | 'f' | 'n' | 'r' | 't' => sb.append(c)
                            case 'u' => {
                                sb.append(c)
                                val res = gaze.attempt(hexStep)
                                res match {
                                    case Left(_) => fail = true
                                    case Right(res) => {
                                        if (res.length == 4) {
                                            sb.append(res)
                                        } else {
                                            fail = true
                                        }
                                    }
                                }
                            }
                            case _ => {
                                fail = true
                            }
                        }
                    }
                }
            } else {
                sb.append(c)
            }
        }
        if (fail) {
            Left(NoMatch)
        } else {
            Right(sb.toString)
        }
    }
}
