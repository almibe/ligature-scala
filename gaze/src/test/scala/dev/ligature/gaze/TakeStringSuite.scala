/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

import scala.collection.mutable.ArrayBuffer

private val fiveStep = takeString("5")
private val helloStep = takeString("hello")
private val spaceStep = takeString(" ")
private val worldStep = takeString("world")

class TakeStringSuite extends FunSuite {
  test("empty input") {
    val gaze = Gaze.from("")
    assertEquals(gaze.attempt(fiveStep), Result.NoMatch)
    assert(gaze.isComplete)
  }

  test("single 5 input") {
    val gaze = Gaze.from("5")
    assertEquals(gaze.attempt(fiveStep), Result.Match("5"))
    assert(gaze.isComplete)
  }

  test("single 4 input") {
    val gaze = Gaze.from("4")
    assertEquals(gaze.attempt(fiveStep), Result.NoMatch)
    assert(!gaze.isComplete)
  }

  test("multiple 5s input") {
    val gaze = Gaze.from("55555")
    val res = ArrayBuffer[String]()
    while (!gaze.isComplete) {
      val nres = gaze.attempt(fiveStep)
      nres match {
        case Result.Match(m)                    => res += m
        case Result.NoMatch | Result.EmptyMatch => throw new Error("Should not happen")
      }
    }
    assertEquals(res.toList, List("5", "5", "5", "5", "5"))
  }

  test("hello world test") {
    val gaze = Gaze.from("hello world")
    assertEquals(gaze.attempt(helloStep), Result.Match("hello"))
    assertEquals(gaze.attempt(spaceStep), Result.Match(" "))
    assertEquals(gaze.attempt(worldStep), Result.Match("world"))
    assert(gaze.isComplete)
  }

  test("map test") {
    val gaze = Gaze.from("1")
    val oneDigit = takeString("1").map(_.map(_.asDigit))
    assertEquals(gaze.attempt(oneDigit), Result.Match(Seq(1)))
  }
}
