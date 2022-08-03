/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.Some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeUntilSuite: FunSpec() {

  init {
    test("take until basic cases") {
      val nibbler = takeUntil('\n')
      val takeNewLine = takeString("\n")
      val gaze1 = Gaze.from("")
      val gaze2 = Gaze.from("\n")
      val gaze3 = Gaze.from("    \n   ")
      val gaze4 = Gaze.from("123\n")

      gaze1.attempt(nibbler) shouldBe Some(listOf())
      gaze1.isComplete shouldBe true

      gaze2.attempt(nibbler) shouldBe Some(listOf())
      gaze2.isComplete shouldBe false
      gaze2.attempt(takeNewLine) shouldBe Some(listOf('\n'))
      gaze2.isComplete shouldBe true

      gaze3.attempt(nibbler) shouldBe Some(listOf(' ', ' ', ' ', ' '))

      gaze4.attempt(nibbler) shouldBe Some(listOf('1', '2', '3'))
      gaze4.isComplete shouldBe false
      gaze4.attempt(takeNewLine) shouldBe Some(listOf('\n'))
      gaze4.isComplete shouldBe true
    }
  }
}
