/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TakeFirstSuite : FunSpec() {
  val takeHello = takeString("hello")
  val takeSpace = takeString(" ")
  val takeWorld = takeString("world")

  val takeFirstEmpty: Nibbler<Char, Char> = takeFirst()
  val takeFirstSingle = takeFirst(takeHello)
  val takeFirst3 = takeFirst(takeHello, takeSpace, takeWorld)

  init {
    test("empty take first") {
      val gaze = Gaze.from("")
      val gaze2 = Gaze.from("")
      val gaze3 = Gaze.from("")
      gaze.attempt(takeFirstEmpty) shouldBe null
      gaze2.attempt(takeFirstSingle) shouldBe null
      gaze3.attempt(takeFirst3) shouldBe null
    }

    test("no match take first") {
      val gaze = Gaze.from("noting matches this")
      val gaze2 = Gaze.from("noting matches this")
      val gaze3 = Gaze.from("noting matches this")
      gaze.attempt(takeFirstEmpty) shouldBe null
      gaze2.attempt(takeFirstSingle) shouldBe null
      gaze3.attempt(takeFirst3) shouldBe null
    }

    test("first match take first") {
      val gaze = Gaze.from("hello world")
      val gaze2 = Gaze.from("hello world")
      val gaze3 = Gaze.from("hello world")
      gaze.attempt(takeFirstEmpty) shouldBe null
      gaze2.attempt(takeFirstSingle) shouldBe "hello".toList()
      gaze3.attempt(takeFirst3) shouldBe "hello".toList()
    }

    test("middle match take first") {
      val gaze = Gaze.from(" helloworld")
      val gaze2 = Gaze.from(" helloworld")
      val gaze3 = Gaze.from(" helloworld")
      gaze.attempt(takeFirstEmpty) shouldBe null
      gaze2.attempt(takeFirstSingle) shouldBe null
      gaze3.attempt(takeFirst3) shouldBe " ".toList()
    }

    test("last match take first") {
      val gaze = Gaze.from("world hello")
      val gaze2 = Gaze.from("world hello")
      val gaze3 = Gaze.from("world hello")
      gaze.attempt(takeFirstEmpty) shouldBe null
      gaze2.attempt(takeFirstSingle) shouldBe null
      gaze3.attempt(takeFirst3) shouldBe "world".toList()
    }

    test("take first with repeats") {
      val gaze = Gaze.from("hellohellohello")
      gaze.attempt(takeFirstEmpty) shouldBe null
      gaze.attempt(takeFirstSingle) shouldBe "hello".toList()
      gaze.attempt(takeFirst3) shouldBe "hello".toList()
      gaze.attempt(takeFirst3) shouldBe "hello".toList()
      gaze.isComplete shouldBe true
    }
  }
}
