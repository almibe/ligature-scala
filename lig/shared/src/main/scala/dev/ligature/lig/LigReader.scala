/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  between,
  optional,
  takeAll,
  takeAllGrouped,
  takeCharacters,
  takeString,
  takeWhile
}
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral, Value}
import dev.ligature.lig.LigNibblers.*

case class LigError(message: String)

def read(input: String): Either[LigError, List[Statement]] = {
  val gaze = Gaze.from(input)
  val statements: ArrayBuffer[Statement] = ArrayBuffer()
  var continue = true
  while (continue && !gaze.isComplete()) {
    gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
    parseStatement(gaze) match {
      case Left(resStatement)  => return Left(resStatement)
      case Right(resStatement) => statements.append(resStatement)
    }
    val check = gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
    if (check.isDefined && !gaze.isComplete()) {
      continue = true
    } else {
      continue = false
    }
  }
  Right(statements.toList)
}

def parseStatement(gaze: Gaze[Char]): Either[LigError, Statement] =
  for {
    _ <- gaze
      .attempt(optional(whiteSpaceAndNewLineNibbler))
      .toRight(LigError("Error parsing optional whitespace before Statement"))
    entity <- parseIdentifier(gaze)
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(LigError("Error parsing whitespace after Entity"))
    attribute <- parseIdentifier(gaze)
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(LigError("Error parsing whitespace after Attribute"))
    value <- parseValue(gaze)
    _ <- gaze
      .attempt(optional(whiteSpaceAndNewLineNibbler))
      .toRight(LigError(""))
  } yield Statement(entity, attribute, value)

def parseIdentifier(gaze: Gaze[Char]): Either[LigError, Identifier] = {
  val id = gaze.attempt(identifierNibbler).map { idText =>
    createIdentifier(idText.mkString)
  }

  id match {
    case Some(Right(id)) => Right(id)
    case Some(Left(err)) => Left(err)
    case _               => Left(LigError("Could not match Identifier."))
  }
}

def createIdentifier(id: String): Either[LigError, Identifier] =
  Identifier
    .fromString(id)
    .left
    .map(_ => LigError("Invalid Identifier Id - $id"))

def parseValue(gaze: Gaze[Char]): Either[LigError, Value] = {
  val entityRes = parseIdentifier(gaze)
  if (entityRes.isRight) return entityRes

  val integerRes = parseIntegerLiteral(gaze)
  if (integerRes.isRight) return integerRes

  val stringRes = parseStringLiteral(gaze)
  if (stringRes.isRight) return stringRes

  Left(LigError("Unsupported Value."))
}

def parseIntegerLiteral(gaze: Gaze[Char]): Either[LigError, IntegerLiteral] =
  gaze.attempt(numberNibbler) match {
    case None => Left(LigError("Could not parse Integer."))
    case Some(i) =>
      Right(IntegerLiteral(i.mkString.toLong)) // TODO toLong can throw
  }

def parseStringLiteral(gaze: Gaze[Char]): Either[LigError, StringLiteral] = {
  val res = gaze.attempt(takeAllGrouped(takeString("\""), stringContentNibbler))

  res match {
    case None      => Left(LigError("Could not parse String."))
    case Some(res) => Right(StringLiteral(res(1).mkString))
  }
}
