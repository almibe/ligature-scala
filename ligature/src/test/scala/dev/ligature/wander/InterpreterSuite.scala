/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite

class InterpreterSuite extends FunSuite {
  def check(script: String, expected: WanderValue) =
    run(script) match
      case Left(err)         => throw RuntimeException(err.toString())
      case Right((value)) => assertEquals(value, expected)

  // test("load script with no exports") {
  //   val script = "x = false, {}"
  //   check(script, WanderValue.Module(Map()))
  // }
  // test("load script with one exports") {
  //   val script = "hello = 2, { hello }"
  //   val tokens = WanderValue.Module(Map(Field("hello") -> WanderValue.Int(2)))
  //   check(script, tokens)
  // }
  // test("Triple support") {
  //   val script = "`a` `b` `c`"
  //   val result = WanderValue.Triple(
  //     Triple(
  //       LigatureValue.Word("a"),
  //       LigatureValue.Word("b"),
  //       LigatureValue.Word("c")
  //     )
  //   )
  //   check(script, result)
  // }
  test("Network support") {
    val script = "{ a b c }"
    val result = WanderValue.Network(
      Set(
        Triple(
          LigatureValue.Word("a"),
          LigatureValue.Word("b"),
          LigatureValue.Word("c")
        )
      )
    )
    check(script, result)
  }
  // test("Triple with empty Record as Value") {
  //   val script = "`a` `a` {}"
  //   val result = WanderValue.Triple(
  //     Triple(
  //       LigatureValue.Word("a"),
  //       LigatureValue.Word("a"),
  //       LigatureValue.Record(Map())
  //     )
  //   )
  //   check(script, result)
  // }
}
