/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.*
import dev.ligature.wander.libraries
import munit.FunSuite

class StringSuite extends FunSuite {
  def check(script: String, expected: WanderValue) =
    assertEquals(
      run(script, common()).getOrElse(???)._1,
      expected
    )

  test("test cat") {
    val script = "String.cat \"hello, \" \"world\""
    val expected = WanderValue.String("hello, world")
    check(script, expected)
  }  
}