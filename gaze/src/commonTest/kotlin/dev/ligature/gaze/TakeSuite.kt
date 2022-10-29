/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeSuite : FunSpec() {

  init {
    test("multiple nibblers succeed") {
      val gaze = Gaze.from("5678")
      val nibbler5 = take('5')
      val nibbler6 = take('6')
      val nibbler7 = take('7')
      val nibbler8 = take('8')
      gaze.attempt(nibbler5) shouldBe listOf('5')
      gaze.attempt(nibbler5) shouldBe null
      gaze.attempt(nibbler6) shouldBe listOf('6')
      gaze.attempt(nibbler7) shouldBe listOf('7')
      gaze.attempt(nibbler8) shouldBe listOf('8')
      gaze.attempt(nibbler8) shouldBe null
      gaze.isComplete shouldBe true
    }
  }
}
