/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite

class InterpreterSuite extends FunSuite {
  def check(script: String, expected: List[LigatureValue]) =
    run(script) match
      case Left(err)         => throw RuntimeException(err.toString())
      case Right((value))    => assertEquals(value, expected)

  test("run empty script") {
    val script = ""
    check(script, List())
  }

  test("run script with single literal") {
    val script = "\"test\""
    check(script, List(LigatureValue.Literal("test")))
  }

  // test("run script with single literal") {
  //   val script = "\"test\""
  //   check(script, List(LigatureValue.Literal("test")))
  // }


  // test("load script with one exports") {
  //   val script = "hello = 2, { hello }"
  //   val tokens = LigatureValue.Module(Map(Field("hello") -> LigatureValue.Int(2)))
  //   check(script, tokens)
  // }
  // test("Triple support") {
  //   val script = "`a` `b` `c`"
  //   val result = LigatureValue.Triple(
  //     Triple(
  //       LigatureValue.Word("a"),
  //       LigatureValue.Word("b"),
  //       LigatureValue.Word("c")
  //     )
  //   )
  //   check(script, result)
  // }
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
  // test("Triple with empty Record as Value") {
  //   val script = "`a` `a` {}"
  //   val result = LigatureValue.Triple(
  //     Triple(
  //       LigatureValue.Word("a"),
  //       LigatureValue.Word("a"),
  //       LigatureValue.Record(Map())
  //     )
  //   )
  //   check(script, result)
  // }
}
