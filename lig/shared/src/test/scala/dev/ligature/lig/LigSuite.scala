/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze

class LigSuite extends CommonSuite(parse) {
  test("parse identifiers") {
    val test = "<test>"
    val identifier = parseIdentifier(Gaze.from(test))
    assertEquals(identifier, Right(testIdentifier))
  }

  test("parse complex identifier") {
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
