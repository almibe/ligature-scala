/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class ParserSuite extends FunSuite {
  def ident(identifier: String): Term =
    Identifier.fromString(identifier) match
      case Left(value) => ??? //just crash
      case Right(value) => Term.IdentifierLiteral(value)

  def check(script: String): Either[WanderError, Seq[Term]] =
    val tokens = tokenize(script) match
      case Left(err) => return Left(err)
      case Right(tokens) => tokens
    parse(tokens)

  test("parse Name") {
    val input = check("test test2")
    val expected = Right(Seq(Term.NameTerm(Name("test")), Term.NameTerm(Name("test2"))))
    assertEquals(input, expected)
  }
  // test("parse nothing keyword") {
  //   val script = "nothing"
  //   val result = Right(Seq(Term.NothingLiteral))
  //   check(script, result)
  // }
  // test("parse Identifier") {
  //   val script = "<test> <test2>"
  //   val result = Right(Seq(ident("test"), ident("test2")))
  //   check(script, result)
  // }
  // test("parse Integer") {
  //   val script = "123 0 -321"
  //   val result = Right(Seq(Term.IntegerLiteral(123), Term.IntegerLiteral(0), Term.IntegerLiteral(-321)))
  //   check(script, result)
  // }
  // test("parse String") {
  //   val script = "\"hello\" \"world\""
  //   val result = Right(Seq(Term.StringLiteral("hello"), Term.StringLiteral("world")))
  //   check(script, result)
  // }
  // test("parse Boolean") {
  //   val script = "true true false"
  //   val result = Right(Seq(Term.BooleanLiteral(true), Term.BooleanLiteral(true), Term.BooleanLiteral(false)))
  //   check(script, result)
  // }
  // test("parse Function Calls") {
  //   val script = "not false"
  //   val result = Right(Seq(Term.Application(Seq(Term.NameTerm(Name("not")), Term.BooleanLiteral(false)))))
  //   check(script, result)
  // }
  // test("parse Function Call with question mark argument") {
  //   val script = "query ? ? ?"
  //   val result = Right(Seq(Term.Application(Seq(Term.NameTerm(Name("query")), Term.QuestionMark, Term.QuestionMark, Term.QuestionMark))))
  //   check(script, result)
  // }
  // test("parse empty List") {
  //   val script = "[]"
  //   val result = Right(Seq(Term.Array(Seq())))
  //   check(script, result)
  // }
  // test("parse List") {
  //   val script = "[1 2 \"three\"]"
  //   val result = Right(Seq(Term.Array(Seq(Term.IntegerLiteral(1), Term.IntegerLiteral(2), Term.StringLiteral("three")))))
  //   check(script, result)
  // }
  // test("parse let expression") {
  //   val script = "let x = 5 in x end"
  //   val result = Right(Seq(Term.LetExpression(Seq((Name("x"), Term.IntegerLiteral(5))), Term.NameTerm(Name("x")))))
  //   check(script, result)
  // }
  // test("parse conditionals") {
  //   val script = "if true false else true"
  //   val result = Right(Seq(
  //     Term.IfExpression(
  //       Term.BooleanLiteral(true), 
  //       Term.BooleanLiteral(false),
  //       Term.BooleanLiteral(true)
  //     )))
  //   check(script, result)
  // }
  // test("parse Lambda") {
  //   val script = "\\x -> x"
  //   val result = Right(Seq(
  //     Term.Lambda(Seq(Name("x")), Seq(Term.NameTerm(Name("x"))))
  //   ))
  //   check(script, result)
  // }
}
