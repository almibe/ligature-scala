/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite
import dev.ligature.wander.modules.std

class InterpreterSuite extends FunSuite {
  def check(script: String, expected: Map[Field, WanderValue]) =
    run(script, std()) match
      case Left(err)         => throw RuntimeException(err.toString())
      case Right((value, _)) => assertEquals(value, WanderValue.Module(expected))

  test("load script with no exports") {
    val script = "x = false, {}"
    check(script, Map())
  }
  test("load script with one exports") {
    val script = "hello = 2, { hello }"
    val tokens = Map(Field("hello") -> WanderValue.Int(2))
    check(script, tokens)
  }
}
