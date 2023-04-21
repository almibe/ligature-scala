/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.none
import arrow.core.Some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeWhileSuite: FunSpec() {
  private val fiveStep = takeWhile<Char> { it == '5' }
  private val eatAllStep = takeWhile<Char> { true }
  private val spaceStep = takeWhile<Char> { c -> c == ' ' || c == '\t' }
  private val digitStep = takeWhile<Char> { it.isDigit() }

  init {
    test("empty input") {
      val gaze = Gaze.from("")
      gaze.attempt(fiveStep) shouldBe none()
      gaze.attempt(eatAllStep) shouldBe none()
      gaze.attempt(spaceStep) shouldBe none()
      gaze.attempt(digitStep) shouldBe none()
      gaze.isComplete shouldBe true
    }

    test("single 5 input") {
      val gaze = Gaze.from("5")
      gaze.attempt(fiveStep) shouldBe Some(listOf('5'))
      gaze.isComplete shouldBe true
    }

    test("single 4 input") {
      val gaze = Gaze.from("4")
      gaze.attempt(fiveStep) shouldBe none()
      gaze.isComplete shouldBe false
    }

    test("multiple 5s input") {
      val gaze = Gaze.from("55555")
      val res = gaze.attempt(fiveStep)
      res shouldBe Some(listOf('5', '5', '5', '5', '5'))
    }

    test("eat all nibbler test") {
      val gaze = Gaze.from("hello world")
      gaze.attempt(eatAllStep) shouldBe Some("hello world".toList())
      gaze.isComplete shouldBe true
    }
  }
}
