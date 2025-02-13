/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.CatsEffectSuite

class InterpreterSuite extends CatsEffectSuite {
  def check(script: String, expected: List[LigatureValue]) =
    assertIO(run(script), expected)

  test("run empty script") {
    val script = ""
    check(script, List())
  }

  test("run script with empty quote") {
    val script = "[]"
    check(script, List(LigatureValue.Quote(List())))
  }

  test("run nothing-doing action") {
    val script = "[] nothing-doing"
    check(script, List(LigatureValue.Quote(List())))
  }

  test("run clear action") {
    val script = "[] clear"
    check(script, List())
  }

  // test("Network support") {
  //   val script = "{ a b c }"
  //   val result = LigatureValue.Network(InMemoryNetwork(
  //     Set(
  //       Triple(
  //         LigatureValue.Word("a"),
  //         LigatureValue.Word("b"),
  //         LigatureValue.Word("c")
  //       )
  //     )
  //   ))
  //   check(script, result)
  // }
}
