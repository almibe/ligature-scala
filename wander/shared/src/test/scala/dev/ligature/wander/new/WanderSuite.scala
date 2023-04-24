/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

import munit.FunSuite

class WanderSuite extends FunSuite {
  test("tokenize integers") {
    val input = "5"
    val expected = "5"
    assertEquals(run(input), expected)
  }
}
