/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.command

import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  optional,
  take,
  takeAll,
  takeCond,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  repeat
}
import dev.ligature.lig.LigNibblers

enum Token:
  case BooleanLiteral(value: Boolean)
  case Space
  case Identifier(value: String)
  case Integer(value: Long)
  case Comment(value: String)
  case NewLine
  case StringLiteral(value: String)
  case EqualSign
  case Name(value: String)
  case OpenBrace, CloseBrace, Colon, OpenParen, CloseParen, OpenSquare, CloseSquare, Arrow

def tokenize(input: String): Either[WanderError, Seq[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case None =>
      if (gaze.isComplete) {
        Right(List())
      } else {
        Left(WanderError(s"Error T1 - could not tokenize ${gaze.peek()}"))
      }
    case Some(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError(s"Error T2 - could not tokenize ${gaze.peek()}"))
      }
  }
}

val stringTokenNib =
  LigNibblers.stringNibbler.map(results => Seq(Token.StringLiteral(results(1).mkString)))

val newLineTokenNib =
  takeFirst(takeString("\n"), takeString("\r\n")).map(res => Seq(Token.NewLine))

val commentTokenNib = takeAll(
  takeString("#"),
  takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
).map(results => Seq(Token.Comment(results.mkString)))

/** This nibbler matches both names and keywords. After the initial match all
  * keywords are checked and if none match and name is returned.
  */
val nameTokenNib = takeAll(
  takeCond((c: Char) => c.isLetter || c == '_'),
  optional(takeWhile[Char]((c: Char) => c.isLetter || c.isDigit || c == '_'))
)
  .map { value =>
    value.mkString match {
      case "true"        => Seq(Token.BooleanLiteral(true))
      case "false"       => Seq(Token.BooleanLiteral(false))
      case value: String => Seq(Token.Name(value))
    }
  }

val equalSignTokenNib =
  takeString("=").map(res => Seq(Token.EqualSign))

val colonTokenNib =
  takeString(":").map(res => Seq(Token.Colon))

val openBraceTokenNib =
  takeString("{").map(res => Seq(Token.OpenBrace))

val closeBraceTokenNib =
  takeString("}").map(res => Seq(Token.CloseBrace))

val openSquareTokenNib =
  takeString("[").map(res => Seq(Token.OpenSquare))

val closeSquareTokenNib =
  takeString("]").map(res => Seq(Token.CloseSquare))

val openParenTokenNib =
  takeString("(").map(res => Seq(Token.OpenParen))

val closeParenTokenNib =
  takeString(")").map(res => Seq(Token.CloseParen))

val arrowTokenNib =
  takeString("->").map(res => Seq(Token.Arrow))

val integerTokenNib =
  LigNibblers.numberNibbler.map(res => Seq(Token.Integer(res.mkString.toLong)))

val spacesTokenNib =
  takeWhile[Char](_ == ' ').map(res => Seq(Token.Space))

val identifierTokenNib =
  LigNibblers.identifierNibbler.map(res => Seq(Token.Identifier(res.mkString)))

val tokensNib: Nibbler[Char, Token] = repeat(
  takeFirst(
    spacesTokenNib,
    nameTokenNib,
    colonTokenNib,
    openParenTokenNib,
    closeParenTokenNib,
    openSquareTokenNib,
    closeSquareTokenNib,
    arrowTokenNib,
    integerTokenNib,
    newLineTokenNib,
    identifierTokenNib,
    openBraceTokenNib,
    closeBraceTokenNib,
    stringTokenNib,
    commentTokenNib,
    equalSignTokenNib
  )
)
