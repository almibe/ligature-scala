/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.lig.lexer.LigToken
import dev.ligature.lig.lexer.tokenize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LigLexerSpec: FunSpec() {
  init {
    test("read Identifier") {
      val testCases = mapOf<String, List<LigToken>>(
        "<identifier>" to listOf(LigToken.Identifier("identifier")),
      )
      for((input, expected) in testCases) {
        tokenize(input) shouldBe expected
      }
    }

    test("read Integer Literal") {
      val testCases = mapOf<String, List<LigToken>>(
        "1" to listOf(LigToken.IntegerLiteral("1")),
      )
      for((input, expected) in testCases) {
        tokenize(input) shouldBe expected
      }
    }

    test("read String Literal") {
      val testCases = mapOf<String, List<LigToken>>(
        "\"hello\"" to listOf(LigToken.StringLiteral("hello")),
        "\"LGTBQ+\"" to listOf(LigToken.StringLiteral("LGTBQ+"))
      )
      for((input, expected) in testCases) {
        tokenize(input) shouldBe expected
      }
    }

    test("read Bytes Literal") {
      val testCases = mapOf<String, List<LigToken>>(
        "0x1122EF" to listOf(LigToken.BytesLiteral("0x1122EF")),
      )
      for((input, expected) in testCases) {
        tokenize(input) shouldBe expected
      }
    }

    test("read example data with white space") {
      val testCases = mapOf<String, List<LigToken>>(
        "0x1122EF  \"hello, world!\" " to listOf(
          LigToken.BytesLiteral("0x1122EF"),
          LigToken.WhiteSpace,
          LigToken.StringLiteral("hello, world!"),
          LigToken.WhiteSpace
          ),
      )
      for((input, expected) in testCases) {
        tokenize(input) shouldBe expected
      }
    }

    test("read example data with new lines") {
      val testCases = mapOf<String, List<LigToken>>(
        "0x1122EF\n\n\"hello, world!\"" to listOf(
          LigToken.BytesLiteral("0x1122EF"),
          LigToken.NewLine,
          LigToken.NewLine,
          LigToken.StringLiteral("hello, world!")
        ),
      )
      for((input, expected) in testCases) {
        tokenize(input) shouldBe expected
      }
    }
  }
}
