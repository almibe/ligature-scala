/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.none
import arrow.core.Some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeAllSuite: FunSpec() {
  val nibbler5 = take('5')
  val nibbler6 = take('6')
  val nibbler7 = take('7')
  val nibbler8 = take('8')

  init {
    test("multiple nibblers succeed") {
      val gaze = Gaze.from("5678")
      val takeAllNib = takeAll(nibbler5, nibbler6, nibbler7, nibbler8)
      val res = gaze.attempt(takeAllNib)
      res shouldBe Some(listOf('5', '6', '7', '8'))
      gaze.isComplete shouldBe true
    }

    test("multiple nibblers fail and retry") {
      val gaze = Gaze.from("5678")

      val takeAllFail = takeAll(nibbler5, nibbler6, nibbler8)
      val res = gaze.attempt(takeAllFail)
      res shouldBe none()
      gaze.isComplete shouldBe false

      val takeAllSucceed = takeAll(nibbler5, nibbler6, nibbler7, nibbler8)
      val res2 = gaze.attempt(takeAllSucceed)
      res2 shouldBe Some(listOf('5', '6', '7', '8'))
      gaze.isComplete shouldBe true
    }
  }
}
