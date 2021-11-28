/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class TakeSuite extends FunSuite {
  test("multiple nibblers succeed") {
    val gaze = Gaze.from("5678")
    val nibbler5 = take('5')
    val nibbler6 = take('6')
    val nibbler7 = take('7')
    val nibbler8 = take('8')
    assertEquals(gaze.attempt(nibbler5), Some(List('5')))
    assertEquals(gaze.attempt(nibbler5), None)
    assertEquals(gaze.attempt(nibbler6), Some(List('6')))
    assertEquals(gaze.attempt(nibbler7), Some(List('7')))
    assertEquals(gaze.attempt(nibbler8), Some(List('8')))
    assertEquals(gaze.attempt(nibbler8), None)
    assertEquals(gaze.isComplete(), true)
  }
}
