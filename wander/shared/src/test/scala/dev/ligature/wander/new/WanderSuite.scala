/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

import munit.FunSuite
import munit.IgnoreSuite

class WanderSuite extends FunSuite {
  test("eval empty script") {
    val input = ""
    val expected = "nothing"
    assertEquals(runPrint(input), expected)
  }
  test("eval nothing keyword") {
    val input = "nothing"
    val expected = "nothing"
    assertEquals(runPrint(input), expected)
  }
  test("eval integer literal") {
    val input = "-5"
    val expected = "-5"
    assertEquals(runPrint(input), expected)
  }
  test("eval integers") {
    val input = "-1 0 500 -5000"
    val expected = "-5000"
    assertEquals(runPrint(input), expected)
  }
  test("eval string literal") {
    val input = "\"hello\""
    val expected = "\"hello\""
    assertEquals(runPrint(input), expected)
  }
  test("eval boolean literal") {
    val input = "false"
    val expected = "false"
    assertEquals(runPrint(input), expected)
  }
  test("eval Identifier") {
    val input = "<hello>"
    val expected = "<hello>"
    assertEquals(runPrint(input), expected)
  }
}
