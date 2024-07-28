/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite
import dev.ligature.wander.modules.std

class InterpreterSuite extends FunSuite {
  def check(script: String, expected: WanderValue) =
    run(script, std()) match
      case Left(err)         => throw RuntimeException(err.toString())
      case Right((value, _)) => assertEquals(value, expected)

  // test("load script with no exports") {
  //   val script = "x = false, {}"
  //   check(script, WanderValue.Module(Map()))
  // }
  // test("load script with one exports") {
  //   val script = "hello = 2, { hello }"
  //   val tokens = WanderValue.Module(Map(Field("hello") -> WanderValue.Int(2)))
  //   check(script, tokens)
  // }
  // test("Statement support") {
  //   val script = "`a` `b` `c`"
  //   val result = WanderValue.Statement(
  //     Statement(
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
        Statement(
          LigatureValue.Word("a"),
          LigatureValue.Word("b"),
          LigatureValue.Word("c")
        )
      )
    )
    check(script, result)
  }
  // test("Statement with empty Record as Value") {
  //   val script = "`a` `a` {}"
  //   val result = WanderValue.Statement(
  //     Statement(
  //       LigatureValue.Word("a"),
  //       LigatureValue.Word("a"),
  //       LigatureValue.Record(Map())
  //     )
  //   )
  //   check(script, result)
  // }
}
