/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class TakeAllGroupedSuite extends FunSuite {
  val nibbler5 = take("5")
  val nibbler6 = take("6")
  val nibbler7 = take("7")
  val nibbler8 = take("8")

  test("multiple nibblers succeed") {
    val gaze = Gaze.from("5678")
    val takeAllNib = takeAll(nibbler5, nibbler6, nibbler7, nibbler8)
    val res = gaze.attempt(takeAllNib)
    assertEquals(res, Result.Match(Seq("5", "6", "7", "8")))
    assert(gaze.isComplete)
  }

  test("multiple nibblers fail and retry") {
    val gaze = Gaze.from("5678")
    val takeAllFail = takeAll(nibbler5, nibbler6, nibbler8)
    val res = gaze.attempt(takeAllFail)
    assertEquals(res, Result.NoMatch)
    assert(!gaze.isComplete)

    val takeAllSucceed = takeAll(nibbler5, nibbler6, nibbler7, nibbler8)
    val res2 = gaze.attempt(takeAllSucceed)
    assertEquals(res2, Result.Match(Seq("5", "6", "7", "8")))
    assert(gaze.isComplete)
  }
}
