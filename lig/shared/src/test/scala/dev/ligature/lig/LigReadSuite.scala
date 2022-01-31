/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze

class LigSuite extends FunSuite {
  val testIdentifier = Identifier.fromString("test").getOrElse { ??? }
  def identifier(id: String) = Identifier.fromString(id).getOrElse { ??? }

  test("parse identifiers") {
    val test = "<test>"
    val identifier = parseIdentifier(Gaze.from(test))
    assertEquals(identifier, Right(testIdentifier))
  }

  test("complex entity identifier") {
    val identifierS = "<http$://&&this@2]34.[42;342?#--__>"
    val identifierRes = parseIdentifier(Gaze.from(identifierS))
    assertEquals(
      identifierRes,
      Right(identifier("http$://&&this@2]34.[42;342?#--__"))
    )
  }

  test("parse IntegerLiteral") {
    val test = "3452345"
    val res = parseIntegerLiteral(Gaze.from(test))
    assertEquals(res, Right(IntegerLiteral(3452345)))
  }

  test("parse StringLiteral") {
    val test = "\"3452345\\nHello\""
    val res = parseStringLiteral(Gaze.from(test))
    assertEquals(res, Right(StringLiteral("3452345\\nHello")))
  }

  test("basic Statement with all Entities") {
    val statement = Statement(
      identifier("e1"),
      identifier("a1"),
      identifier("e2")
    )
    val lines = write(List(statement).iterator)
    val resStatements = parse(lines)
    assertEquals(List(statement), resStatements.getOrElse(???).toList)
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
        StringLiteral("string literal")
      ),
      Statement(
        identifier("e2"),
        identifier("a3"),
        IntegerLiteral(Long.MaxValue)
      )
    )
    val lines = write(statements.iterator)
    val resStatements = parse(lines)
    assertEquals(statements, resStatements.getOrElse(???).toList)
  }
}
