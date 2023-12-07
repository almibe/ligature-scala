/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import munit.FunSuite

class RepeatSepSuite extends FunSuite {
  val repeatHello: Nibbler[String, Seq[String]] = repeatSep(
    takeFirst(
      // TODO order of these matters it would be nice if it didn't
      takeString("aa"),
      takeString("a")
    ),
    ","
  )

  test("empty repeat test") {
    val gaze = Gaze.from("aa,a")
    assertEquals(gaze.attempt(repeatHello), Result.Match(Seq("aa", "a")))
  }
}
