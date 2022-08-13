/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LexerSpec: FunSpec() {
  private fun runCases(cases: Map<String, List<Token>>) {
    for ((input, expected) in cases) {
      tokenize(input) shouldBe Either.Right(expected)
    }
  }

  init {
    test("read boolean literals") {
      runCases(mapOf(
        "true" to listOf(Token.Boolean(true)),
        "false" to listOf(Token.Boolean(false))
      ))
    }

    test("read white space") {
      runCases(mapOf(
        " " to listOf(Token.Spaces),
        "    " to listOf(Token.Spaces)
      ))
    }

    test("read Identifier literals") {
      runCases(mapOf(
        "<a>" to listOf(Token.Identifier("a")),
        "<https://ligature.dev/#home>" to listOf(Token.Identifier("https://ligature.dev/#home"))
      ))
    }

    test("read Integer literals") {
      runCases(mapOf(
        "0" to listOf(Token.Integer("0")),
        "123123" to listOf(Token.Integer("123123")),
        "-2389284923" to listOf(Token.Integer("-2389284923"))
      ))
    }

    test("read comments") {
      runCases(mapOf(
        ";" to listOf(Token.Comment(";")),
        ";hello" to listOf(Token.Comment(";hello")),
        ";this is a@#$@%$#@$%@ comment;;;;  " to listOf(Token.Comment(";this is a@#\$@%\$#@\$%@ comment;;;;  "))
      ))
    }

    test("read new lines") {
      runCases(mapOf(
        "\n" to listOf(Token.NewLine),
        "\r\n\n" to listOf(Token.NewLine, Token.NewLine),
        "\n\n\r\n\r\n\n" to listOf(Token.NewLine, Token.NewLine, Token.NewLine, Token.NewLine, Token.NewLine)
      ))
    }

//    test("read String Literals") {
//      TODO("data class StringLiteral(val value: String")
//    }
//
//    test("read Bytes Literals") {
//      TODO("data class BytesLiteral(val value: String")
//    }
//
//    test("read let keyword") {
//      TODO("object LetKeyword")
//    }
//
//    test("read equals sign") {
//      TODO("object EqualSign")
//    }
//
//    test("read names") {
//      TODO("data class Name(val value: String")
//    }
//
//    test("read braces") {
//      TODO("object OpenBrace")
//      TODO("object CloseBrace")
//    }
//
//    test("read colon") {
//      TODO("object Colon")
//    }
//
//    test("read parens") {
//      TODO("object OpenParen")
//      TODO("object CloseParen")
//    }
//
//    test("read arrow") {
//      TODO("object Arrow")
//    }
//
//    test("read if and else keywords") {
//      TODO("object IfKeyword")
//      TODO("object ElseKeyword")
//    }
  }
}
