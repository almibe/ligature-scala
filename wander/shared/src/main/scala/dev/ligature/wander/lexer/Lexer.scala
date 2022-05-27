/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

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

enum TokenType:
  case Boolean, Spaces, Identifier, Integer, Comment, NewLine, String,
    LetKeyword, EqualSign, Name, OpenBrace, CloseBrace, Colon, OpenParen, CloseParen,
    Arrow, IfKeyword, ElseKeyword

case class Token(content: String, tokenType: TokenType)

case class TokenizeError(message: String)

def tokenize(input: String): Either[TokenizeError, Seq[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case None      =>
      if (gaze.isComplete()) {
        Right(List())
      } else {
        Left(TokenizeError("Error"))
      }
    case Some(res) =>
      if (gaze.isComplete()) {
        Right(res)
      } else {
        Left(TokenizeError("Error"))
      }
  }
}

val stringTokenNib =
  LigNibblers.stringNibbler.map(results =>
    Seq(Token(results(1).mkString, TokenType.String))
  )

val newLineTokenNib =
  takeFirst(takeString("\n"), takeString("\r\n")).map(res => Seq(Token(res.mkString, TokenType.NewLine)))

val commentTokenNib = takeAll(takeString("#"), takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))).map(results =>
  Seq(Token(results.mkString, TokenType.Comment))
)

/** This nibbler matches both names and keywords. After the initial match all
  * keywords are checked and if none match and name is returned.
  */
val nameTokenNib = takeAll(
  takeCond { (c: Char) => c.isLetter || c == '_' },
  optional(takeWhile[Char] { (c: Char) => c.isLetter || c.isDigit || c == '_' })
)
  .map { value =>
    value.mkString match {
      case "let"         => Seq(Token("let", TokenType.LetKeyword))
      case "if"          => Seq(Token("if", TokenType.IfKeyword))
      case "else"        => Seq(Token("else", TokenType.ElseKeyword))
      case "true"        => Seq(Token("true", TokenType.Boolean))
      case "false"       => Seq(Token("false", TokenType.Boolean))
      case value: String => Seq(Token(value, TokenType.Name))
    }
  }

val equalSignTokenNib =
  takeString("=").map(res => Seq(Token(res.mkString, TokenType.EqualSign)))

val colonTokenNib =
  takeString(":").map(res => Seq(Token(res.mkString, TokenType.Colon)))

val openBraceTokenNib =
  takeString("{").map(res => Seq(Token(res.mkString, TokenType.OpenBrace)))

val closeBraceTokenNib =
  takeString("}").map(res => Seq(Token(res.mkString, TokenType.CloseBrace)))

val openParenTokenNib =
  takeString("(").map(res => Seq(Token(res.mkString, TokenType.OpenParen)))

val closeParenTokenNib =
  takeString(")").map(res => Seq(Token(res.mkString, TokenType.CloseParen)))

val arrowTokenNib =
  takeString("->").map(res => Seq(Token(res.mkString, TokenType.Arrow)))

val integerTokenNib = LigNibblers.numberNibbler.map(res =>
  Seq(Token(res.mkString, TokenType.Integer))
)

val spacesTokenNib = takeWhile[Char](_ == ' ').map(res =>
  Seq(Token(res.mkString, TokenType.Spaces))
)

val identifierTokenNib =
  LigNibblers.identifierNibbler.map(res =>
    Seq(Token(res.mkString, TokenType.Identifier))
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
