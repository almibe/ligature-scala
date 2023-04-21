/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class TakeCondSuite extends FunSuite {
  test("multiple nibblers succeed") {
    val gaze = Gaze.from("5678")
    val nibbler5 = takeCond[Char](_ == '5')
    val nibbler6 = takeCond[Char](_ == '6')
    val nibblerDigit = takeCond[Char](_.isDigit)
    val nibblerLetter = takeCond[Char](_.isLetter)
    assertEquals(gaze.attempt(nibbler5), Some(List('5')))
    assertEquals(gaze.attempt(nibbler5), None)
    assertEquals(gaze.attempt(nibbler6), Some(List('6')))
    assertEquals(gaze.attempt(nibblerDigit), Some(List('7')))
    assertEquals(gaze.attempt(nibblerLetter), None)
    assertEquals(gaze.attempt(nibblerDigit), Some(List('8')))
    assertEquals(gaze.attempt(nibblerDigit), None)
    assertEquals(gaze.isComplete, true)
  }
}
