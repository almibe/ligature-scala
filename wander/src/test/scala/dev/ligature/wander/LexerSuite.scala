/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class LexerSuite extends FunSuite {
  def check(script: String, tokens: Either[WanderError, Seq[Token]]) =
    assertEquals(tokenize(script), tokens)
  val sp = Token.Spaces(" ")

  test("tokenize booleans") {
    val script = "true false"
    val tokens = Right(Seq(Token.BooleanLiteral(true), sp, Token.BooleanLiteral(false)))
    check(script, tokens)
  }
  test("tokenize Integers") {
    val script = "123 0 -123"
    val tokens = Right(
      Seq(Token.IntegerLiteral(123), sp, Token.IntegerLiteral(0), sp, Token.IntegerLiteral(-123))
    )
    check(script, tokens)
  }
  test("tokenize Bytes") {
    val script = "0x00 0xFF 0xfe 0x10"
    val tokens = Right(
      Seq(
        Token.Bytes(Seq(0.byteValue)),
        sp,
        Token.Bytes(Seq(-1.byteValue)),
        sp,
        Token.Bytes(Seq(-2.byteValue)),
        sp,
        Token.Bytes(Seq(16.byteValue))
      )
    )
    check(script, tokens)
  }
  test("tokenize Strings") {
    val script = "\"hello, world!\""
    val tokens = Right(Seq(Token.StringLiteral("hello, world!")))
    check(script, tokens)
  }
  test("tokenize Strings with quotes") {
    val script = "\"\\\"hello, world!\\\"\""
    val tokens = Right(Seq(Token.StringLiteral("\"hello, world!\"")))
    check(script, tokens)
  }

  test("tokenize interpolated Strings") {
    val script = "i\"hello, world!\""
    val tokens = Right(Seq(Token.StringLiteral("hello, world!", true)))
    check(script, tokens)
  }
  test("tokenize Names") {
    val script = "hello world _test also_a_321test123"
    val tokens = Right(
      Seq(
        Token.Field("hello"),
        sp,
        Token.Field("world"),
        sp,
        Token.Field("_test"),
        sp,
        Token.Field("also_a_321test123")
      )
    )
    check(script, tokens)
  }
  test("tokenize symbols") {
    val script = "when ?{}:()][\n\r\n->=>= --test"
    val tokens = Right(
      Seq(
        Token.WhenKeyword,
        sp,
        Token.QuestionMark,
        Token.OpenBrace,
        Token.CloseBrace,
        Token.Colon,
        Token.OpenParen,
        Token.CloseParen,
        Token.CloseBracket,
        Token.OpenBracket,
        Token.NewLine,
        Token.NewLine,
        Token.Arrow,
        Token.WideArrow,
        Token.EqualSign,
        sp,
        Token.Comment
      )
    )
    check(script, tokens)
  }
  test("tokenize grouping") {
    val script = "(hello 2)"
    val tokens = Right(
      Seq(Token.OpenParen, Token.Field("hello"), sp, Token.IntegerLiteral(2), Token.CloseParen)
    )
    check(script, tokens)
  }
  test("tokenize pipe") {
    val script = "true | not"
    val tokens = Right(
      Seq(Token.BooleanLiteral(true), sp, Token.Pipe, sp, Token.Field("not"))
    )
    check(script, tokens)
  }
}
