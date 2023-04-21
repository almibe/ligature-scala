/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import dev.ligature.gaze.*
import dev.ligature.lig.LigNibblers

object Nibblers {
  val stringTokenNib =
    LigNibblers.stringNibbler.map { listOf(Token.StringLiteral(it.joinToString(""))) }

  //NOTE: New lines are hard coded as \n because sometimes on Windows
//the two types of new lines get mixed up in the codebase between the editor and Scalafmt.
//Not ideal, but it works consistently at least.
  val newLineTokenNib =
    takeFirst(takeString("\n"), takeString("\r\n")).map { res ->
      listOf(Token.NewLine)
    }

  val commentTokenNib = dev.ligature.gaze.takeAll(
    takeString(";"),
    takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
  ).map { results -> listOf(Token.Comment(results.joinToString(""))) }

  /** This nibbler matches both names and keywords. After the initial match all
   * keywords are checked and if none match and name is returned.
   */
  val nameTokenNib = dev.ligature.gaze.takeAll(
    takeCond { c: Char -> c.isLetter() || c == '_' },
    optional(takeWhile<Char> { c: Char -> c.isLetter() || c.isDigit() || c == '_' })
  )
    .map { value ->
      when(val content = value.joinToString("")) {
        "let"   -> listOf(Token.LetKeyword)
        "if"    -> listOf(Token.IfKeyword)
        "else"  -> listOf(Token.ElseKeyword)
        "true"  -> listOf(Token.Boolean(true))
        "false" -> listOf(Token.Boolean(false))
        else    -> listOf(Token.Name(content))
      }
    }

  val equalSignTokenNib =
    takeString("=").map { res -> listOf(Token.EqualSign) }

  val colonTokenNib =
    takeString(":").map { res -> listOf(Token.Colon) }

  val openBraceTokenNib =
    takeString("{").map { res -> listOf(Token.OpenBrace) }

  val closeBraceTokenNib =
    takeString("}").map { res -> listOf(Token.CloseBrace) }

  val openParenTokenNib =
    takeString("(").map { res -> listOf(Token.OpenParen) }

  val closeParenTokenNib =
    takeString(")").map { res -> listOf(Token.CloseParen) }

  val arrowTokenNib =
    takeString("->").map { res -> listOf(Token.Arrow) }

  val bytesTokenNib =
    LigNibblers.bytesNibbler.map { res -> listOf(Token.BytesLiteral(res.joinToString("")))}

  val integerTokenNib =
    LigNibblers.integerNibbler.map { res -> listOf(Token.Integer(res.joinToString(""))) }

  val spacesTokenNib =
    takeWhile<Char> { it == ' ' }.map { res -> listOf(Token.Spaces) }

  val identifierTokenNib =
    LigNibblers.identifierNibbler.map { res -> listOf(Token.Identifier(res.joinToString(""))) }

  val tokensNib: Nibbler<Char, Token> = repeat(
    takeFirst(
      spacesTokenNib,
      nameTokenNib,
      colonTokenNib,
      openParenTokenNib,
      closeParenTokenNib,
      arrowTokenNib,
      bytesTokenNib,
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
}
