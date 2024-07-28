/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite

class ParserSuite extends FunSuite {
  def check(script: String): Either[WanderError, Seq[Term]] =
    val tokens = tokenize(script) match
      case Left(err)     => return Left(err)
      case Right(tokens) => tokens
    parse(tokens)

  // test("parse Grouping") {
  //   val input = check("testing 1 2 3")
  //   val expected = Right(
  //     Seq(
  //       Term.Application(
  //         Seq(
  //           Term.FieldPathTerm(FieldPath(Seq(Field("testing")))),
  //           Term.Int(1),
  //           Term.Int(2),
  //           Term.Int(3)
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(input, expected)
  // }
  // test("parse Integer") {
  //   val result = check("-321")
  //   val expected = Right(Seq(Term.Int(-321)))
  //   assertEquals(result, expected)
  // }
  // test("parse Bytes") {
  //   val result = check("0x11")
  //   val expected = Right(Seq(Term.Bytes(Seq(17.byteValue))))
  //   assertEquals(result, expected)
  // }
  // test("parse Word") {
  //   val result = check("`hello`")
  //   val expected = Right(Seq(Term.Word("hello")))
  //   assertEquals(result, expected)
  // }
  // test("parse Triple") {
  //   val result = check("`a` `b` `c`")
  //   val expected = Right(
  //     Seq(Term.Application(Seq(Term.Word("a"), Term.Word("b"), Term.Word("c"))))
  //   )
  //   assertEquals(result, expected)
  // }
  test("parse String") {
    val result = check("\"hello\"")
    val expected = Right(Seq(Term.String("hello")))
    assertEquals(result, expected)
  }
  test("parse String with quotes") {
    val result = check("\"\\\"hello\\\"\"")
    val expected = Right(Seq(Term.String("\"hello\"")))
    assertEquals(result, expected)
  }

  // test("parse interpolated String") {
  //   val result = check("i\"hello $(UName)\"")
  //   val expected = Right(Seq(Term.String("hello $(UName)", true)))
  //   assertEquals(result, expected)
  // }
  // test("parse Boolean") {
  //   val result = check("false")
  //   val expected = Right(Seq(Term.BooleanLiteral(false)))
  //   assertEquals(result, expected)
  // }
  // test("parse Function Calls") {
  //   val input = check("not true")
  //   val expected =
  //     Right(
  //       Seq(
  //         Term.Application(
  //           Seq(Term.FieldPathTerm(FieldPath(Seq(Field("not")))), Term.BooleanLiteral(true))
  //         )
  //       )
  //     )
  //   assertEquals(input, expected)
  // }
  // test("parse Function Call with question mark argument") {
  //   val result = check("query ?")
  //   val expected = Right(
  //     Seq(
  //       Term.Application(
  //         Seq(
  //           Term.FieldPathTerm(FieldPath(Seq(Field("query")))),
  //           Term.Slot("")
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  test("parse empty List") {
    val result = check("[]")
    val expected = Right(Seq(Term.Array(Seq())))
    assertEquals(result, expected)
  }
  // test("parse List") {
  //   val result = check("[1, 2, \"three\"]")
  //   val expected = Right(
  //     Seq(
  //       Term.Array(
  //         Seq(Term.Int(1), Term.Int(2), Term.String("three"))
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse let expression") {
  //   val result = check("x = 5")
  //   val expected =
  //     Right(Seq(Term.Binding(Field("x"), None, Term.Int(5))))
  //   assertEquals(result, expected)
  // }
  // test("parse Lambda") {
  //   val result = check("\\x -> x")
  //   val expected = Right(
  //     Seq(
  //       Term.Lambda(Seq(Field("x")), Term.FieldPathTerm(FieldPath(Seq(Field("x")))))
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse let expression with lambda") {
  //   val result = check("id = \\x -> x")
  //   val expected =
  //     Right(
  //       Seq(
  //         Term.Binding(
  //           Field("id"),
  //           None,
  //           Term.Lambda(Seq(Field("x")), Term.FieldPathTerm(FieldPath(Seq(Field("x")))))
  //         )
  //       )
  //     )
  //   assertEquals(result, expected)
  // }
  // test("parse empty grouping") {
  //   val result = check("()")
  //   val expected = Right(
  //     Seq(
  //       Term.Grouping(Seq())
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse simple expression grouping") {
  //   val result = check("(x)")
  //   val expected = Right(
  //     Seq(
  //       Term.Grouping(
  //         Seq(
  //           Term.FieldPathTerm(FieldPath(Seq(Field("x"))))
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse grouping") {
  //   val result = check("(true, 4)")
  //   val expected = Right(
  //     Seq(
  //       Term.Grouping(
  //         Seq(
  //           Term.BooleanLiteral(true),
  //           Term.Int(4)
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse empty Dataset".ignore) {
  //   val result = check("{}")
  //   val expected = Right(
  //     Seq(
  //       Term.Dataset(Set())
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse Module with one entry") {
  //   val result = check("{x = 5}")
  //   val expected = Right(
  //     Seq(
  //       Term.Module(
  //         Seq(
  //           (Field("x"), Term.Int(5))
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse Module with multiple entries") {
  //   val result = check("{x = 5, y = 6}")
  //   val expected = Right(
  //     Seq(
  //       Term.Module(
  //         Seq((Field("x"), Term.Int(5)), (Field("y"), Term.Int(6)))
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  test("parse network with one triple") {
    val result = check("{ a b c }")
    val expected = Right(
      Seq(
        Term.Network(
          Seq(Term.NetworkRoot(Seq(Term.Word("a"), Term.Word("b"), Term.Word("c"))))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse network with multiple triples".ignore) {
    val result = check("{ a b c, a b d, b c d }")
    val expected = Right(
      Seq(
        Term.Network(
          Seq(
            Term.NetworkRoot(Seq(Term.Word("a"), Term.Word("b"), Term.Word("c"))),
            Term.NetworkRoot(Seq(Term.Word("a"), Term.Word("b"), Term.Word("d"))),
            Term.NetworkRoot(Seq(Term.Word("b"), Term.Word("c"), Term.Word("d")))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse network with slots") {
    val result = check("{ $ $ $ }")
    val expected = Right(
      Seq(
        Term.Network(
          Seq(Term.NetworkRoot(Seq(Term.Slot(""), Term.Slot(""), Term.Slot(""))))
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse network with named slots") {
    val result = check("{ $a $b $c }")
    val expected = Right(
      Seq(
        Term.Network(
          Seq(Term.NetworkRoot(Seq(Term.Slot("a"), Term.Slot("b"), Term.Slot("c"))))
        )
      )
    )
    assertEquals(result, expected)
  }
  // test("parse when expression") {
  //   val result = check("when true => 6, false => 7 end")
  //   val expected = Right(
  //     Seq(
  //       Term.WhenExpression(
  //         Seq(
  //           (Term.BooleanLiteral(true), Term.Int(6)),
  //           (Term.BooleanLiteral(false), Term.Int(7))
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse tagged Field in assignment") {
  //   val result = check("zero: Core.Int = 0")
  //   val expected = Right(
  //     Seq(
  //       Term.Binding(
  //         Field("zero"),
  //         Some(FieldPath(Seq(Field("Core"), Field("Int")))),
  //         Term.Int(0)
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
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
  // test("parse lambda bindings syntax") {
  //   val result = check("id i i2 = i")
  //   val expected = Right(
  //     Seq(
  //       Term.Binding(
  //         Field("id"),
  //         None,
  //         Term.Lambda(Seq(Field("i"), Field("i2")), Term.FieldPathTerm(FieldPath(Seq(Field("i")))))
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse pipe") {
  //   val result = check("true | not")
  //   val expected = Right(
  //     Seq(
  //       Term.Application(
  //         Seq(
  //           Term.FieldPathTerm(FieldPath(List(Field("not")))),
  //           Term.BooleanLiteral(true)
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse chained pipe") {
  //   val result = check("true | not | not")
  //   val expected = Right(
  //     Seq(
  //       Term.Application(
  //         Seq(
  //           Term.FieldPathTerm(FieldPath(List(Field("not")))),
  //           Term.Application(
  //             Seq(
  //               Term.FieldPathTerm(FieldPath(List(Field("not")))),
  //               Term.BooleanLiteral(true)
  //             )
  //           )
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
  // test("parse Triple with empty Record for Value") {
  //   val result = check("`a` `a` {}")
  //   val expected = Right(
  //     Seq(
  //       Term.Application(
  //         Seq(
  //           Term.Word("a"),
  //           Term.Word("a"),
  //           Term.Module(Seq())
  //         )
  //       )
  //     )
  //   )
  //   assertEquals(result, expected)
  // }
}
