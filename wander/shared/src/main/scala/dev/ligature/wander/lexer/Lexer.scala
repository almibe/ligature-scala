/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import dev.ligature.gaze.{Gaze, takeFirst, takeString, takeWhile, repeat}
import dev.ligature.lig.identifierNibbler

enum TokenType:
    case Boolean
    case Spaces
    case Identifier

case class Token(val content: String, val tokenType: TokenType)

case class TokenizeError(message: String)

def tokenize(input: String): Either[TokenizeError, List[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case Left(err) => Left(TokenizeError("Error"))
    case Right(res) => Right(res)
  }
}

val booleanTokenNib = takeFirst(takeString("true"), takeString("false")).map(Token(_, TokenType.Boolean))

val spacesNib = takeWhile(_ == ' ').map(Token(_, TokenType.Spaces))

val identifierNib = identifierNib.map()

val tokensNib = repeat(takeFirst(spacesNib, booleanTokenNib))
