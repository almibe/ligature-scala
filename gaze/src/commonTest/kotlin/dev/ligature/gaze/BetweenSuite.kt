/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.Some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BetweenSuite: FunSpec() {
  val quote = takeString("'")
  val open = takeString("<")
  val close = takeString(">")
  val content = takeString("hello")

  init {
    test("quote wrap test") {
      val gaze = Gaze.from("'hello'")
      gaze.attempt(between(quote, content)) shouldBe Some("hello".toList())
    }

    test("angle bracket test") {
      val gaze = Gaze.from("<hello>")
      gaze.attempt(between(open, content, close)) shouldBe Some("hello".toList())
    }
  }
}
