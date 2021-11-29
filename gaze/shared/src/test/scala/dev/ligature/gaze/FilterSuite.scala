/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class FilterSuite extends FunSuite {
  test("filter numbers test") {
    val gaze = Gaze.from("12341234hello")
    val filterNibbler = filter((c: Char) => !c.isDigit, takeString("hello"))
    assertEquals(gaze.attempt(filterNibbler), Some("hello".toSeq))
  }

  test("filter all numbers test") {
    val gaze = Gaze.from("12341234hello12342343hello234234234")
    val filterNibbler = filter((c: Char) => !c.isDigit, takeString("hello"))
    assertEquals(gaze.attempt(filterNibbler), Some("hellohello".toSeq))
  }
}
