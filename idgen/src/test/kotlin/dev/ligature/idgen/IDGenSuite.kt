/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.idgen

import io.kotest.core.spec.style.FunSpec

class IDGenSuite : FunSpec({
  test("check ids") {
    val regEx = Regex("[0-9a-zA-Z_-]{12}")
    val id = genId()
    println("***" + id)
    assert(regEx.matches(id))
  }
})
