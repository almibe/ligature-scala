/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class RepeatSuite extends FunSuite {
  val repeatHello: Nibbler[Char, Char] = repeat(takeString("hello"))

  test("empty repeat test") {
    val gaze = Gaze.from("")
    assertEquals(gaze.attempt(repeatHello), None)
  }

  test("one match repeat test") {
    val gaze = Gaze.from("hello")
    assertEquals(gaze.attempt(repeatHello), Some("hello".toSeq))
  }

  test("two match repeat test") {
    val gaze = Gaze.from("hellohello")
    assertEquals(gaze.attempt(repeatHello), Some("hellohello".toSeq))
  }

  test("two match repeat test with remaining text") {
    val gaze = Gaze.from("hellohellohell")
    assertEquals(gaze.attempt(repeatHello), Some("hellohello".toSeq))
    assert(!gaze.isComplete())
  }
}
