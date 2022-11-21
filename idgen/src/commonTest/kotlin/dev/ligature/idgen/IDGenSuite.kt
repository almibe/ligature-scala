/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.idgen

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IDGenSuite : FunSpec() {
  init {
    test("check ids") {
      val regEx = "[0-9a-zA-Z_-]{12}".toRegex()
      for (i in 0..1000) {
        val id = genId()
        regEx.matches(id).shouldBe(true)
      }
    }
  }
}
