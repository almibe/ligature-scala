/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import dev.ligature.gaze.{
  Gaze,
  takeAll,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  repeat
}
import dev.ligature.lig.LigNibblers

enum TokenType:
  case Boolean, Spaces, Identifier, Integer, Comment, NewLine, String,
  LetKeyword, EqualSign, Name

case class Token(val content: String, val tokenType: TokenType)

case class TokenizeError(message: String)

def tokenize(input: String): Either[TokenizeError, List[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case Left(err)  => Left(TokenizeError("Error"))
    case Right(res) => Right(res)
  }
}

val stringTokenNib =
  LigNibblers.stringNibbler.map(results => Token(results(1), TokenType.String))

val newLineTokenNib = takeString("\n").map(Token(_, TokenType.NewLine))

val commentTokenNib = takeAll(takeString("#"), takeUntil('\n')).map(results =>
  Token(results.mkString, TokenType.Comment)
)

val booleanTokenNib = takeFirst(takeString("true"), takeString("false")).map(
  Token(_, TokenType.Boolean)
)

val integerTokenNib = LigNibblers.numberNibbler.map(Token(_, TokenType.Integer))

val spacesTokenNib = takeWhile(_ == ' ').map(Token(_, TokenType.Spaces))

val identifierTokenNib =
  LigNibblers.identifierNibbler.map(Token(_, TokenType.Identifier))

val tokensNib = repeat(
  takeFirst(
    spacesTokenNib,
    booleanTokenNib,
    integerTokenNib,
    newLineTokenNib,
    identifierTokenNib,
    stringTokenNib,
    commentTokenNib
  )
)
