/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RepeatSuite : FunSpec() {
  val repeatHello: Nibbler<Char, Char> = repeat(takeString("hello"))

  init {
    test("empty repeat test") {
      val gaze = Gaze.from("")
      gaze.attempt(repeatHello) shouldBe null
    }

    test("one match repeat test") {
      val gaze = Gaze.from("hello")
      gaze.attempt(repeatHello) shouldBe "hello".toList()
    }

    test("two match repeat test") {
      val gaze = Gaze.from("hellohello")
      gaze.attempt(repeatHello) shouldBe "hellohello".toList()
      gaze.isComplete shouldBe true
    }

    test("two match repeat test with remaining text") {
      val gaze = Gaze.from("hellohellohell")
      gaze.attempt(repeatHello) shouldBe "hellohello".toList()
      gaze.isComplete shouldBe false
    }
  }
}
