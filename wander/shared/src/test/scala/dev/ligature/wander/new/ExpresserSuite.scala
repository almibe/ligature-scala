/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

import munit.FunSuite

class ExpresserSuite extends FunSuite {
  test("expressionize integers") {
    val input = List(Element.Integer(5))
    val expected = Right(List(Expression.Integer(5)))
    assertEquals(expressionize(input), expected)
  }
}
