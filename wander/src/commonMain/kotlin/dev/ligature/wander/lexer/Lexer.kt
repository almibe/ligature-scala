/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import dev.ligature.gaze.*
import dev.ligature.wander.WanderError

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
  object ElsifKeyword: Token
  object ElseKeyword: Token
}

data class TokenizeError(override val message: String): WanderError

fun tokenize(input: String): Either<TokenizeError, List<Token>> {
  val gaze = Gaze.from(input)
  return when(val res = gaze.attempt(Nibblers.tokensNib)) {
    null ->
      if (gaze.isComplete) {
        Either.Right(listOf())
      } else {
        Either.Left(TokenizeError("Error"))
      }
    else ->
      if (gaze.isComplete) {
        Either.Right(res)
      } else {
        Either.Left(TokenizeError("Error"))
      }
  }
}
