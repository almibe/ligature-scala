/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class OptionSuite extends FunSuite {
  val optionalHello = takeAll(optional(takeChar('a')), takeChar('b'))

  test("option test") {
    val gaze = Gaze.from("ab")
    assertEquals(gaze.attempt(optionalHello), Result.Match("ab".toSeq))

    val gaze2 = Gaze.from("b")
    assertEquals(gaze2.attempt(optionalHello), Result.Match("b".toSeq))
  }
}
