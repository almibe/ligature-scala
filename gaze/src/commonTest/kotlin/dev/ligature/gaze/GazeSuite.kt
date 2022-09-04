/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GazeSuite: FunSpec() {
  init {
    test("empty input") {
      val gaze = Gaze.from("")
      gaze.isComplete shouldBe true
      gaze.peek() shouldBe null
      gaze.peek() shouldBe null
      gaze.isComplete shouldBe true
    }

    test("init Gaze with zero values") {
      val gaze = Gaze(listOf<Unit>())
      gaze.isComplete shouldBe true
      gaze.peek() shouldBe null
      gaze.next() shouldBe null
      gaze.isComplete shouldBe true
    }

    test("init Gaze with one value") {
      val gaze = Gaze(listOf(5))
      gaze.isComplete shouldBe false
      gaze.peek() shouldBe 5
      gaze.next() shouldBe 5
      gaze.isComplete shouldBe true
      gaze.peek() shouldBe null
      gaze.next() shouldBe null
      gaze.isComplete shouldBe true
    }

    test("init Gaze with single char string") {
      val gaze = Gaze.from("5")
      gaze.isComplete shouldBe false
      gaze.peek() shouldBe '5'
      gaze.next() shouldBe '5'
      gaze.isComplete shouldBe true
      gaze.peek() shouldBe null
      gaze.next() shouldBe null
      gaze.isComplete shouldBe true
    }
  }
}
