/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.bend.Token
import dev.ligature.bend.WanderValue
import dev.ligature.bend.modules.std

class BindingSuite extends munit.FunSuite {
  def check(script: String, expected: WanderValue) =
    assertEquals(
      run(script, std()) match
        case Right(value) => value._1
        case Left(err)    => throw RuntimeException(err.toString())
      ,
      expected
    )
  test("basic binding") {
    val script = "x = 5"
    val result = WanderValue.Int(5)
    check(script, result)
  }
  test("basic binding and reference") {
    val script = "x = 5, x"
    val result = WanderValue.Int(5)
    check(script, result)
  }
  test("read Module") {
    val script = "rec = { x = { y = 5 } }, rec.x.y"
    val result = WanderValue.Int(5)
    check(script, result)
  }
}
