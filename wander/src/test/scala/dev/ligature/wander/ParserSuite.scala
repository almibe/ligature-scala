/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class ParserSuite extends FunSuite {
  def check(script: String): Either[WanderError, Seq[Term]] =
    val tokens = tokenize(script) match
      case Left(err)     => return Left(err)
      case Right(tokens) => tokens
    parse(tokens)

  test("parse Grouping") {
    val input = check("testing 1 2 3")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.FieldPathTerm(FieldPath(Seq(Field("testing")))),
            Term.IntegerLiteral(1),
            Term.IntegerLiteral(2),
            Term.IntegerLiteral(3)
          )
        )
      )
    )
    assertEquals(input, expected)
  }
  test("parse Integer") {
    val result = check("-321")
    val expected = Right(Seq(Term.IntegerLiteral(-321)))
    assertEquals(result, expected)
  }
  test("parse Bytes") {
    val result = check("0x11")
    val expected = Right(Seq(Term.Bytes(Seq(17.byteValue))))
    assertEquals(result, expected)
  }
  test("parse String") {
    val result = check("\"hello\"")
    val expected = Right(Seq(Term.StringLiteral("hello")))
    assertEquals(result, expected)
  }
  test("parse String with quotes") {
    val result = check("\"\\\"hello\\\"\"")
    val expected = Right(Seq(Term.StringLiteral("\"hello\"")))
    assertEquals(result, expected)
  }

  test("parse interpolated String") {
    val result = check("i\"hello $(UName)\"")
    val expected = Right(Seq(Term.StringLiteral("hello $(UName)", true)))
    assertEquals(result, expected)
  }
  test("parse Boolean") {
    val result = check("false")
    val expected = Right(Seq(Term.BooleanLiteral(false)))
    assertEquals(result, expected)
  }
  test("parse Function Calls") {
    val input = check("not true")
    val expected =
      Right(
        Seq(
          Term.Application(
            Seq(Term.FieldPathTerm(FieldPath(Seq(Field("not")))), Term.BooleanLiteral(true))
          )
        )
      )
    assertEquals(input, expected)
  }
  test("parse Function Call with question mark argument") {
    val result = check("query ?")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.FieldPathTerm(FieldPath(Seq(Field("query")))),
            Term.QuestionMark
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse empty List") {
    val result = check("[]")
    val expected = Right(Seq(Term.Array(Seq())))
    assertEquals(result, expected)
  }
  test("parse List") {
    val result = check("[1, 2, \"three\"]")
    val expected = Right(
      Seq(
        Term.Array(
          Seq(Term.IntegerLiteral(1), Term.IntegerLiteral(2), Term.StringLiteral("three"))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse let expression") {
    val result = check("x = 5")
    val expected =
      Right(Seq(Term.Binding(Field("x"), None, Term.IntegerLiteral(5))))
    assertEquals(result, expected)
  }
  test("parse Lambda") {
    val result = check("\\x -> x")
    val expected = Right(
      Seq(
        Term.Lambda(Seq(Field("x")), Term.FieldPathTerm(FieldPath(Seq(Field("x")))))
      )
    )
    assertEquals(result, expected)
  }
  test("parse let expression with lambda") {
    val result = check("id = \\x -> x")
    val expected =
      Right(
        Seq(
          Term.Binding(
            Field("id"),
            None,
            Term.Lambda(Seq(Field("x")), Term.FieldPathTerm(FieldPath(Seq(Field("x")))))
          )
        )
      )
    assertEquals(result, expected)
  }
  test("parse empty grouping") {
    val result = check("()")
    val expected = Right(
      Seq(
        Term.Grouping(Seq())
      )
    )
    assertEquals(result, expected)
  }
  test("parse simple expression grouping") {
    val result = check("(x)")
    val expected = Right(
      Seq(
        Term.Grouping(
          Seq(
            Term.FieldPathTerm(FieldPath(Seq(Field("x"))))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse grouping") {
    val result = check("(true, 4)")
    val expected = Right(
      Seq(
        Term.Grouping(
          Seq(
            Term.BooleanLiteral(true),
            Term.IntegerLiteral(4)
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse empty Module") {
    val result = check("{}")
    val expected = Right(
      Seq(
        Term.Module(Seq())
      )
    )
    assertEquals(result, expected)
  }
  test("parse Module with one entry") {
    val result = check("{x = 5}")
    val expected = Right(
      Seq(
        Term.Module(
          Seq(
            (Field("x"), Term.IntegerLiteral(5))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse record with multiple entries") {
    val result = check("{x = 5, y = 6}")
    val expected = Right(
      Seq(
        Term.Module(
          Seq((Field("x"), Term.IntegerLiteral(5)), (Field("y"), Term.IntegerLiteral(6)))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse when expression") {
    val result = check("when true => 6, false => 7 end")
    val expected = Right(
      Seq(
        Term.WhenExpression(
          Seq(
            (Term.BooleanLiteral(true), Term.IntegerLiteral(6)),
            (Term.BooleanLiteral(false), Term.IntegerLiteral(7))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse tagged Field in assignment") {
    val result = check("zero: Core.Int = 0")
    val expected = Right(
      Seq(
        Term.Binding(
          Field("zero"),
          Some(FieldPath(Seq(Field("Core"), Field("Int")))),
          Term.IntegerLiteral(0)
        )
      )
    )
    assertEquals(result, expected)
  }
  // test("parse tagged lambda") {
  //   val result = check("x: Core.Int -> Core.Bool = \\i -> false")
  //   val expected = Right(
  //     Seq(
  //       Term.Binding(
  //         TaggedField(Field("x"), Some(FieldPath(Seq(Field("Core"), Field("Int")))), UName("Core.Bool")))),
  //         Term.Lambda(UName("i"), Term.BooleanLiteral(false))
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  test("parse lambda bindings syntax") {
    val result = check("id i i2 = i")
    val expected = Right(
      Seq(
        Term.Binding(
          Field("id"),
          None,
          Term.Lambda(Seq(Field("i"), Field("i2")), Term.FieldPathTerm(FieldPath(Seq(Field("i")))))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse pipe") {
    val result = check("true | not")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.FieldPathTerm(FieldPath(List(Field("not")))),
            Term.BooleanLiteral(true)
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse chained pipe") {
    val result = check("true | not | not")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.FieldPathTerm(FieldPath(List(Field("not")))),
            Term.Application(
              Seq(
                Term.FieldPathTerm(FieldPath(List(Field("not")))),
                Term.BooleanLiteral(true)
              )
            )
          )
        )
      )
    )
    assertEquals(result, expected)
  }
}
