/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.{Gaze, NoMatch, Nibbler, between, takeAll, takeCharacters, takeString, takeWhile}
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral, Value}
import dev.ligature.lig.LigNibblers.*

case class LigError(val message: String)

def parse(input: String): Either[LigError, Iterator[Statement]] = {
    val gaze = Gaze.from(input)
    val statements: ArrayBuffer[Statement] = ArrayBuffer()
    while (!gaze.isComplete()) {
        parseStatement(gaze) match {
            case Left(resStatement) => return Left(resStatement)
            case Right(resStatement) => statements.append(resStatement)
        }
    }
    return Right(statements.iterator)
}

def parseStatement(gaze: Gaze[Char]): Either[LigError, Statement] = {
    val res = for {
        _ <- gaze.attempt(whiteSpaceAndNewLineNibbler).orElse(Right(())) //TODO this orElse should be eventually encoded in Gaze
        entity <- parseIdentifier(gaze)
        _ <- gaze.attempt(whiteSpaceNibbler)
        attribute <- parseIdentifier(gaze)
        _ <- gaze.attempt(whiteSpaceNibbler)
        value <- parseValue(gaze)
        _ <- gaze.attempt(whiteSpaceNibbler)
        context <- parseIdentifier(gaze)
        _ <- gaze.attempt(whiteSpaceAndNewLineNibbler).orElse(Right(())) //TODO this orElse should be eventually encoded in Gaze
    } yield ((Statement(entity, attribute, value, context)))
    res.left.map(_ => LigError("Could not create Statement."))
}

def parseIdentifier(gaze: Gaze[Char]): Either[LigError, Identifier] = {
    val id = gaze.attempt(identifierNibbler).map { idText => Identifier.fromString(idText)}

    id match {
        case Right(Right(id)) => Right(id)
        case Right(Left(_)) => Left(LigError("Could not create Identifier."))
        case _ => Left(LigError("Could not create Identifier."))
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
    gaze.attempt(numberNibbler) match {
        case Left(_) => Left(LigError("Could not parse Integer."))
        case Right(i) => Right(IntegerLiteral(i.toLong)) //TODO toLong can throw
    }
}

def parseStringLiteral(gaze: Gaze[Char]): Either[LigError, StringLiteral] = {
    val res = gaze.attempt(takeAll(takeString("\""), stringContentNibbler))

    res match {
        case Left(_) => Left(LigError("Could not parse String."))
        case Right(res) => Right(StringLiteral(res(1)))
    }
}
