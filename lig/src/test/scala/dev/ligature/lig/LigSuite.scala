/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import munit.FunSuite
import dev.ligature.{Identifier, LigatureLiteral, Statement}
import dev.ligature.gaze.Gaze

class LigSuite extends FunSuite {
  val testIdentifier = Identifier.fromString("test").getOrElse(???)
  def identifier(id: String) = Identifier.fromString(id).getOrElse(???)

  test("basic Statement with all Entities") {
    val statement = Statement(
      identifier("e1"),
      identifier("a1"),
      identifier("e2")
    )
    val lines = write(List(statement).iterator)
    val resStatements = read(lines)
    resStatements match {
      case Right(statements) => assertEquals(statements, List(statement))
      case Left(err)         => fail("failed", clues(err))
    }
  }

  test("list of Statements with Literal Values") {
    val statements = List(
      Statement(
        identifier("e1"),
        identifier("a1"),
        identifier("e2")
      ),
      Statement(
        identifier("e2"),
        identifier("a2"),
        LigatureLiteral.StringLiteral("string literal")
      ),
      Statement(
        identifier("e2"),
        identifier("a3"),
        LigatureLiteral.IntegerLiteral(Long.MaxValue)
      )
    )
    val lines = write(statements.iterator)
    val resStatements = read(lines)
    resStatements match {
      case Right(res) => assertEquals(res, statements)
      case Left(err)  => fail("failed", clues(err))
    }
  }

  test("parse Statement from multi-line String") {
    val statements =
      """
        |<a> <b> <c>
        |<a> <b> 123
        |<a> <b> "Test"
        |""".stripMargin
    val expectedStatements = Set(
      Statement(identifier("a"), identifier("b"), identifier("c")),
      Statement(identifier("a"), identifier("b"), LigatureLiteral.IntegerLiteral(123)),
      Statement(identifier("a"), identifier("b"), LigatureLiteral.StringLiteral("Test"))
    )
    val resStatements = read(statements)
    resStatements match {
      case Right(res) => assertEquals(res.toSet, expectedStatements)
      case Left(err)  => fail("failed", clues(err))
    }
  }

  test("parse identifiers") {
    val test = "<test>"
    val identifier = parseIdentifier(Gaze.from(test), Map(), None)
    assertEquals(identifier, Right(testIdentifier))
  }

  test("parse complex identifier") {
    val identifierS = "<http$://&&this@2]34.[42;342?#--__>"
    val identifierRes = parseIdentifier(Gaze.from(identifierS), Map(), None)
    assertEquals(
      identifierRes,
      Right(identifier("http$://&&this@2]34.[42;342?#--__"))
    )
  }

  test("parse IntegerLiteral") {
    val test = "3452345"
    val res: Either[LigError, LigatureLiteral] = parseIntegerLiteral(Gaze.from(test))
    assertEquals(res, Right(LigatureLiteral.IntegerLiteral(3452345)))
  }

  test("parse StringLiteral") {
    val test = "\"3452345\\nHello\""
    val res: Either[LigError, LigatureLiteral] = parseStringLiteral(Gaze.from(test))
    assertEquals(res, Right(LigatureLiteral.StringLiteral("3452345\\nHello")))
  }

  test("write identifiers") {
    val res = writeIdentifier(testIdentifier)
    assertEquals(res, "<test>")
  }

  test("write IntegerLiteral") {
    val test = LigatureLiteral.IntegerLiteral(3535)
    val res = writeValue(test)
    assertEquals(res, "3535")
  }

  test("write StringLiteral") {
    val test = LigatureLiteral.StringLiteral("3535 55Hello")
    val res = writeValue(test)
    assertEquals(res, "\"3535 55Hello\"")
  }
}
