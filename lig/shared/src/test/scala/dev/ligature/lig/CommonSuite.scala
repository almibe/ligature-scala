/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze
import dev.ligature.dlig.DLigError

/**
 * This test suite contains code that is used by both Lig and DLig.
 */
abstract class CommonSuite[E](val parse: (input: String) => Either[E, List[Statement]]) extends FunSuite { 
  val testIdentifier = Identifier.fromString("test").getOrElse { ??? }
  def identifier(id: String) = Identifier.fromString(id).getOrElse { ??? }

  test("basic Statement with all Entities") {
    val statement = Statement(
      identifier("e1"),
      identifier("a1"),
      identifier("e2")
    )
    val lines = write(List(statement).iterator)
    val resStatements = parse(lines)
    resStatements match {
      case Right(statements) => assertEquals(statements, List(statement))
      case Left(err) => fail("failed", clues(err))
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
    resStatements match {
      case Right(res) => assertEquals(res, statements)
      case Left(err) => fail("failed", clues(err))
    }
  }
}
