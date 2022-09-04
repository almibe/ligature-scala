/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeStringSuite: FunSpec() {
  private val fiveStep = takeString("5")
  private val helloStep = takeString("hello")
  private val spaceStep = takeString(" ")
  private val worldStep = takeString("world")

  init {
    test("empty input") {
      val gaze = Gaze.from("")
      gaze.attempt(fiveStep) shouldBe null
      gaze.isComplete shouldBe true
    }

    test("single 5 input") {
      val gaze = Gaze.from("5")
      gaze.attempt(fiveStep) shouldBe listOf('5')
      gaze.isComplete shouldBe true
    }

    test("single 4 input") {
      val gaze = Gaze.from("4")
      gaze.attempt(fiveStep) shouldBe null
      gaze.isComplete shouldBe false
    }

    test("multiple 5s input") {
      val gaze = Gaze.from("55555")
      val res = mutableListOf<Char>()
      while (!gaze.isComplete) {
        when(val nres = gaze.attempt(fiveStep)) {
          null -> throw Error("Should not happen")
          else -> res.addAll(nres)
        }
      }
      res shouldBe listOf('5', '5', '5', '5', '5')
    }

    test("hello world test") {
      val gaze = Gaze.from("hello world")
      gaze.attempt(helloStep) shouldBe "hello".toList()
      gaze.attempt(spaceStep) shouldBe " ".toList()
      gaze.attempt(worldStep) shouldBe "world".toList()
      gaze.isComplete shouldBe true
    }

    test("map test") {
      val gaze = Gaze.from("1")
      val oneDigit = takeString("1").map { it.map { it.digitToInt() }}
      gaze.attempt(oneDigit) shouldBe listOf(1)
    }
  }
}
