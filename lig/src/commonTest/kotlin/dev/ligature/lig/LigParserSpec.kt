/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.Either
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.lig.lexer.tokenize
import dev.ligature.lig.parser.parse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LigParserSpec: FunSpec() {
  init {
    test("parse simple Statement") {
      val input = "<a> <b> <c>"
      val tokens = tokenize(input)
      parse(tokens) shouldBe Either.Right(listOf(
        Statement(Identifier("a"), Identifier("b"), Identifier("c"))
      ))
    }

    test("parse complex Statement") {
      val input = "  <a>    <b>   12345    "
      val tokens = tokenize(input)
      parse(tokens) shouldBe Either.Right(listOf(
        Statement(Identifier("a"), Identifier("b"), IntegerLiteral(12345))
      ))
    }

    test("parse multiple Statements") {
      val input = "<a> <b> <c>\n<e> <f> <g>\n"
      val tokens = tokenize(input)
      parse(tokens) shouldBe Either.Right(listOf(
        Statement(Identifier("a"), Identifier("b"), Identifier("c")),
        Statement(Identifier("e"), Identifier("f"), Identifier("g"))
      ))
    }

    test("support empty lines") {
      val input = "\n\n  \n<a> <b> <c>\n  <e> <f> <g>\n    "
      val tokens = tokenize(input)
      parse(tokens) shouldBe Either.Right(listOf(
        Statement(Identifier("a"), Identifier("b"), Identifier("c")),
        Statement(Identifier("e"), Identifier("f"), Identifier("g"))
      ))
    }
  }
}
