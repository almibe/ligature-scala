/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

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
      Seq(Token.IntegerValue(123), sp, Token.IntegerValue(0), sp, Token.IntegerValue(-123))
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
  test("tokenize Identifier") {
    val script = "`hello`"
    val tokens = Right(Seq(Token.Identifier("hello")))
    check(script, tokens)
  }
  test("tokenize Slot") {
    val script = "?hello"
    val tokens = Right(Seq(Token.Slot("hello")))
    check(script, tokens)
  }
  test("tokenize Strings") {
    val script = "\"hello, world!\""
    val tokens = Right(Seq(Token.StringValue("hello, world!")))
    check(script, tokens)
  }
  test("tokenize Strings with quotes") {
    val script = "\"\\\"hello, world!\\\"\""
    val tokens = Right(Seq(Token.StringValue("\"hello, world!\"")))
    check(script, tokens)
  }

  test("tokenize interpolated Strings") {
    val script = "i\"hello, world!\""
    val tokens = Right(Seq(Token.StringValue("hello, world!", true)))
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
    val script = "when {}:()][\n\r\n->=>= --test"
    val tokens = Right(
      Seq(
        Token.WhenKeyword,
        sp,
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
      Seq(Token.OpenParen, Token.Field("hello"), sp, Token.IntegerValue(2), Token.CloseParen)
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
