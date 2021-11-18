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
        assertEquals(gaze.attempt(fiveStep), Left(NoMatch))
        assert(gaze.isComplete())
    }

    test("single 5 input") {
        val gaze = Gaze.from("5")
        assertEquals(gaze.attempt(fiveStep), Right("5"))
        assert(gaze.isComplete())
    }

    test("single 4 input") {
        val gaze = Gaze.from("4")
        assertEquals(gaze.attempt(fiveStep), Left(NoMatch))
        assert(!gaze.isComplete())
    }

    test ("multiple 5s input") {
        val gaze = Gaze.from("55555")
        val res = ArrayBuffer[Int]()
        while(!gaze.isComplete()) {
            val nres = gaze.attempt(fiveStep)
            nres match {
              case Right(m) => res.append(m.toInt)
              case Left(_) => throw new Error("Should not happen")
            }
        }
        assertEquals(res.toList, List(5,5,5,5,5))
    }

    test("hello world test") {
        val gaze = Gaze.from("hello world")
        assertEquals(gaze.attempt(helloStep), Right("hello"))
        assertEquals(gaze.attempt(spaceStep), Right(" "))
        assertEquals(gaze.attempt(worldStep), Right("world"))
        assert(gaze.isComplete())
    }
}
