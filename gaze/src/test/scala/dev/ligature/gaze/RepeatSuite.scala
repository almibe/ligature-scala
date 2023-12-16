/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class RepeatSuite extends FunSuite {
  val repeatHello: Nibbler[String, String] = concat(repeat(takeString("a")))

  test("empty repeat test") {
    val gaze = Gaze.from("")
    assertEquals(gaze.attempt(repeatHello), Result.NoMatch)
  }

  test("one match repeat test") {
    val gaze = Gaze.from("a")
    assertEquals(gaze.attempt(repeatHello), Result.Match("a"))
  }

  test("two match repeat test") {
    val gaze = Gaze.from("aa")
    assertEquals(gaze.attempt(repeatHello), Result.Match("aa"))
  }

  test("two match repeat test with remaining text") {
    val gaze = Gaze.from("aab")
    assertEquals(gaze.attempt(repeatHello), Result.Match("aa"))
    assert(!gaze.isComplete)
  }
}
