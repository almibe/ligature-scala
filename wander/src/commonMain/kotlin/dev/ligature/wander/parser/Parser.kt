/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.gaze.*
import dev.ligature.wander.WanderError
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.Nibblers.scriptNib

data class ParsingError(override val message: String): WanderError

fun parse(script: List<Token>): Either<ParsingError, List<Element>> {
  val filteredInput = script.filter { token: Token ->
    token !is Token.Comment && token !is Token.Spaces && token !is Token.NewLine
  }.toList()
  val gaze = Gaze(filteredInput)
  return when(val res = gaze.attempt(scriptNib)) {
    null ->
      if (gaze.isComplete) {
        Right(listOf())
      } else {
        Left(ParsingError("No Match"))
      }
    else ->
      if (gaze.isComplete) {
        Right(res) // .filter(_.isDefined).map(_.get)))
      } else {
        Left(ParsingError("Not complete - ${gaze.peek()}"))
      }
  }
}
