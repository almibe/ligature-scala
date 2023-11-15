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

enum Token:
  case BooleanLiteral(value: Boolean)
  case Spaces(value: String)
  case Identifier(value: dev.ligature.wander.Identifier)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String)
  case Name(value: String)
  case OpenBrace, CloseBrace, Colon, OpenParen, CloseParen, NewLine,
    Arrow, IfKeyword, ElseKeyword, EqualSign, LetKeyword, Comment,
    OpenBracket, CloseBracket, NothingKeyword, QuestionMark, InKeyword,
    EndKeyword, ThenKeyword, Period, Backtick, Hash, Lambda, Pipe

def tokenize(input: String): Either[WanderError, Seq[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case None =>
      if (gaze.isComplete) {
        Right(List())
      } else {
        Left(WanderError("Error tokenizing."))
      }
    case Some(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError("Error tokenizing."))
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
      case "end"         => Seq(Token.EndKeyword)
      case "in"          => Seq(Token.InKeyword)
      case "then"        => Seq(Token.ThenKeyword)
      case "else"        => Seq(Token.ElseKeyword)
      case "true"        => Seq(Token.BooleanLiteral(true))
      case "false"       => Seq(Token.BooleanLiteral(false))
      case "nothing"     => Seq(Token.NothingKeyword)
      case value: String => Seq(Token.Name(value))
    }
  }

val questionMarkNib =
  takeString("?").map(res => Seq(Token.QuestionMark))

val equalSignTokenNib =
  takeString("=").map(res => Seq(Token.EqualSign))

val colonTokenNib =
  takeString(":").map(res => Seq(Token.Colon))

val openBraceTokenNib =
  takeString("{").map(res => Seq(Token.OpenBrace))

val closeBraceTokenNib =
  takeString("}").map(res => Seq(Token.CloseBrace))

val openBracketTokenNib =
  takeString("[").map(res => Seq(Token.OpenBracket))

val closeBracketTokenNib =
  takeString("]").map(res => Seq(Token.CloseBracket))

val openParenTokenNib =
  takeString("(").map(res => Seq(Token.OpenParen))

val closeParenTokenNib =
  takeString(")").map(res => Seq(Token.CloseParen))

val hashTokenNib =
  takeString("#").map(res => Seq(Token.Hash))

val backtickTokenNib =
  takeString("`").map(res => Seq(Token.Backtick))

val arrowTokenNib =
  takeString("->").map(res => Seq(Token.Arrow))

val lambdaTokenNib =
  takeString("\\").map(res => Seq(Token.Lambda))

val integerTokenNib =
  LigNibblers.numberNibbler.map(res => Seq(Token.IntegerLiteral(res.mkString.toLong)))

val spacesTokenNib =
  takeWhile[Char](_ == ' ').map(res => Seq(Token.Spaces(res.mkString)))

val identifierTokenNib =
  LigNibblers.identifierNibbler.map(res => Seq(Token.Identifier(dev.ligature.wander.Identifier.fromString(res.mkString).getOrElse(???))))

val tokensNib: Nibbler[Char, Token] = repeat(
  takeFirst(
    spacesTokenNib,
    nameTokenNib,
    colonTokenNib,
    openParenTokenNib,
    closeParenTokenNib,
    arrowTokenNib,
    lambdaTokenNib,
    integerTokenNib,
    newLineTokenNib,
    identifierTokenNib,
    openBraceTokenNib,
    closeBraceTokenNib,
    openBracketTokenNib,
    closeBracketTokenNib,
    backtickTokenNib,
    stringTokenNib,
    commentTokenNib,
    equalSignTokenNib,
    questionMarkNib,
    hashTokenNib,
  )
)
