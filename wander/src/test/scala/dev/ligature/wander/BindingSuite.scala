/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.Token
import dev.ligature.wander.WanderValue
import dev.ligature.wander.libraries.std

class LetSuite extends munit.FunSuite {
  def check(script: String, expected: Either[WanderError, WanderValue]) =
    assertEquals(
      run(script, std()).getOrElse(???)._1,
      expected.getOrElse(???)
    )

  test("basic binding") {
    val script = "x = 5"
    val result = Right(WanderValue.Int(5))
    check(script, result)
  }
  test("basic binding and reference") {
    val script = "x = 5, x"
    val result = Right(WanderValue.Int(5))
    check(script, result)
  }
}
