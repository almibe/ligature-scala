/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.BendValue

class BytesSuite extends munit.FunSuite {
  test("encode and decode basic types") {
    val values = Seq(
      BendValue.Int(4), 
      BendValue.Bytes(Seq(0x12, 0x45)),
      BendValue.String("Hello")
    )
    val results = values.map(value => decodeBendValue(encodeBendValue(value)))
    assertEquals(values, results)
  }

  test("encode and decode arrays") {
    val values = Seq(
      BendValue.Array(Seq()),

    )
    val results = values.map(value => decodeBendValue(encodeBendValue(value)))
    assertEquals(values, results)
  }

  test("encode and decode structs".ignore) {
    val values = Seq(
      BendValue.Module(Map()),

    )
    val results = values.map(value => decodeBendValue(encodeBendValue(value)))
    assertEquals(values, results)
  }
}
