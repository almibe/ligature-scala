/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeCondSuite : FunSpec() {

  init {
    test("multiple nibblers succeed") {
      val gaze = Gaze.from("5678")
      val nibbler5 = takeCond<Char> { it == '5' }
      val nibbler6 = takeCond<Char> { it == '6' }
      val nibblerDigit = takeCond<Char> { it.isDigit() }
      val nibblerLetter = takeCond<Char> { it.isLetter() }
      gaze.attempt(nibbler5) shouldBe listOf('5')
      gaze.attempt(nibbler5) shouldBe null
      gaze.attempt(nibbler6) shouldBe listOf('6')
      gaze.attempt(nibblerDigit) shouldBe listOf('7')
      gaze.attempt(nibblerLetter) shouldBe null
      gaze.attempt(nibblerDigit) shouldBe listOf('8')
      gaze.attempt(nibblerDigit) shouldBe null
      gaze.isComplete shouldBe true
    }
  }
}
