/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

import munit.FunSuite

class LexerSuite extends FunSuite {
  test("tokenize integers") {
    val input = "5"
    val expected = Right(List(Token.Integer(5)))
    assertEquals(tokenize(input), expected)
  }
  test("tokenize parens") {
    val input = "()()(())))"
    val expected = Right(
      List(
        Token.OpenParen,
        Token.CloseParen,
        Token.OpenParen,
        Token.CloseParen,
        Token.OpenParen,
        Token.OpenParen,
        Token.CloseParen,
        Token.CloseParen,
        Token.CloseParen,
        Token.CloseParen
      )
    )
    assertEquals(tokenize(input), expected)
  }
}
