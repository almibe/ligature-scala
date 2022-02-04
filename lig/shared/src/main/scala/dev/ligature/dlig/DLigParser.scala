/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

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
import dev.ligature.{
  Identifier,
  IntegerLiteral,
  Statement,
  StringLiteral,
  Value
}
import dev.ligature.lig.LigNibblers.{
  whiteSpaceAndNewLineNibbler,
  whiteSpaceNibbler
}
import dev.ligature.dlig.DLigNibblers.{
  identifierNibbler
}
import dev.ligature.lig.{
  createIdentifier,
  parseValue
}

case class DLigError(val message: String)

def parse(input: String): Either[DLigError, List[DLigModel]] = {
  val gaze = Gaze.from(input)
  val model: ArrayBuffer[DLigModel] = ArrayBuffer()
//   var continue = true
//   while (continue && !gaze.isComplete()) {
//     gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
//     parseStatement(gaze) match {
//       case Left(resStatement)  => return Left(resStatement)
//       case Right(resStatement) => statements.append(resStatement)
//     }
//     val check = gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
//     if (check.isDefined && !gaze.isComplete()) {
//       continue = true
//     } else {
//       continue = false
//     }
//   }
  return Right(model.toList)
}

def parseStatement(gaze: Gaze[Char]): Either[DLigError, Statement] = {
  for {
    _ <- gaze
      .attempt(optional(whiteSpaceAndNewLineNibbler))
      .toRight(DLigError("Error parsing optional whitespace before Statement"))
    entity <- parseIdentifier(gaze)
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(DLigError("Error parsing whitespace after Entity"))
    attribute <- parseIdentifier(gaze)
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(DLigError("Error parsing whitespace after Attribute"))
    value <- parseValue(gaze).left.map(err => DLigError(err.message))
    // _ <- gaze.attempt(whiteSpaceNibbler).toRight(LigError(""))
  } yield (Statement(entity, attribute, value))
}

def parseIdentifier(gaze: Gaze[Char]): Either[DLigError, Identifier] = {
  val id = gaze.attempt(identifierNibbler).map { idText =>
    createIdentifier(idText.mkString)
  }

  id match {
    case Some(Right(id)) => Right(id)
    case Some(Left(err)) => Left(DLigError(err.message))
    case _               => Left(DLigError("Could not match Identifier."))
  }
}
