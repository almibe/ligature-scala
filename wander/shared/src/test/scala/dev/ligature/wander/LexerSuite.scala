/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import munit.FunSuite

class LexerSuite extends FunSuite {
  def check(script: String, tokens: Either[LigatureError, Seq[Token]]) =
    assertEquals(tokenize(script), tokens)
  def ident(identifier: String): Token =
    Identifier.fromString(identifier) match
      case Left(value) => ??? //just crash
      case Right(value) => Token.Identifier(value)
  val sp = Token.Spaces(" ")

  test("tokenize booleans") {
    val script = "true false"
    val tokens = Right(Seq(Token.BooleanLiteral(true), sp, Token.BooleanLiteral(false)))
    check(script, tokens)
  }
  test("tokenize Identifiers") {
    val script = "<hello><123><_4><https://ligature.dev>"
    val tokens = Right(Seq(ident("hello"), ident("123"), ident("_4"), ident("https://ligature.dev")))
    check(script, tokens)
  }
  test("tokenize Integers") {
    val script = "123 0 -123"
    val tokens = Right(Seq(Token.IntegerLiteral(123), sp, Token.IntegerLiteral(0), sp, Token.IntegerLiteral(-123)))
    check(script, tokens)
  }
  test("tokenize Strings") {
    val script = "\"hello, world!\""
    val tokens = Right(Seq(Token.StringLiteral("hello, world!")))
    check(script, tokens)
  }
  test("tokenize Names") {
    val script = "hello world _test also_a_321test123"
    val tokens = Right(Seq(Token.Name("hello"), sp, Token.Name("world"), sp, Token.Name("_test"), sp, Token.Name("also_a_321test123")))
    check(script, tokens)
  }
  test("tokenize symbols") {
    val script = "{}:()\n\r\n->if elsif else = let --test"
    val tokens = Right(Seq(Token.OpenBrace, Token.CloseBrace, Token.Colon, Token.OpenParen, Token.CloseParen, Token.NewLine, Token.NewLine,
    Token.Arrow, Token.IfKeyword, sp, Token.ElsifKeyword, sp, Token.ElseKeyword, sp, Token.EqualSign, sp, Token.LetKeyword, sp, Token.Comment))
  }
}
