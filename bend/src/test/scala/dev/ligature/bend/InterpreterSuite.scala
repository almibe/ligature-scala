/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import munit.FunSuite
import dev.ligature.bend.modules.std
import dev.ligature.Statement
import dev.ligature.LigatureValue

class InterpreterSuite extends FunSuite {
  def check(script: String, expected: BendValue) =
    run(script, std()) match
      case Left(err)         => throw RuntimeException(err.toString())
      case Right((value, _)) => assertEquals(value, expected)

  test("load script with no exports") {
    val script = "x = false, {}"
    check(script, BendValue.Module(Map()))
  }
  test("load script with one exports") {
    val script = "hello = 2, { hello }"
    val tokens = BendValue.Module(Map(Field("hello") -> BendValue.Int(2)))
    check(script, tokens)
  }
  test("Statement support") {
    val script = "`a` `b` `c`"
    val result = BendValue.Statement(
      Statement(
        LigatureValue.Identifier("a"),
        LigatureValue.Identifier("b"),
        LigatureValue.Identifier("c")
      )
    )
    check(script, result)
  }
  test("Graph support") {
    val script = "{ `a` `b` `c` }"
    val result = BendValue.Graph(
      Set(
        Statement(
          LigatureValue.Identifier("a"),
          LigatureValue.Identifier("b"),
          LigatureValue.Identifier("c")
        )
      )
    )
    check(script, result)
  }
}
