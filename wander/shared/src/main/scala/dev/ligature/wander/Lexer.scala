/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

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
import dev.ligature.LigatureError
import dev.ligature.wander.parser.ScriptError
import dev.ligature.Identifier

enum Token:
  case BooleanLiteral(value: Boolean)
  case Spaces(value: String)
  case Identifier(value: dev.ligature.Identifier)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String)
  case Name(value: String)
  case OpenBrace, CloseBrace, Colon, OpenParen, CloseParen, NewLine,
    Arrow, IfKeyword, ElseKeyword, EqualSign, LetKeyword, Comment

def tokenize(input: String): Either[ScriptError, Seq[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case None =>
      if (gaze.isComplete) {
        Right(List())
      } else {
        Left(ScriptError("Error"))
      }
    case Some(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(ScriptError("Error"))
      }
  }
}

val stringTokenNib =
  LigNibblers.stringNibbler.map(results => Seq(Token.StringLiteral(results(1).mkString)))

//NOTE: New lines are hard coded as \n because sometimes on Windows
//the two types of new lines get mixed up in the codebase between the editor and Scalafmt.
//Not ideal, but it works consistently at least.
val newLineTokenNib =
  takeFirst(takeString("\n"), takeString("\r\n")).map(res => Seq(Token.NewLine))

val commentTokenNib = takeAll(
  takeString("--"),
  takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
).map(results => Seq(Token.Comment))

/** This nibbler matches both names and keywords. After the initial match all
  * keywords are checked and if none match and name is returned.
  */
val nameTokenNib = takeAll(
  takeCond((c: Char) => c.isLetter || c == '_'),
  optional(takeWhile[Char]((c: Char) => c.isLetter || c.isDigit || c == '_'))
)
  .map { value =>
    value.mkString match {
      case "let"         => Seq(Token.LetKeyword)
      case "if"          => Seq(Token.IfKeyword)
      case "else"        => Seq(Token.ElseKeyword)
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

val openParenTokenNib =
  takeString("(").map(res => Seq(Token.OpenParen))

val closeParenTokenNib =
  takeString(")").map(res => Seq(Token.CloseParen))

val arrowTokenNib =
  takeString("->").map(res => Seq(Token.Arrow))

val integerTokenNib =
  LigNibblers.numberNibbler.map(res => Seq(Token.IntegerLiteral(res.mkString.toLong)))

val spacesTokenNib =
  takeWhile[Char](_ == ' ').map(res => Seq(Token.Spaces(res.mkString)))

val identifierTokenNib =
  LigNibblers.identifierNibbler.map(res =>
    Seq(Token.Identifier(Identifier.fromString(res.mkString).getOrElse(???)))
  )

val tokensNib: Nibbler[Char, Token] = repeat(
  takeFirst(
    spacesTokenNib,
    nameTokenNib,
    colonTokenNib,
    openParenTokenNib,
    closeParenTokenNib,
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
