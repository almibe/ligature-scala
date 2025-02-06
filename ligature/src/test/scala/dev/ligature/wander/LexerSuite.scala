/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite

class LexerSuite extends FunSuite {
  def check(script: String, tokens: Either[WanderError, Seq[Token]]) =
    assertEquals(tokenize(script), tokens)
  val sp = Token.Spaces(" ")

  test("tokenize network") {
    val script = "{a b c, d e f}"
    val tokens = Right(Seq(
      Token.OpenBrace,
      Token.Element("a"), 
      sp,
      Token.Element("b"),
      sp,
      Token.Element("c"),
      Token.Comma,
      sp,
      Token.Element("d"),
      sp,
      Token.Element("e"),
      sp,
      Token.Element("f"),
      Token.CloseBrace))
    check(script, tokens)
  }
  test("tokenize Integers") {
    val script = "123 0 -123"
    val tokens = Right(
      Seq(Token.Element("123"), sp, Token.Element("0"), sp, Token.Element("-123"))
    )
    check(script, tokens)
  }
  test("tokenize Bytes") {
    val script = "0x00 0xFF"
    val tokens = Right(
      Seq(
        Token.Element("0x00"),
        sp,
        Token.Element("0xFF"),
      )
    )
    check(script, tokens)
  }
  test("tokenize call") {
    val script = "hello arg"
    val tokens = Right(Seq(Token.Element("hello"), sp, Token.Element("arg")))
    check(script, tokens)
  }
  test("tokenize Slot") {
    val script = "?hello"
    val tokens = Right(Seq(Token.Element("?hello")))
    check(script, tokens)
  }
  test("tokenize literal") {
    val script = "\"hello, world!\""
    val tokens = Right(Seq(Token.Literal("hello, world!")))
    check(script, tokens)
  }
  test("tokenize Literal with quotes") {
    val script = "\"\\\"hello, world!\\\"\""
    val tokens = Right(Seq(Token.Literal("\"hello, world!\"")))
    check(script, tokens)
  }

  test("tokenize Names") {
    val script = "hello world _test also_a_321test123"
    val tokens = Right(
      Seq(
        Token.Element("hello"),
        sp,
        Token.Element("world"),
        sp,
        Token.Element("_test"),
        sp,
        Token.Element("also_a_321test123")
      )
    )
    check(script, tokens)
  }
  // test("tokenize symbols") {
  //   val script = "when {}:()][\n\r\n->=>= --test"
  //   val tokens = Right(
  //     Seq(
  //       Token.WhenKeyword,
  //       sp,
  //       Token.OpenBrace,
  //       Token.CloseBrace,
  //       Token.Colon,
  //       Token.OpenParen,
  //       Token.CloseParen,
  //       Token.CloseBracket,
  //       Token.OpenBracket,
  //       Token.NewLine,
  //       Token.NewLine,
  //       Token.Arrow,
  //       Token.WideArrow,
  //       Token.EqualSign,
  //       sp,
  //       Token.Comment
  //     )
  //   )
  //   check(script, tokens)
  // }
  test("tokenize quote") {
    val script = "(hello 2)"
    val tokens = Right(
      Seq(Token.OpenParen, Token.Element("hello"), sp, Token.Element("2"), Token.CloseParen)
    )
    check(script, tokens)
  }
  test("tokenize multiople applications") {
    val script = "test 1, test 2"
    val tokens = Right(
      Seq(Token.Element("test"), sp, Token.Element("1"), Token.Comma, sp, Token.Element("test"), sp, Token.Element("2"))
    )
    check(script, tokens)
  }
}
