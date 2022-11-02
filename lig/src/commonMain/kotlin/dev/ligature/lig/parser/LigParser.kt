/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig.parser

import arrow.core.Either
import arrow.core.continuations.eagerEffect
import dev.ligature.*
import dev.ligature.gaze.*
import dev.ligature.idgen.genId
import dev.ligature.lig.LigError
import dev.ligature.lig.lexer.LigToken

private fun String.allIndices(pattern: String): List<Int> {
  var cont = true
  var currentIndex = 0
  val results = mutableListOf<Int>()
  while(cont) {
    val index = this.indexOf(pattern, currentIndex)
    if (index == -1) {
      cont = false
    } else {
      results.add(index)
      currentIndex = index + pattern.length
      if (currentIndex >= this.length) {
        cont = false
      }
    }
  }
  return results
}

fun parse(input: List<LigToken>): Either<LigError, List<Statement>> {
  //TODO the below is a quick work around, eventually this should use the
  //TODO generator used by the Ligature instance
  val input = input.map {
    if (it is LigToken.GeneratedIdentifier) {
      val indices = it.name.allIndices("{}")
      if (indices.size == 1) {
        LigToken.Identifier(it.name.replace("{}", genId()))
      } else {
        TODO("Return error, invalid identifier")
      }
    } else {
      it
    }
  }

  val gaze = Gaze(input)
  val statements = mutableListOf<Statement>()
  stripNewLinesAndWhiteSpace(gaze)
  var foundNewLine = true
  while (!gaze.isComplete && foundNewLine) {
    when (val res = gaze.attempt(statementNibbler)) {
      null -> TODO()
      else -> statements.addAll(res)
    }
    foundNewLine = stripNewLinesAndWhiteSpace(gaze)
  }
  return Either.Right(statements)
}

/**
 * This function removes all leading new lines and white space. It returns true if it read at least
 * one new line.
 */
private fun stripNewLinesAndWhiteSpace(gaze: Gaze<LigToken>): Boolean {
  var foundNewLine = false
  while (!gaze.isComplete) {
    if (gaze.peek() == LigToken.WhiteSpace) {
      gaze.next()
    } else if (gaze.peek() == LigToken.NewLine) {
      gaze.next()
      foundNewLine = true
    } else {
      break
    }
  }
  return foundNewLine
}

val statementNibbler: Nibbler<LigToken, Statement> =
    takeAll<LigToken, LigToken>(
            optional(take(LigToken.WhiteSpace)),
            takeCond { it is LigToken.Identifier },
            take(LigToken.WhiteSpace),
            takeCond { it is LigToken.Identifier },
            take(LigToken.WhiteSpace),
            takeFirst(
                takeCond { it is LigToken.Identifier },
                takeCond { it is LigToken.StringLiteral },
                takeCond { it is LigToken.IntegerLiteral },
                takeCond { it is LigToken.BytesLiteral },
            ),
            optional(take(LigToken.WhiteSpace)))
        .map {
          val input = it.filter { token -> token !is LigToken.WhiteSpace }
          val res =
              eagerEffect<LigError, Statement> {
                    val entity = input[0].toIdentifier().bind()
                    val attribute = input[1].toIdentifier().bind()
                    val value = input[2].toValue().bind()
                    Statement(entity, attribute, value)
                  }
                  .toEither()
          when (res) {
            is Either.Right -> listOf(res.value)
            is Either.Left -> TODO()
          }
        }

fun LigToken.toIdentifier(): Either<LigError, Identifier> =
    when (this) {
      is LigToken.Identifier -> this.toIdentifier()
      else -> Either.Left(LigError("Invalid Identifier $this."))
    }

fun LigToken.Identifier.toIdentifier(): Either<LigError, Identifier> =
    Identifier.create(name).mapLeft { LigError("Invalid Identifier $name.") }

fun LigToken.toValue(): Either<LigError, Value> =
    when (this) {
      is LigToken.Identifier -> this.toIdentifier()
      is LigToken.IntegerLiteral ->
          Either.Right(IntegerLiteral(this.value.toLong())) // TODO handle toLong errors
      is LigToken.StringLiteral -> Either.Right(StringLiteral(this.value))
      // TODO Bytes
      else ->
          Either.Left(LigError("Unknown Value Type -- $this")) // handle white space and new lines
    }
