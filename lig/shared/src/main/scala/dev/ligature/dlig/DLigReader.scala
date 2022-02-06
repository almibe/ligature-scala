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
  createIdentifier,
  parseIntegerLiteral,
  parseStringLiteral
}
import dev.ligature.idgen.genId
import scala.collection.mutable.HashMap

case class DLigError(val message: String)

def readDLig(input: String): Either[DLigError, List[Statement]] = {
  val gaze = Gaze.from(input)
  // val model: ArrayBuffer[DLigModel] = ArrayBuffer()
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
      case Left(err)   => return Left(err)
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
def parsePrefix(
    gaze: Gaze[Char]
): Either[DLigError, Option[(String, String)]] = {
  val parseResult = gaze.attempt(
    takeAllGrouped(
      takeString("prefix"),
      whiteSpaceNibbler,
      DLigNibblers.prefixNameNibbler,
      whiteSpaceNibbler,
      takeString("="),
      whiteSpaceNibbler,
      takeWhile { c =>
        "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
      }
    )
  )
  parseResult match {
    case None => Right(None)
    case Some(p) => {
      val prefixName = p(2).mkString
      val prefixValue = p(6).mkString
      Right(Some((prefixName, prefixValue)))
    }
  }
}

def parseDLigStatements(
    gaze: Gaze[Char],
    prefixes: Map[String, String]
): Either[DLigError, List[Statement]] = {
  val statements = ArrayBuffer[Statement]()
  var lastStatement: Option[Statement] = None
  while (!gaze.isComplete()) {
    parseDLigStatement(gaze, prefixes, lastStatement) match {
      case Left(err) => return Left(err)
      case Right(statement) => {
        lastStatement = Some(statement)
        statements.addOne(statement)
      }
    }
  }
  return Right(statements.toList)
}

def parseDLigStatement(
    gaze: Gaze[Char],
    prefixes: Map[String, String],
    lastStatement: Option[Statement]
): Either[DLigError, Statement] = {
  for {
    _ <- gaze
      .attempt(optional(whiteSpaceAndNewLineNibbler))
      .toRight(DLigError("Error parsing optional whitespace before Statement"))
    entity <- parseIdentifier(gaze, prefixes, lastEntity(lastStatement))
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(DLigError("Error parsing whitespace after Entity"))
    attribute <- parseIdentifier(gaze, prefixes, lastAttribute(lastStatement))
    _ <- gaze
      .attempt(whiteSpaceNibbler)
      .toRight(DLigError("Error parsing whitespace after Attribute"))
    value <- parseValue(gaze, prefixes, lastValue(lastStatement))
    _ <- gaze
      .attempt(optional(whiteSpaceAndNewLineNibbler))
      .toRight(DLigError(""))
  } yield (Statement(entity, attribute, value))
}

def lastEntity(lastStatement: Option[Statement]): Option[Identifier] = {
  lastStatement match {
    case None            => None
    case Some(statement) => Some(statement.entity)
  }
}

def lastAttribute(lastStatement: Option[Statement]): Option[Identifier] = {
  lastStatement match {
    case None            => None
    case Some(statement) => Some(statement.attribute)
  }
}

def lastValue(lastStatement: Option[Statement]): Option[Value] = {
  lastStatement match {
    case None            => None
    case Some(statement) => Some(statement.value)
  }
}

def parseIdentifier(
    gaze: Gaze[Char],
    prefixes: Map[String, String],
    lastIdentifier: Option[Identifier]
): Either[DLigError, Identifier] = {
  // attempt copy character
  val copyChar = gaze.attempt(DLigNibblers.copyNibbler)
  if (copyChar.isDefined) {
    lastIdentifier match {
      case None =>
        return Left(
          DLigError("Can't Use Copy Character Without Existing Instance.")
        )
      case Some(id) => return Right(id)
    }
  }
  val idGenId = gaze.attempt(
    between(
      takeString("<"),
      repeat(
        takeFirst(
          takeWhile(c =>
            "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
          ),
          takeString("{}")
        )
      ),
      takeString(">")
    )
  )
  if (idGenId.isDefined) {
    handleIdGenId(idGenId.get.mkString) match {
      case Right(id) => return Right(id)
      case Left(err) => return Left(err)
    }
  }
  val prefixedGenId = gaze.attempt(
    takeAllGrouped(
      DLigNibblers.prefixNameNibbler,
      takeString(":"),
      repeat(
        takeFirst(
          takeWhile(c =>
            "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
          ),
          takeString("{}")
        )
      )
    )
  )
  if (prefixedGenId.isDefined) {
    handlePrefixedGenId(prefixedGenId.get, prefixes) match {
      case Right(id) => return Right(id)
      case Left(err) => return Left(err)
    }
  }

  return Left(DLigError("Could not match Identifier."))
}

def handleIdGenId(input: String): Either[DLigError, Identifier] = {
  Identifier.fromString(genIdId(input)).left.map(err => DLigError(err.message))
}

def genIdId(input: String): String = {
  val itr = input.toCharArray.iterator
  val sb = StringBuilder()
  while (itr.hasNext) {
    itr.next match {
      case c: Char if c == '{' => {
        itr.next // eat }, TODO should probably assert here
        sb.append(genId())
      }
      case c: Char => { sb.append(c) }
    }
  }
  sb.toString
}

def handlePrefixedId(
    input: Seq[Seq[Char]],
    prefixes: Map[String, String]
): Either[DLigError, Identifier] = {
  val prefixName = input(0).mkString
  prefixes.get(prefixName) match {
    case None => Left(DLigError(s"Prefix Name $prefixName, Doesn't Exist."))
    case Some(prefixValue) => {
      val postfix = input(2).mkString
      Identifier
        .fromString(prefixValue + postfix)
        .left
        .map(err => DLigError(err.message))
    }
  }
}

def handlePrefixedGenId(
    input: Seq[Seq[Char]],
    prefixes: Map[String, String]
): Either[DLigError, Identifier] = {
  val prefixName = input(0).mkString
  prefixes.get(prefixName) match {
    case None => Left(DLigError(s"Prefix Name $prefixName, Doesn't Exist."))
    case Some(prefixValue) => {
      val postfix = input(2).mkString
      Identifier
        .fromString(genIdId(prefixValue + postfix))
        .left
        .map(err => DLigError(err.message))
    }
  }
}

def parseValue(
    gaze: Gaze[Char],
    prefixes: Map[String, String],
    lastValue: Option[Value]
): Either[DLigError, Value] = {
  // attempt copy character
  val copyChar = gaze.attempt(DLigNibblers.copyNibbler)
  if (copyChar.isDefined) {
    lastValue match {
      case None =>
        return Left(
          DLigError("Can't Use Copy Character Without Existing Instance.")
        )
      case Some(id) => return Right(id)
    }
  }

  val entityRes = parseIdentifier(
    gaze,
    prefixes,
    None
  ) // can be None since copy character has been checked for
  if (entityRes.isRight) return entityRes

  val integerRes = parseIntegerLiteral(gaze)
  if (integerRes.isRight)
    return integerRes.left.map(err => DLigError(err.message))

  val stringRes = parseStringLiteral(gaze)
  if (stringRes.isRight)
    return stringRes.left.map(err => DLigError(err.message))

  return Left(DLigError("Unsupported Value."))
}
