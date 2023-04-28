/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.command

import munit.FunSuite

class ParserSuite extends FunSuite {
  test("parse integers") {
    val input = List(Token.Integer(5))
    val expected = Right(List(Command.Literal(5)))
    assertEquals(parse(input), expected)
  }
}
