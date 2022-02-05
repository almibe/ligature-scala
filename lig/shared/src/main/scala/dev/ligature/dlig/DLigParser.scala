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
  takeWhile,
  takeFirst,
  repeat
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
import dev.ligature.lig.{
  createIdentifier
}
import scala.collection.mutable.HashMap

case class DLigError(val message: String)

def readDLig(input: String): Either[DLigError, List[Statement]] = {
  val gaze = Gaze.from(input)
  //val model: ArrayBuffer[DLigModel] = ArrayBuffer()
  for {
    prefixes <- parsePrefixes(gaze)
    statements <- parseDLigStatements(gaze, prefixes)
  } yield statements
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
//  return Right(model.toList)
}

def parsePrefixes(gaze: Gaze[Char]): Either[DLigError, Map[String, String]] = {
  val result = HashMap[String, String]()
  while (!gaze.isComplete()) {
    parsePrefix(gaze) match {
      case Left(err) => return Left(err)
      case Right(None) => return Right(result.toMap)
      case Right(Some(pair)) => {
        if (result.contains(pair._1)) {
          return Left(DLigError(s"Duplicate Prefix Name: ${pair._1}"))
        } else {
          result.addOne(pair)
        }
      }
    }
  }
  return Right(result.toMap)
}

//TODO: this function never returns an Error so if there is a malformed prefix it won't be caught until we try to read a Statement
def parsePrefix(gaze: Gaze[Char]): Either[DLigError, Option[(String, String)]] = {
  val parseResult = gaze.attempt(takeAllGrouped(
    takeString("prefix"),
    whiteSpaceNibbler,
    DLigNibblers.prefixNameNibbler,
    whiteSpaceNibbler,
    takeString("="),
    whiteSpaceNibbler,
    takeWhile { c =>
      "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
    }
  ))
  parseResult match {
    case None => Right(None)
    case Some(p) => {
      val prefixName = p(2).mkString
      val prefixValue = p(6).mkString
      Right(Some((prefixName, prefixValue)))
    }
  }
}

def parseDLigStatements(gaze: Gaze[Char], prefixes: Map[String, String]): Either[DLigError, List[Statement]] = {
  val statements = ArrayBuffer[Statement]()
  while (!gaze.isComplete()) {
    parseDLigStatement(gaze, prefixes) match {
      case Left(err) => Left(err)
      case Right(statement) => statements.addOne(statement)
    }
  }
  return Right(statements.toList)
}

def parseDLigStatement(gaze: Gaze[Char], prefixes: Map[String, String]): Either[DLigError, Statement] = {
  for {
    _ <- gaze
      .attempt(optional(whiteSpaceAndNewLineNibbler))
      .toRight(DLigError("Error parsing optional whitespace before Statement"))
    entity <- parseIdentifier(gaze, prefixes)
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(DLigError("Error parsing whitespace after Entity"))
    attribute <- parseIdentifier(gaze, prefixes)
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(DLigError("Error parsing whitespace after Attribute"))
    value <- parseValue(gaze, prefixes).left.map(err => DLigError(err.message))
    // _ <- gaze.attempt(whiteSpaceNibbler).toRight(LigError(""))
  } yield (Statement(entity, attribute, value))
}

def parseIdentifier(gaze: Gaze[Char], prefixes: Map[String, String]): Either[DLigError, Identifier] = {
  //attempt regular identifier
  val id = gaze.attempt(DLigNibblers.identifierNibbler)
  if (id.isDefined) {
    Identifier.fromString(id.mkString) match {
      case Right(id) => return Right(id)
      case Left(err) => return Left(DLigError(err.message))
    }
  }
  //attempt identifier with gen id
  val idGenId = gaze.attempt(between(
    takeString("<"),
    repeat(takeFirst(takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)), takeString("{}"))),
    takeString(">")))
  if (idGenId.isDefined) {
    handleIdGenId(idGenId.get.mkString) match {
      case Right(id) => return Right(id)
      case Left(err) => return Left(err)
    }
  }
  //attempt prefixed identifier
  val prefixedId = gaze.attempt(takeAllGrouped(
    DLigNibblers.prefixNameNibbler,
    takeString(":"),
    takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString))
  ))
  if (prefixedId.isDefined) {
    handlePrefixedId(prefixedId.get, prefixes) match {
      case Right(id) => return Right(id)
      case Left(err) => return Left(err)
    }
  }
  //attempt prefixed identifier with gen id
  val prefixedGenId = gaze.attempt(takeAllGrouped(
    DLigNibblers.prefixNameNibbler,
    takeString(":"),
    repeat(takeFirst(takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)), takeString("{}")))
  ))
  if (prefixedGenId.isDefined) {
    handlePrefixedGenId(prefixedGenId.get, prefixes) match {
      case Right(id) => return Right(id)
      case Left(err) => return Left(err)
    }
  }

  return Left(DLigError("Could not match Identifier."))
}

def handleIdGenId(input: String): Either[DLigError, Identifier] = {
  ???
}

def handlePrefixedId(input: Seq[Seq[Char]], prefixes: Map[String, String]): Either[DLigError, Identifier] = {
  ???
}

def handlePrefixedGenId(input: Seq[Seq[Char]], prefixes: Map[String, String]): Either[DLigError, Identifier] = {
  ???
}

def parseValue(gaze: Gaze[Char], prefixes: Map[String, String]): Either[DLigError, Value] = {
  ???
}
