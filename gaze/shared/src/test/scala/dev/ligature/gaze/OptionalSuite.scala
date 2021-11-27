/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class OptionSuite extends FunSuite {
  val ello = takeString("ello")
  val optionalHello = takeAll(optional(takeString("h")), takeString("ello"))

  test("empty repeat test") {
    val gaze = Gaze.from("hello")
    assertEquals(gaze.attempt(repeatHello), None)
  }
}
