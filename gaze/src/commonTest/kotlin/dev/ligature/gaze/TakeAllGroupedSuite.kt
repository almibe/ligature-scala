/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeAllGroupedSuite : FunSpec() {
  val nibbler5 = take('5')
  val nibbler6 = take('6')
  val nibbler7 = take('7')
  val nibbler8 = take('8')

  init {
    test("multiple nibblers succeed") {
      val gaze = Gaze.from("5678")
      val takeAllNib = takeAllGrouped(nibbler5, nibbler6, nibbler7, nibbler8)
      val res = gaze.attempt(takeAllNib)
      res shouldBe listOf(listOf('5'), listOf('6'), listOf('7'), listOf('8'))
      gaze.isComplete shouldBe true
    }

    test("multiple nibblers fail and retry") {
      val gaze = Gaze.from("5678")

      val takeAllFail = takeAllGrouped(nibbler5, nibbler6, nibbler8)
      val res = gaze.attempt(takeAllFail)
      res shouldBe null
      gaze.isComplete shouldBe false

      val takeAllSucceed = takeAllGrouped(nibbler5, nibbler6, nibbler7, nibbler8)
      val res2 = gaze.attempt(takeAllSucceed)
      res2 shouldBe listOf(listOf('5'), listOf('6'), listOf('7'), listOf('8'))
      gaze.isComplete shouldBe true
    }
  }
}
