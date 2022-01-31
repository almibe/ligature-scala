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

  test("write identifiers") {
    val res = writeIdentifier(testIdentifier)
    assertEquals(res, "<test>")
  }

  test("write IntegerLiteral") {
    val test = IntegerLiteral(3535)
    val res = writeValue(test)
    assertEquals(res, "3535")
  }

  test("write StringLiteral") {
    val test = StringLiteral("3535 55Hello")
    val res = writeValue(test)
    assertEquals(res, "\"3535 55Hello\"")
  }
}
