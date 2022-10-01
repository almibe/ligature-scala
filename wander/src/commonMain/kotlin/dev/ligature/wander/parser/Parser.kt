/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import arrow.core.Either
import dev.ligature.gaze.*
import dev.ligature.wander.WanderError
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.Nibblers.scriptNib

data class ParsingError(override val message: String): WanderError

fun parse(script: List<Token>): Either<ParsingError, Script> {
  val filteredInput = script.filter { token: Token ->
    token !is Token.Comment && token !is Token.Spaces && token !is Token.NewLine
  }.toList()
  val gaze = Gaze(filteredInput)
  return when(val res = gaze.attempt(scriptNib)) {
    null ->
      if (gaze.isComplete) {
        Either.Right(Script(listOf()))
      } else {
        Either.Left(ParsingError("No Match"))
      }
    else ->
      if (gaze.isComplete) {
        Either.Right(Script(res)) // .filter(_.isDefined).map(_.get)))
      } else {
        Either.Left(ParsingError("Not complete - ${gaze.peek()}"))
      }
  }
}
