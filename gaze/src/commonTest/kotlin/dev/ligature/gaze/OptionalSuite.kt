/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OptionSuite: FunSpec() {
  val optionalHello = takeAll(optional(takeString("h")), takeString("ello"))

  init {
    test("option test") {
      val gaze = Gaze.from("hello")
      gaze.attempt(optionalHello) shouldBe "hello".toList()

      val gaze2 = Gaze.from("ello")
      gaze2.attempt(optionalHello) shouldBe "ello".toList()
    }
  }
}
