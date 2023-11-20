/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class ParserSuite extends FunSuite {
  def ident(identifier: String): Term =
    Identifier.fromString(identifier) match
      case Left(value)  => ??? // just crash
      case Right(value) => Term.IdentifierLiteral(value)

  def check(script: String): Either[WanderError, Term] =
    val tokens = tokenize(script) match
      case Left(err)     => return Left(err)
      case Right(tokens) => tokens
    parse(tokens)

  test("parse Application") {
    val input = check("testing 1 2 3")
    val expected = Right(
      Term.Application(
        Seq(
          Term.NameTerm(Name("testing")),
          Term.IntegerLiteral(1),
          Term.IntegerLiteral(2),
          Term.IntegerLiteral(3)
        )
      )
    )
    assertEquals(input, expected)
  }
  test("parse nothing keyword") {
    val result = check("nothing")
    val expected = Right(Term.NothingLiteral)
    assertEquals(result, expected)
  }
  test("parse Identifier") {
    val result = check("<test2>")
    val expected = Right(ident("test2"))
    assertEquals(result, expected)
  }
  test("parse Integer") {
    val result = check("-321")
    val expected = Right(Term.IntegerLiteral(-321))
    assertEquals(result, expected)
  }
  test("parse String") {
    val result = check("\"hello\"")
    val expected = Right(Term.StringLiteral("hello"))
    assertEquals(result, expected)
  }
  test("parse Boolean") {
    val result = check("false")
    val expected = Right(Term.BooleanLiteral(false))
    assertEquals(result, expected)
  }
  test("parse Function Calls") {
    val input = check("not true")
    val expected =
      Right(Term.Application(Seq(Term.NameTerm(Name("not")), Term.BooleanLiteral(true))))
    assertEquals(input, expected)
  }
  test("parse Function Call with question mark argument") {
    val result = check("query ? ? ?")
    val expected = Right(
      Term.Application(
        Seq(Term.NameTerm(Name("query")), Term.QuestionMark, Term.QuestionMark, Term.QuestionMark)
      )
    )
    assertEquals(result, expected)
  }
  test("parse empty List") {
    val result = check("[]")
    val expected = Right(Term.Array(Seq()))
    assertEquals(result, expected)
  }
  test("parse List") {
    val result = check("[1, 2, \"three\"]")
    val expected = Right(
      Term.Array(Seq(Term.IntegerLiteral(1), Term.IntegerLiteral(2), Term.StringLiteral("three")))
    )
    assertEquals(result, expected)
  }
  test("parse let expression") {
    val result = check("let x 5")
    val expected = Right(Term.LetExpression(Name("x"), Term.IntegerLiteral(5)))
    assertEquals(result, expected)
  }
  // test("parse conditionals") {
  //   val result = check("if true false else true")
  //   val expected = Right(
  //     Term.IfExpression(
  //       Term.BooleanLiteral(true),
  //       Term.BooleanLiteral(false),
  //       Term.BooleanLiteral(true)
  //     ))
  //   assertEquals(result, expected)
  // }
  test("parse Lambda") {
    val result = check("\\x -> x")
    val expected = Right(
      Term.Lambda(Seq(Name("x")), Term.NameTerm(Name("x")))
    )
    assertEquals(result, expected)
  }
  test("parse empty Record") {
    val result = check("{}")
    val expected = Right(Term.Record(Seq()))
    assertEquals(result, expected)
  }
  test("parse empty Record") {
    val result = check("{x = 5}")
    val expected = Right(Term.Record(Seq((Name("x"), Term.IntegerLiteral(5)))))
    assertEquals(result, expected)
  }
  test("parse lambda inside of Record") {
    val result = check("{id = \\x -> x}")
    val expected =
      Right(Term.Record(Seq((Name("id"), Term.Lambda(Seq(Name("x")), Term.NameTerm(Name("x")))))))
    assertEquals(result, expected)
  }
  test("parse multi value record") {
    val result = check("{a = 5, id = \\x -> x, b = \"hello\"}")
    val expected = Right(
      Term.Record(
        Seq(
          (Name("a"), Term.IntegerLiteral(5)),
          (Name("id"), Term.Lambda(Seq(Name("x")), Term.NameTerm(Name("x")))),
          (Name("b"), Term.StringLiteral("hello"))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse let expression with lambda") {
    val result = check("let id \\x -> x")
    val expected =
      Right(Term.LetExpression(Name("id"), Term.Lambda(Seq(Name("x")), Term.NameTerm(Name("x")))))
    assertEquals(result, expected)
  }
  test("parse empty grouping") {
    val result = check("()")
    val expected = Right(
      Term.Grouping(Seq())
    )
    assertEquals(result, expected)
  }
  test("parse grouping") {
    val result = check("(let y true, \\x -> x, Bool.not y)")
    val expected = Right(
      Term.Grouping(
        Seq(
          Term.LetExpression(Name("y"), Term.BooleanLiteral(true)),
          Term.Lambda(Seq(Name("x")), Term.NameTerm(Name("x"))),
          Term.Application(Seq(Term.NameTerm(Name("Bool.not")), Term.NameTerm(Name("y"))))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse when expression") {
    val result = check("when ( true => 6, false => 7 )")
    val expected = Right(
      Term.WhenExpression(
        Seq(
          (Term.BooleanLiteral(true), Term.IntegerLiteral(6)),
          (Term.BooleanLiteral(false), Term.IntegerLiteral(7))
        )
      )
    )
    assertEquals(result, expected)
  }
}
