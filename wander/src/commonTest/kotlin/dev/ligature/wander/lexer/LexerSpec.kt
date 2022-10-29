/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LexerSpec : FunSpec() {
  private fun runCases(cases: Map<String, List<Token>>) {
    for ((input, expected) in cases) {
      tokenize(input) shouldBe Either.Right(expected)
    }
  }

  init {
    test("read boolean literals") {
      runCases(
          mapOf("true" to listOf(Token.Boolean(true)), "false" to listOf(Token.Boolean(false))))
    }

    test("read white space") {
      runCases(mapOf(" " to listOf(Token.Spaces), "    " to listOf(Token.Spaces)))
    }

    test("read Identifier literals") {
      runCases(
          mapOf(
              "<a>" to listOf(Token.Identifier("a")),
              "<https://ligature.dev/#home>" to
                  listOf(Token.Identifier("https://ligature.dev/#home"))))
    }

    test("read Integer literals") {
      runCases(
          mapOf(
              "0" to listOf(Token.Integer("0")),
              "123123" to listOf(Token.Integer("123123")),
              "-2389284923" to listOf(Token.Integer("-2389284923"))))
    }

    test("read comments") {
      runCases(
          mapOf(
              "--" to listOf(Token.Comment("--")),
              "--hello" to listOf(Token.Comment("--hello")),
              "-- this is a@#$@%$#@$%@ comment;;;;  " to
                  listOf(Token.Comment("-- this is a@#\$@%\$#@\$%@ comment;;;;  "))))
    }

    test("read new lines") {
      runCases(
          mapOf(
              "\n" to listOf(Token.NewLine),
              "\r\n\n" to listOf(Token.NewLine, Token.NewLine),
              "\n\n\r\n\r\n\n" to
                  listOf(
                      Token.NewLine, Token.NewLine, Token.NewLine, Token.NewLine, Token.NewLine)))
    }

    test("read String Literals") {
      runCases(
          mapOf(
              "\"hello\"" to listOf(Token.StringLiteral("hello")),
          ))
    }

    test("read Bytes Literals") {
      runCases(
          mapOf(
              "0x55" to listOf(Token.BytesLiteral("0x55")),
          ))
    }

    test("read let keyword") {
      runCases(
          mapOf(
              "let" to listOf(Token.LetKeyword),
          ))
    }

    test("read equals sign") {
      runCases(
          mapOf(
              "=" to listOf(Token.EqualSign),
          ))
    }

    test("read names") {
      runCases(
          mapOf(
              "hello" to listOf(Token.Name("hello")),
          ))
    }

    test("read braces") {
      runCases(
          mapOf(
              "{" to listOf(Token.OpenBrace),
              "}" to listOf(Token.CloseBrace),
              "{{}}}" to
                  listOf(
                      Token.OpenBrace,
                      Token.OpenBrace,
                      Token.CloseBrace,
                      Token.CloseBrace,
                      Token.CloseBrace)))
    }

    test("read colon") {
      runCases(
          mapOf(
              ":" to listOf(Token.Colon),
              "::::" to listOf(Token.Colon, Token.Colon, Token.Colon, Token.Colon)))
    }

    test("read dot") {
      runCases(
          mapOf(
              "." to listOf(Token.Dot),
              "...." to listOf(Token.Dot, Token.Dot, Token.Dot, Token.Dot),
              "name.name" to listOf(Token.Name("name"), Token.Dot, Token.Name("name"))))
    }

    test("read parens") {
      runCases(
          mapOf(
              "(" to listOf(Token.OpenParen),
              ")" to listOf(Token.CloseParen),
              "(()))" to
                  listOf(
                      Token.OpenParen,
                      Token.OpenParen,
                      Token.CloseParen,
                      Token.CloseParen,
                      Token.CloseParen)))
    }

    test("read square brackets") {
      runCases(
          mapOf(
              "[" to listOf(Token.OpenSquare),
              "]" to listOf(Token.CloseSquare),
              "[[]]]" to
                  listOf(
                      Token.OpenSquare,
                      Token.OpenSquare,
                      Token.CloseSquare,
                      Token.CloseSquare,
                      Token.CloseSquare)))
    }

    test("read arrow") {
      runCases(
          mapOf(
              "->" to listOf(Token.Arrow),
              "->->" to listOf(Token.Arrow, Token.Arrow),
              "->->->" to listOf(Token.Arrow, Token.Arrow, Token.Arrow)))
    }

    test("read if and else keywords") {
      runCases(
          mapOf(
              "if" to listOf(Token.IfKeyword),
              "elsif" to listOf(Token.ElsifKeyword),
              "else" to listOf(Token.ElseKeyword),
          ))
    }
  }
}
