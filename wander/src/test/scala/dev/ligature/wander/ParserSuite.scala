/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import munit.FunSuite

class ParserSuite extends FunSuite {
  def ident(identifier: String): Term =
    Identifier.fromString(identifier) match
      case Left(value)  => ??? // just crash
      case Right(value) => Term.IdentifierLiteral(value)

  def check(script: String, expected: Either[LigatureError, Seq[Term]]) =
    val terms = for
      tokens <- tokenize(script)
      terms <- parse(tokens)
    yield terms
    assertEquals(terms, expected)

  test("parse Name") {
    val script = "test test2"
    val result = Right(Seq(Term.NameTerm(Name("test")), Term.NameTerm(Name("test2"))))
    check(script, result)
  }
  test("parse nothing keyword") {
    val script = "nothing"
    val result = Right(Seq(Term.NothingLiteral))
    check(script, result)
  }
  test("parse Identifier") {
    val script = "<test> <test2>"
    val result = Right(Seq(ident("test"), ident("test2")))
    check(script, result)
  }
  test("parse Integer") {
    val script = "123 0 -321"
    val result =
      Right(Seq(Term.IntegerLiteral(123), Term.IntegerLiteral(0), Term.IntegerLiteral(-321)))
    check(script, result)
  }
  test("parse String") {
    val script = "\"hello\" \"world\""
    val result = Right(Seq(Term.StringLiteral("hello"), Term.StringLiteral("world")))
    check(script, result)
  }
  test("parse Boolean") {
    val script = "true true false"
    val result =
      Right(Seq(Term.BooleanLiteral(true), Term.BooleanLiteral(true), Term.BooleanLiteral(false)))
    check(script, result)
  }
  test("parse Function Calls") {
    val script = "not(false)"
    val result = Right(Seq(Term.FunctionCall(Name("not"), Seq(Term.BooleanLiteral(false)))))
    check(script, result)
  }
  test("parse Function Call with question mark argument") {
    val script = "query(? ? ?)"
    val result = Right(
      Seq(
        Term.FunctionCall(
          Name("query"),
          Seq(Term.QuestionMark, Term.QuestionMark, Term.QuestionMark)
        )
      )
    )
    check(script, result)
  }
  test("parse empty List") {
    val script = "[]"
    val result = Right(Seq(Term.List(Seq())))
    check(script, result)
  }
  test("parse List") {
    val script = "[1 2 \"three\"]"
    val result = Right(
      Seq(
        Term.List(Seq(Term.IntegerLiteral(1), Term.IntegerLiteral(2), Term.StringLiteral("three")))
      )
    )
    check(script, result)
  }
  test("parse let binding") {
    val script = "let x = 5"
    val result = Right(Seq(Term.LetBinding(Name("x"), Term.IntegerLiteral(5))))
    check(script, result)
  }
  test("parse Scope") {
    val script = "{ 5 }"
    val result = Right(Seq(Term.Scope(Seq(Term.IntegerLiteral(5)))))
    check(script, result)
  }
  test("parse conditionals") {
    val script = "if true false else true"
    val result = Right(
      Seq(
        Term.IfExpression(
          Term.BooleanLiteral(true),
          Term.BooleanLiteral(false),
          Term.BooleanLiteral(true)
        )
      )
    )
    check(script, result)
  }
  test("parse WanderFunction") {
    val script = "let id = { x -> x } id(6)"
    val result = Right(
      Seq(
        Term.LetBinding(
          Name("id"),
          Term.WanderFunction(Seq(Name("x")), Seq(Term.NameTerm(Name("x"))))
        ),
        Term.FunctionCall(Name("id"), Seq(Term.IntegerLiteral(6)))
      )
    )
    check(script, result)
  }
}
