/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.Either
import arrow.core.none
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.StringLiteral
import dev.ligature.gaze.Gaze
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LigSuite: FunSpec() {

  init {
    test("basic Statement with all Entities") {
      val statement = Statement(
        Identifier("e1"),
        Identifier("a1"),
        Identifier("e2")
      )
      val lines = write(listOf(statement).iterator())
      val resStatements = read(lines)
      when(resStatements) {
        is Either.Right -> resStatements.value shouldBe listOf(statement)
        is Either.Left  -> throw Error("Could not read.") //fail("failed", clues(err))
      }
    }

    test("list of Statements with Literal Values") {
      val statements = listOf(
        Statement(
          Identifier("e1"),
          Identifier("a1"),
          Identifier("e2")
        ),
        Statement(
          Identifier("e2"),
          Identifier("a2"),
          StringLiteral("string literal")
        ),
        Statement(
          Identifier("e2"),
          Identifier("a3"),
          IntegerLiteral(Long.MAX_VALUE)
        )
      )
      val lines = write(statements.iterator())
      val resStatements = read(lines)
      when(resStatements) {
        is Either.Right -> resStatements.value shouldBe statements
        is Either.Left  -> throw Error("Could not read.") //fail("failed", clues(err))
      }
    }

    test("parse Statement from multi-line String") {
      val statements =
        """
        |<a> <b> <c>
        |<a> <b> 123
        |<a> <b> "Test"
        |""".trimMargin()
      val expectedStatements = setOf(
        Statement(Identifier("a"), Identifier("b"), Identifier("c")),
        Statement(Identifier("a"), Identifier("b"), IntegerLiteral(123)),
        Statement(Identifier("a"), Identifier("b"), StringLiteral("Test")),
      )
      val resStatements = read(statements)
      when(resStatements) {
        is Either.Right -> resStatements.value.toSet() shouldBe expectedStatements
        is Either.Left  -> throw Error("Could not read.") //fail("failed", clues(err))
      }
    }

    test("parse Identifiers") {
      val test = "<test>"
      val identifier = parseIdentifier(Gaze.from(test), mapOf(), none())
      identifier shouldBe Either.Right(Identifier("test"))
    }

    test("parse complex Identifier") {
      val identifierS = "<http$://&&this@2]34.[42;342?#--__>"
      val identifierRes = parseIdentifier(Gaze.from(identifierS), mapOf(), none())
      identifierRes shouldBe
        Either.Right(Identifier("http$://&&this@2]34.[42;342?#--__"))
    }

    test("parse IntegerLiteral") {
      val test = "3452345"
      val res = parseIntegerLiteral(Gaze.from(test))
      res shouldBe Either.Right(IntegerLiteral(3452345))
    }

    test("parse StringLiteral") {
      val test = "\"3452345\\nHello\""
      val res = parseStringLiteral(Gaze.from(test))
      res shouldBe Either.Right(StringLiteral("3452345\\nHello"))
    }

    test("write Identifiers") {
      val res = writeIdentifier(Identifier("test"))
      res shouldBe "<test>"
    }

    test("write IntegerLiteral") {
      val test = IntegerLiteral(3535)
      val res = writeValue(test)
      res shouldBe "3535"
    }

    test("write StringLiteral") {
      val test = StringLiteral("3535 55Hello")
      val res = writeValue(test)
      res shouldBe "\"3535 55Hello\""
    }
  }
}
