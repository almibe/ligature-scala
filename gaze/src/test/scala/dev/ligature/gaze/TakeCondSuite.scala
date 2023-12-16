/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class TakeCondSuite extends FunSuite {
  test("multiple nibblers succeed") {
    val gaze = Gaze.from("5678")
    val nibbler5 = takeCond[String](_ == "5")
    val nibbler6 = takeCond[String](_ == "6")
    val nibblerDigit = takeCond[String](_(0).isDigit)
    val nibblerLetter = takeCond[String](_(0).isLetter)
    assertEquals(gaze.attempt(nibbler5), Result.Match("5"))
    assertEquals(gaze.attempt(nibbler5), Result.NoMatch)
    assertEquals(gaze.attempt(nibbler6), Result.Match("6"))
    assertEquals(gaze.attempt(nibblerDigit), Result.Match("7"))
    assertEquals(gaze.attempt(nibblerLetter), Result.NoMatch)
    assertEquals(gaze.attempt(nibblerDigit), Result.Match("8"))
    assertEquals(gaze.attempt(nibblerDigit), Result.NoMatch)
    assertEquals(gaze.isComplete, true)
  }
}
