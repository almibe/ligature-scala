/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class TakeFirstSuite extends FunSuite {
  val takeHello = takeString("hello")
  val takeSpace = takeString(" ")
  val takeWorld = takeString("world")

  val takeFirstEmpty: Nibbler[String, String] = takeFirst()
  val takeFirstSingle = takeFirst(takeHello)
  val takeFirst3 = takeFirst(takeHello, takeSpace, takeWorld)

  test("empty take first") {
    val gaze = Gaze.from("")
    val gaze2 = Gaze.from("")
    val gaze3 = Gaze.from("")
    assertEquals(gaze.attempt(takeFirstEmpty), Result.NoMatch)
    assertEquals(gaze2.attempt(takeFirstSingle), Result.NoMatch)
    assertEquals(gaze3.attempt(takeFirst3), Result.NoMatch)
  }

  test("no match take first") {
    val gaze = Gaze.from("noting matches this")
    val gaze2 = Gaze.from("noting matches this")
    val gaze3 = Gaze.from("noting matches this")
    assertEquals(gaze.attempt(takeFirstEmpty), Result.NoMatch)
    assertEquals(gaze2.attempt(takeFirstSingle), Result.NoMatch)
    assertEquals(gaze3.attempt(takeFirst3), Result.NoMatch)
  }

  test("first match take first") {
    val gaze = Gaze.from("hello world")
    val gaze2 = Gaze.from("hello world")
    val gaze3 = Gaze.from("hello world")
    assertEquals(gaze.attempt(takeFirstEmpty), Result.NoMatch)
    assertEquals(gaze2.attempt(takeFirstSingle), Result.Match("hello"))
    assertEquals(gaze3.attempt(takeFirst3), Result.Match("hello"))
  }

  test("middle match take first") {
    val gaze = Gaze.from(" helloworld")
    val gaze2 = Gaze.from(" helloworld")
    val gaze3 = Gaze.from(" helloworld")
    assertEquals(gaze.attempt(takeFirstEmpty), Result.NoMatch)
    assertEquals(gaze2.attempt(takeFirstSingle), Result.NoMatch)
    assertEquals(gaze3.attempt(takeFirst3), Result.Match(" "))
  }

  test("last match take first") {
    val gaze = Gaze.from("world hello")
    val gaze2 = Gaze.from("world hello")
    val gaze3 = Gaze.from("world hello")
    assertEquals(gaze.attempt(takeFirstEmpty), Result.NoMatch)
    assertEquals(gaze2.attempt(takeFirstSingle), Result.NoMatch)
    assertEquals(gaze3.attempt(takeFirst3), Result.Match("world"))
  }

  test("take first with repeats") {
    val gaze = Gaze.from("hellohellohello")
    assertEquals(gaze.attempt(takeFirstEmpty), Result.NoMatch)
    assertEquals(gaze.attempt(takeFirstSingle), Result.Match("hello"))
    assertEquals(gaze.attempt(takeFirst3), Result.Match("hello"))
    assertEquals(gaze.attempt(takeFirst3), Result.Match("hello"))
    assert(gaze.isComplete)
  }
}
