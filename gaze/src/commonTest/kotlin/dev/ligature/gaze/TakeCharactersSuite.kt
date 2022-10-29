/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeCharactersSuite : FunSpec() {
  private val abcStep = takeCharacters('a', 'b', 'c')
  private val spaceStep = takeCharacters(' ')
  private val fiveStep = takeCharacters('5')
  private val emptyStep = takeCharacters()

  init {
    test("empty input") {
      val gaze = Gaze.from("")
      gaze.attempt(abcStep) shouldBe null
      gaze.attempt(spaceStep) shouldBe null
      gaze.attempt(emptyStep) shouldBe null
      gaze.isComplete shouldBe true
    }

    test("single 5 input") {
      val gaze = Gaze.from("5")
      gaze.attempt(abcStep) shouldBe null
      gaze.attempt(spaceStep) shouldBe null
      gaze.attempt(emptyStep) shouldBe null
      gaze.isComplete shouldBe false
      gaze.attempt(fiveStep) shouldBe "5".toList()
      gaze.isComplete shouldBe true
    }

    test("single 4 input") {
      val gaze = Gaze.from("4")
      gaze.attempt(fiveStep) shouldBe null
      gaze.isComplete shouldBe false
    }

    test("multiple 5s input") {
      val gaze = Gaze.from("55555")
      val res = when (val match = gaze.attempt(fiveStep)) {
        null -> throw Error("Should not happen")
        else -> match.joinToString("").toInt()
      }
      res shouldBe 55555
    }

    test("abcd test") {
      val gaze = Gaze.from("abc d")
      gaze.attempt(abcStep) shouldBe "abc".toList()
      gaze.attempt(spaceStep) shouldBe " ".toList()
      gaze.attempt(abcStep) shouldBe null
      gaze.isComplete shouldBe false
    }
  }
}
