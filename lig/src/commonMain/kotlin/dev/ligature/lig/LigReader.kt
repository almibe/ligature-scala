/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.*
import dev.ligature.gaze.*
import dev.ligature.idgen.genId
import dev.ligature.lig.LigNibblers.whiteSpaceNibbler
import arrow.core.Either
import arrow.core.Option
import arrow.core.None
import arrow.core.none
import arrow.core.Some
import dev.ligature.lig.lexer.tokenize
import dev.ligature.lig.parser.parse

data class LigError(val message: String)

fun read(input: String): Either<LigError, List<Statement>> {
  val tokens = tokenize(input)
  return parse(tokens)
}

//def read(input: String): Either[LigError, List[Statement]] = {
//  val gaze = Gaze.from(input)
//  val statements: ArrayBuffer[Statement] = ArrayBuffer()
//  var continue = true
//  while (continue && !gaze.isComplete) {
//    gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
//    parseStatement(gaze) match {
//      case Left(resStatement)  => return Left(resStatement)
//      case Right(resStatement) => statements.append(resStatement)
//    }
//    val check = gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
//    if (check.isDefined && !gaze.isComplete) {
//      continue = true
//    } else {
//      continue = false
//    }
//  }
//  Right(statements.toList)
//}

//fun read(input: String): Either<LigError, List<Statement>> {
//  TODO()
//  val gaze = Gaze.from(input)
//  val model: MutableList<DLigModel> = mutableListOf()
//  val prefixes = parsePrefixes(gaze)
//  //val statements = parseStatements(gaze, prefixes)
//  var `continue` = true
//  while (`continue` && !gaze.isComplete) {
//    gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
//    parseStatement(gaze) match {
//      case Left(resStatement)  => return Left(resStatement)
//      case Right(resStatement) => statements.append(resStatement)
//    }
//    val check = gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
//    if (check.isDefined && !gaze.isComplete) {
//      `continue` = true
//    } else {
//      `continue` = false
//    }
//  }
//  return Right(model.toList)
//}

//def parseStatement(gaze: Gaze[Char]): Either[LigError, Statement] =
//  for {
//    _ <- gaze
//      .attempt(optional(whiteSpaceAndNewLineNibbler))
//      .toRight(LigError("Error parsing optional whitespace before Statement"))
//    entity <- parseIdentifier(gaze)
//    _ <- gaze
//      .attempt(whiteSpaceNibbler)
//      .toRight(LigError("Error parsing whitespace after Entity"))
//    attribute <- parseIdentifier(gaze)
//    _ <- gaze
//      .attempt(whiteSpaceNibbler)
//      .toRight(LigError("Error parsing whitespace after Attribute"))
//    value <- parseValue(gaze)
//    _ <- gaze
//      .attempt(optional(whiteSpaceAndNewLineNibbler))
//      .toRight(LigError(""))
//  } yield Statement(entity, attribute, value)

fun createIdentifier(id: String): Either<LigError, Identifier> =
  Either.Right(Identifier(id)) //TODO not sure where to do error handling
//  Identifier
//    .fromString(id)
//    .left
//    .map(_ => LigError("Invalid Identifier Id - $id"))

//fun parseIntegerLiteral(gaze: Gaze<Char>): Either<LigError, IntegerLiteral> =
//  when(val res = gaze.attempt(numberNibbler)) {
//    is None -> Either.Left(LigError("Could not parse Integer."))
//    is Some ->
//      Either.Right(IntegerLiteral(res.value.joinToString("").toLong())) // TODO toLong can throw
//  }

//fun parseStringLiteral(gaze: Gaze<Char>): Either<LigError, StringLiteral> =
//  when(val res = gaze.attempt(takeAllGrouped(takeString("\""), stringContentNibbler))) {
//    is None -> Either.Left(LigError("Could not parse String."))
//    is Some -> Either.Right(StringLiteral(res.value[1].joinToString("")))
//  }

fun parsePrefixes(gaze: Gaze<Char>): Either<LigError, Map<String, String>> {
  TODO()
//  val result = HashMap[String, String]()
//  while (!gaze.isComplete)
//    parsePrefix(gaze) match {
//      case Left(err)   => return Left(err)
//      case Right(None) => return Right(result.toMap)
//      case Right(Some(pair)) =>
//        if (result.contains(pair._1)) {
//          return Left(LigError(s"Duplicate Prefix Name: ${pair._1}"))
//        } else {
//          result.addOne(pair)
//        }
//    }
//  Right(result.toMap)
}

//TODO: this function never returns an Error so if there is a malformed prefix it won't be caught until we try to read a Statement
fun parsePrefix(
    gaze: Gaze<Char>
): Either<LigError, Option<Pair<String, String>>> {
  val parseResult = gaze.attempt(
    takeAllGrouped(
      takeString("prefix"),
      whiteSpaceNibbler,
      LigNibblers.prefixNameNibbler,
      whiteSpaceNibbler,
      takeString("="),
      whiteSpaceNibbler,
      takeWhile { c ->
        Regex("[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]").matches(c.toString())
      }
    )
  )
  return when(parseResult) {
    is None -> Either.Right(None)
    is Some -> {
      val prefixName = parseResult.value[2].joinToString("")
      val prefixValue = parseResult.value[6].joinToString("")
      Either.Right(Some(Pair(prefixName, prefixValue)))
    }
  }
}

fun parseStatements(
    gaze: Gaze<Char>,
    prefixes: Map<String, String>
): Either<LigError, List<Statement>> {
  val statements = mutableListOf<Statement>()
  var lastStatement: Option<Statement> = none()
  while (!gaze.isComplete)
    when(val res = parseStatement(gaze, prefixes, lastStatement)) {
      is Either.Left  -> return res
      is Either.Right -> {
        val statement = res.value
        lastStatement = Some(statement)
        statements.add(statement)
      }
    }
  return Either.Right(statements.toList())
}

fun parseStatement(
    gaze: Gaze<Char>,
    prefixes: Map<String, String>,
    lastStatement: Option<Statement>
): Either<LigError, Statement> = TODO()
//  for {
//    _ <- gaze
//      .attempt(optional(whiteSpaceAndNewLineNibbler))
//      .toRight(LigError("Error parsing optional whitespace before Statement"))
//    entity <- parseIdentifier(gaze, prefixes, lastEntity(lastStatement))
//    _ <- gaze
//      .attempt(whiteSpaceNibbler)
//      .toRight(LigError("Error parsing whitespace after Entity"))
//    attribute <- parseIdentifier(gaze, prefixes, lastAttribute(lastStatement))
//    _ <- gaze
//      .attempt(whiteSpaceNibbler)
//      .toRight(LigError("Error parsing whitespace after Attribute"))
//    value <- parseValue(gaze, prefixes, lastValue(lastStatement))
//    _ <- gaze
//      .attempt(optional(whiteSpaceAndNewLineNibbler))
//      .toRight(LigError(""))
//  } yield Statement(entity, attribute, value)

fun lastEntity(lastStatement: Option<Statement>): Option<Identifier> =
  when(lastStatement) {
    is None -> none()
    is Some -> Some(lastStatement.value.entity)
  }

fun lastAttribute(lastStatement: Option<Statement>): Option<Identifier> =
  when(lastStatement) {
    is None -> none()
    is Some -> Some(lastStatement.value.attribute)
  }

fun lastValue(lastStatement: Option<Statement>): Option<Value> =
  when(lastStatement) {
    is None -> none()
    is Some -> Some(lastStatement.value.value)
  }

fun parseIdentifier(
    gaze: Gaze<Char>,
    prefixes: Map<String, String>,
    lastIdentifier: Option<Identifier>
): Either<LigError, Identifier> {
  TODO()
  // attempt copy character
//  val copyChar = gaze.attempt(LigNibblers.copyNibbler)
//  if (copyChar.isDefined) {
//    lastIdentifier match {
//      case None =>
//        return Left(
//          LigError("Can't Use Copy Character Without Existing Instance.")
//        )
//      case Some(id) => return Right(id)
//    }
//  }
//  val idGenId = gaze.attempt(
//    between(
//      takeString("<"),
//      repeat(
//        takeFirst(
//          takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString())),
//          takeString("{}")
//        )
//      ),
//      takeString(">")
//    )
//  )
//  if (idGenId.isDefined) {
//    handleIdGenId(idGenId.get.joinToString("")) match {
//      case Right(id) => return Right(id)
//      case Left(err) => return Left(err)
//    }
//  }
//  val prefixedGenId = gaze.attempt(
//    takeAllGrouped(
//      LigNibblers.prefixNameNibbler,
//      takeString(":"),
//      repeat(
//        takeFirst(
//          takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString())),
//          takeString("{}")
//        )
//      )
//    )
//  )
//  if (prefixedGenId.isDefined) {
//    handlePrefixedGenId(prefixedGenId.get, prefixes) match {
//      case Right(id) => return Right(id)
//      case Left(err) => return Left(err)
//    }
//  }
//
//  Left(LigError(s"Could not match Identifier. ${gaze.location}"))
}

fun handleIdGenId(input: String): Either<LigError, Identifier> = TODO()
  //Identifier.fromString(genIdId(input)).left.map(err => LigError(err.message))

fun genIdId(input: String): String {
  val itr = input.toCharArray().iterator()
  val sb = StringBuilder()
  while (itr.hasNext())
    when(val c = itr.next()) {
      '{' -> {
        itr.next() // eat }, TODO should probably assert here
        sb.append(genId())
      }
      else -> sb.append(c)
    }
  return sb.toString()
}

fun handlePrefixedId(
    input: List<List<Char>>,
    prefixes: Map<String, String>
): Either<LigError, Identifier> {
  val prefixName = input[0].joinToString("")
  return when(val res = prefixes.get(prefixName)) {
    null -> Either.Left(LigError("Prefix Name $prefixName, doesn't exist."))
    else -> {
      val postfix = input[2].joinToString("")
      Either.Right(Identifier(res + postfix)) //TODO not validated
    }
  }
}

fun handlePrefixedGenId(
    input: List<List<Char>>,
    prefixes: Map<String, String>
): Either<LigError, Identifier> {
  val prefixName = input[0].joinToString("")
  return when(val prefixValue = prefixes.get(prefixName)) {
    null -> Either.Left(LigError("Prefix name $prefixName, doesn't exist."))
    else -> {
      val postfix = input[2].joinToString("")
      Either.Right(Identifier(genIdId(prefixValue + postfix))) //TODO not validated
//        .fromString(genIdId(prefixValue + postfix))
//        .left
//        .map(err => LigError(err.message))
    }
  }
}

//fun parseValue(
//    gaze: Gaze<Char>,
//    prefixes: Map<String, String>,
//    lastValue: Option<Value>
//): Either<LigError, Value> {
  //TODO attempt copy character
//  val copyChar = gaze.attempt(LigNibblers.copyNibbler)
//  if (copyChar is Some<Char>) {
//    when(lastValue) {
//      is None -> return Either.Left(
//        LigError("Can't Use Copy Character Without Existing Instance.")
//      )
//      is Some -> return Either.Right(lastValue.value)
//    }
//  }

//  val entityRes = parseIdentifier(
//    gaze,
//    prefixes,
//    None
//  ) // can be None since copy character has been checked for
//  if (entityRes is Either.Left) return entityRes
//
//  val integerRes = parseIntegerLiteral(gaze)
//  if (integerRes is Either.Right)
//    return integerRes //integerRes.left.map(err => LigError(err.message))
//
////  val stringRes = parseStringLiteral(gaze)
//  if (stringRes is Either.Right)
//    return stringRes //stringRes.left.map(err => LigError(err.message))
//
//  return Either.Left(LigError("Unsupported Value."))
//}
