/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.none
import arrow.core.None
import arrow.core.Some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeCharactersSuite: FunSpec() {
  private val abcStep = takeCharacters('a', 'b', 'c')
  private val spaceStep = takeCharacters(' ')
  private val fiveStep = takeCharacters('5')
  private val emptyStep = takeCharacters()

  init {
    test("empty input") {
      val gaze = Gaze.from("")
      gaze.attempt(abcStep) shouldBe none()
      gaze.attempt(spaceStep) shouldBe none()
      gaze.attempt(emptyStep) shouldBe none()
      gaze.isComplete shouldBe true
    }

    test("single 5 input") {
      val gaze = Gaze.from("5")
      gaze.attempt(abcStep) shouldBe none()
      gaze.attempt(spaceStep) shouldBe none()
      gaze.attempt(emptyStep) shouldBe none()
      gaze.isComplete shouldBe false
      gaze.attempt(fiveStep) shouldBe Some("5".toList())
      gaze.isComplete shouldBe true
    }

    test("single 4 input") {
      val gaze = Gaze.from("4")
      gaze.attempt(fiveStep) shouldBe none()
      gaze.isComplete shouldBe false
    }

    test("multiple 5s input") {
      val gaze = Gaze.from("55555")
      val res = when(val match = gaze.attempt(fiveStep)) {
        is Some -> match.value.joinToString("").toInt()
        is None -> throw Error("Should not happen")
      }
      res shouldBe 55555
    }

    test("abcd test") {
      val gaze = Gaze.from("abc d")
      gaze.attempt(abcStep) shouldBe Some("abc".toList())
      gaze.attempt(spaceStep) shouldBe Some(" ".toList())
      gaze.attempt(abcStep) shouldBe none()
      gaze.isComplete shouldBe false
    }
  }
}
