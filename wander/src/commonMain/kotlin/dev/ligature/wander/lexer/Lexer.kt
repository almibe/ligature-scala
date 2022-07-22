/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import dev.ligature.lig.LigNibblers

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import dev.ligature.gaze.*

enum class TokenType {
  Boolean, Spaces, Identifier, Integer, Comment, NewLine, String,
    LetKeyword, EqualSign, Name, OpenBrace, CloseBrace, Colon, OpenParen,
    CloseParen,
    Arrow, IfKeyword, ElseKeyword
}

data class Token(val content: String, val tokenType: TokenType)

data class TokenizeError(val message: String)

fun tokenize(input: String): Either<TokenizeError, List<Token>> {
  val gaze = Gaze.from(input)
  return when(val res = gaze.attempt(tokensNib)) {
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

val stringTokenNib =
  LigNibblers.stringNibbler.map { results -> listOf(Token(results[1].joinToString(""), TokenType.String)) }

//NOTE: New lines are hard coded as \n because sometimes on Windows
//the two types of new lines get mixed up in the codebase between the editor and Scalafmt.
//Not ideal, but it works consistently at least.
val newLineTokenNib =
  takeFirst(takeString("\n"), takeString("\r\n")).map { res ->
    listOf(Token("\n", TokenType.NewLine))
  }

val commentTokenNib = takeAll(
  takeString("#"),
  takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
).map { results -> listOf(Token(results.joinToString(""), TokenType.Comment)) }

/** This nibbler matches both names and keywords. After the initial match all
  * keywords are checked and if none match and name is returned.
  */
val nameTokenNib = takeAll(
  takeCond { c: Char -> c.isLetter() || c == '_' },
  optional(takeWhile<Char> { c: Char -> c.isLetter() || c.isDigit() || c == '_' })
)
  .map { value ->
    when(val content = value.joinToString("")) {
      "let"   -> listOf(Token("let", TokenType.LetKeyword))
      "if"    -> listOf(Token("if", TokenType.IfKeyword))
      "else"  -> listOf(Token("else", TokenType.ElseKeyword))
      "true"  -> listOf(Token("true", TokenType.Boolean))
      "false" -> listOf(Token("false", TokenType.Boolean))
      else    -> listOf(Token(content, TokenType.Name))
    }
  }

val equalSignTokenNib =
  takeString("=").map { res -> listOf(Token(res.joinToString(""), TokenType.EqualSign)) }

val colonTokenNib =
  takeString(":").map { res -> listOf(Token(res.joinToString(""), TokenType.Colon)) }

val openBraceTokenNib =
  takeString("{").map { res -> listOf(Token(res.joinToString(""), TokenType.OpenBrace)) }

val closeBraceTokenNib =
  takeString("}").map { res -> listOf(Token(res.joinToString(""), TokenType.CloseBrace)) }

val openParenTokenNib =
  takeString("(").map { res -> listOf(Token(res.joinToString(""), TokenType.OpenParen)) }

val closeParenTokenNib =
  takeString(")").map { res -> listOf(Token(res.joinToString(""), TokenType.CloseParen)) }

val arrowTokenNib =
  takeString("->").map { res -> listOf(Token(res.joinToString(""), TokenType.Arrow)) }

val integerTokenNib =
  LigNibblers.numberNibbler.map { res -> listOf(Token(res.joinToString(""), TokenType.Integer)) }

val spacesTokenNib =
  takeWhile<Char> { it == ' ' }.map { res -> listOf(Token(res.joinToString(""), TokenType.Spaces)) }

val identifierTokenNib =
  LigNibblers.identifierNibbler.map { res -> listOf(Token(res.joinToString(""), TokenType.Identifier)) }

val tokensNib: Nibbler<Char, Token> = repeat(
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
