/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import dev.ligature.gaze.*

sealed interface Token {
  data class Boolean(val value: kotlin.Boolean): Token
  object Spaces: Token
  data class Identifier(val value: String): Token
  data class Integer(val value: String): Token
  data class Comment(val value: String): Token
  object NewLine: Token
  data class StringLiteral(val value: String): Token
  data class BytesLiteral(val value: String): Token
  object LetKeyword: Token
  object EqualSign: Token
  data class Name(val value: String): Token
  object OpenBrace: Token
  object CloseBrace: Token
  object Colon: Token
  object OpenParen: Token
  object CloseParen: Token
  object Arrow: Token
  object IfKeyword: Token
  object ElseKeyword: Token
}

data class TokenizeError(val message: String)

fun tokenize(input: String): Either<TokenizeError, List<Token>> {
  val gaze = Gaze.from(input)
  return when(val res = gaze.attempt(Nibblers.tokensNib)) {
    is None ->
      if (gaze.isComplete) {
        Either.Right(listOf())
      } else {
        Either.Left(TokenizeError("Error"))
      }
    is Some ->
      if (gaze.isComplete) {
        Either.Right(res.value)
      } else {
        Either.Left(TokenizeError("Error"))
      }
  }
}
