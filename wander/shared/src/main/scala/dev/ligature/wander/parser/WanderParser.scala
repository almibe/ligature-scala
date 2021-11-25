/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  NoMatch,
  filter,
  matchNext,
  takeAll,
  takeFirst,
  takeString,
  repeat
}
import dev.ligature.{IntegerLiteral, Identifier, StringLiteral}
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.lexer.TokenType

def parse(script: Seq[Token]): Either[String, Script] = {
  val gaze = Gaze(script.toVector)
  val res = gaze.attempt(scriptNib)
  res match {
    case Left(err)  => Left("NoMatch")
    case Right(res) => Right(Script(res.filter(_.isDefined).map(_.get)))
  }
}

val booleanNib: Nibbler[Token, NoMatch, Expression] = matchNext[Token] {
  _.tokenType == TokenType.Boolean
}.map { (token: Token) => BooleanValue(token.content.toBoolean) }

val identifierNib: Nibbler[Token, NoMatch, Expression] = matchNext[Token] {
  _.tokenType == TokenType.Identifier
}.map { (token: Token) =>
  LigatureValue(Identifier.fromString(token.content).getOrElse(???))
}

val integerNib: Nibbler[Token, NoMatch, Expression] = matchNext[Token] {
  _.tokenType == TokenType.Integer
}.map { (token: Token) => LigatureValue(IntegerLiteral(token.content.toInt)) }

val stringNib: Nibbler[Token, NoMatch, Expression] = matchNext[Token] {
  _.tokenType == TokenType.String
}.map { (token: Token) => LigatureValue(StringLiteral(token.content)) }

val expressionNib = takeFirst(identifierNib, stringNib, integerNib, booleanNib)

val elementNib = takeFirst(expressionNib)

val scriptNib = repeat(
  filter(
    { (token: Token) =>
      token.tokenType != TokenType.Comment && token.tokenType != TokenType.Spaces && token.tokenType != TokenType.NewLine
    },
    elementNib
  )
)
