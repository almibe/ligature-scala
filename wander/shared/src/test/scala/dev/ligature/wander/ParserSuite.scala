/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.{Identifier, LigatureLiteral}
import munit.FunSuite

class ParserSuite extends FunSuite {
  def ident(identifier: String): Term =
    Identifier.fromString(identifier) match
      case Left(value) => ??? //just crash
      case Right(value) => Term.IdentifierLiteral(value)

  def check(script: String, expected: Either[ScriptError, Script]) =
    val terms = for
      tokens <- tokenize(script)
      terms <- parse(tokens)
    yield terms
    assertEquals(terms, expected)

  test("parse Name") {
    val script = "test test2"
    val result = Right(Script(Seq(Term.Name("test"), Term.Name("test2"))))
    check(script, result)
  }
  test("parse Identifier") {
    val script = "<test> <test2>"
    val result = Right(Script(Seq(ident("test"), ident("test2"))))
    check(script, result)
  }
  test("parse Integer") {
    val script = "123 0 -321"
    val result = Right(Script(Seq(Term.IntegerLiteral(123), Term.IntegerLiteral(0), Term.IntegerLiteral(-321))))
    check(script, result)
  }
  test("parse String") {
    val script = "\"hello\" \"world\""
    val result = Right(Script(Seq(Term.StringLiteral("hello"), Term.StringLiteral("world"))))
    check(script, result)
  }
  test("parse Boolean") {
    val script = "true true false"
    val result = Right(Script(Seq(Term.BooleanLiteral(true), Term.BooleanLiteral(true), Term.BooleanLiteral(false))))
    check(script, result)
  }
  test("parse Function Calls") {
    val script = "not(false)"
    val result = Right(Script(Seq(Term.FunctionCall(Term.Name("not"), Seq(Term.BooleanLiteral(false))))))
    check(script, result)
  }
  // test("parse Scope") {
  //   val script = "{ 5 }"
  //   val result = Right(Script(Seq(Term.Scope(Seq(Term.IntegerLiteral(5))))))
  //   check(script, result)
  // }
  // test("parse WanderFunction") {
  //   val script = "{ x -> x }"
  //   val result = Right(Script(Seq(Term.WanderFunction(Seq(Term.IntegerLiteral(5))))))
  //   check(script, result)
  // }
}
