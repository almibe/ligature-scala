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

  test("parse application") {
    val input = check("testing 1 2 3")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("testing"),
            Term.Element("1"),
            Term.Element("2"),
            Term.Element("3")
          )
        )
      )
    )
    assertEquals(input, expected)
  }
  test("parse application with trailing comma") {
    val input = check("testing 1 2 3,")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("testing"),
            Term.Element("1"),
            Term.Element("2"),
            Term.Element("3")
          )
        )
      )
    )
    assertEquals(input, expected)
  }
  test("parse application with trailing comma") {
    val input = check("testing 1 2 3, test2 100")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("testing"),
            Term.Element("1"),
            Term.Element("2"),
            Term.Element("3")
          )
        ),
        Term.Application(
          Seq(
            Term.Element("test2"),
            Term.Element("100")
          )
        )
      )
    )
    assertEquals(input, expected)
  }
  test("parse empty network") {
    val result = check("empty {}")
    val expected = Right(
      Seq(Term.Application(Seq(Term.Element("empty"), Term.Network(Set()))))
    )
    assertEquals(result, expected)
  }
  test("parse empty quote") {
    val result = check("empty ()")
    val expected = Right(
      Seq(Term.Application(Seq(Term.Element("empty"), Term.Quote(Seq()))))
    )
    assertEquals(result, expected)
  }
  test("parse String") {
    val result = check("\"hello\"")
    val expected = Right(Seq(Term.Application(Seq(Term.Element("hello")))))
    assertEquals(result, expected)
  }
  test("parse String with quotes") {
    val result = check("\"\\\"hello\\\"\"")
    val expected = Right(Seq(Term.Application(Seq(Term.Element("\"hello\"")))))
    assertEquals(result, expected)
  }
  test("parse Function Call with question mark argument") {
    val result = check("query ?")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("query"),
            Term.Element("?")
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse simple expression grouping") {
    val result = check("x (y)")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("x"),
            Term.Quote(Seq(Term.Element("y")))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse grouping") {
    val result = check("test (true 4)")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("test"),
            Term.Quote(Seq(Term.Element("true"), Term.Element("4")))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse network with one entry") {
    val result = check("test {x = 5}")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("test"),
            Term.Network(Set(Entry.Role(Element("x"), Element("="), Element("5"))))
          )
        )
      )
    )
    assertEquals(result, expected)
  }
  test("parse Module with multiple entries") {
    val result = check("test {x = 5, y = 6}")
    val expected = Right(
      Seq(
        Term.Application(
          Seq(
            Term.Element("test"),
            Term.Network(
              Set(
                Entry.Role(Element("x"), Element("="), Element("5")),
                Entry.Role(Element("y"), Element("="), Element("6"))
              )
            )
          )
        )
      )
    )
    assertEquals(result, expected)
  }
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
}
