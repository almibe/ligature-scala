/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.Assertions.assertEquals
import munit.FunSuite

class GazeSuite extends FunSuite {
    test("empty input") {
        val gaze = Gaze.from("")
        assert(gaze.isComplete())
        assertEquals(gaze.peek(), None)
        assertEquals(gaze.peek(), None)
        assert(gaze.isComplete())
    }

    test("init Gaze with zero values") {
        val gaze = new Gaze(Vector())
        assert(gaze.isComplete())
        assertEquals(gaze.peek(), None)
        assertEquals(gaze.next(), None)
        assert(gaze.isComplete())
    }

    test("init Gaze with one value") {
        val gaze = new Gaze(Vector(5));
        assert(!gaze.isComplete())
        assertEquals(gaze.peek(), Some(5))
        assertEquals(gaze.next(), Some(5))
        assert(gaze.isComplete())
        assertEquals(gaze.peek(), None)
        assertEquals(gaze.next(), None)
        assert(gaze.isComplete())
    }

    test("init Gaze with single char string") {
        val gaze = Gaze.from("5");
        assert(!gaze.isComplete())
        assertEquals(gaze.peek(), Some('5'))
        assertEquals(gaze.next(), Some('5'))
        assert(gaze.isComplete())
        assertEquals(gaze.peek(), None)
        assertEquals(gaze.next(), None)
        assert(gaze.isComplete())
    }
}
