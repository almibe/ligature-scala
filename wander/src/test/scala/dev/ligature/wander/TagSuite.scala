/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.preludes.common
import munit.FunSuite

class TagSuite extends FunSuite {
  def check(script: String, expected: WanderValue) =
    assertEquals(
      run(script, common()).getOrElse(???)._1,
      expected
    )

  def checkFail(script: String) =
    val res = run(script, common())
    assert(res.isLeft)
    assert(res.left.getOrElse(???).userMessage.contains("Tag Function"))

  test("run passing tag assignment") {
    val script = "x: Core.Int = 5"
    val result = WanderValue.Int(5)
    check(script, result)
  }

  test("run failing tag assignment") {
    val script = "x: Core.Bool = 5"
    checkFail(script)
  }

  test("run failing tag with Host Function") {
    val script = "Bool.not 5"
    checkFail(script)
  }

  test("run passing tag used with lambda".ignore) {
    val script = "increment: Core.Int -> Core.Int = \\i -> Int.add i 1, increment 4"
    val result = WanderValue.Int(5)
    check(script, result)
  }

  test("run failing tag used with lambda".ignore) {
    val script = "increment: Core.Int -> Core.Int = \\i -> Int.add i 1, increment false"
    checkFail(script)
  }

  test("run failing tag used with lambda in return".ignore) {
    val script = "increment: Core.Int -> Core.Int = \\i -> true, increment 1"
    checkFail(script)
  }
}
