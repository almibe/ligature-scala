// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

// package dev.ligature.gaze

// import munit.FunSuite

// private val fiveStep: Nibbler[String, Seq[String]] = takeWhile[String](_ == "5")
// private val eatAllStep = takeWhile[String](_ => true)
// private val spaceStep = takeWhile[String](c => c == " " || c == "\t")
// private val digitStep = takeWhile[String](_(0).isDigit)

// class TakeWhileSuite extends FunSuite {
//   test("empty input") {
//     val gaze = Gaze.from("")
//     assertEquals(gaze.attempt(fiveStep), Result.NoMatch)
//     assertEquals(gaze.attempt(eatAllStep), Result.NoMatch)
//     assertEquals(gaze.attempt(spaceStep), Result.NoMatch)
//     assertEquals(gaze.attempt(digitStep), Result.NoMatch)
//     assert(gaze.isComplete)
//   }

//   test("single 5 input") {
//     val gaze = Gaze.from("5")
//     val res = gaze.attempt(fiveStep)
//     val expected = Result.Match(Seq("5"))
//     assertEquals(res, expected)
//     assert(gaze.isComplete)
//   }

//   test("single 4 input") {
//     val gaze = Gaze.from("4")
//     assertEquals(gaze.attempt(fiveStep), Result.NoMatch)
//     assert(!gaze.isComplete)
//   }

//   test("multiple 5s input") {
//     val gaze = Gaze.from("55555")
//     val res = gaze.attempt(fiveStep)
//     assertEquals(res, Result.Match(Seq("5", "5", "5", "5", "5")))
//   }

//   test("eat all nibbler test") {
//     val gaze = Gaze.from("hello world")
//     assertEquals(gaze.attempt(eatAllStep), Result.Match("hello world".toSeq))
//     assert(gaze.isComplete)
//   }
// }
