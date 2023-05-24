/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.idgen

import munit.FunSuite

class IDGenSuite extends FunSuite {
  test("check ids") {
    val regEx = "[0-9a-zA-Z_-]{12}".r
    val id = genId()
    assert(regEx.matches(id))
  }
}
