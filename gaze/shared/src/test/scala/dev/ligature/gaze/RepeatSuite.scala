/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class RepeatSuite extends FunSuite {
    val repeatHello = repeat(takeString("hello"))

    test("empty repeat test") {
        val gaze = Gaze.from("")
        assertEquals(gaze.attempt(repeatHello), Left(NoMatch))
    }

    test("one match repeat test") {
        val gaze = Gaze.from("hello")
        assertEquals(gaze.attempt(repeatHello), Right(List("hello")))
    }

    test("two match repeat test") {
        val gaze = Gaze.from("hellohello")
        assertEquals(gaze.attempt(repeatHello), Right(List("hello", "hello")))
    }

    test("two match repeat test with remaining text") {
        val gaze = Gaze.from("hellohellohell")
        assertEquals(gaze.attempt(repeatHello), Right(List("hello", "hello")))
        assert(!gaze.isComplete())
    }
}
