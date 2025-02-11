/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite

class ParserSuite extends FunSuite {
  def check(script: String): Either[WanderError, Seq[LigatureValue]] =
    val tokens = tokenize(script) match
      case Left(err)     => return Left(err)
      case Right(tokens) => tokens
    parse(tokens)

  test("parse application") {
    val input = check("testing 1 2 3")
    val expected = Right(
      Seq(
        LigatureValue.Element("testing"),
        LigatureValue.Element("1"),
        LigatureValue.Element("2"),
        LigatureValue.Element("3")
      )
    )
    assertEquals(input, expected)
  }
  test("parse empty network") {
    val result = check("empty {}")
    val expected = Right(
      Seq(LigatureValue.Element("empty"), LigatureValue.NetworkRef(InMemoryNetwork(Set())))
    )
    assertEquals(result, expected)
  }
  test("parse empty quote") {
    val result = check("empty []")
    val expected = Right(Seq(LigatureValue.Element("empty"), LigatureValue.Quote(Seq())))
    assertEquals(result, expected)
  }
  test("parse String") {
    val result = check("\"hello\"")
    val expected = Right(Seq(LigatureValue.Literal("hello")))
    assertEquals(result, expected)
  }
  test("parse String with quotes") {
    val result = check("\"\\\"hello\\\"\"")
    val expected = Right(Seq(LigatureValue.Literal("\"hello\"")))
    assertEquals(result, expected)
  }
  test("parse network with one entry") {
    val result = check("test {x = 5}")
    val expected = Right(
      Seq(
        LigatureValue.Element("test"),
        LigatureValue.NetworkRef(InMemoryNetwork(Set((LigatureValue.Element("x"), LigatureValue.Element("="), LigatureValue.Element("5")))))
      )
    )
    assertEquals(result, expected)
  }
  test("parse Network with multiple entries") {
    val result = check("{x = 5, y = 6}")
    val expected = Right(
      Seq(
        LigatureValue.NetworkRef(
          InMemoryNetwork(
            Set(
              (LigatureValue.Element("x"), LigatureValue.Element("="), LigatureValue.Element("5")),
              (LigatureValue.Element("y"), LigatureValue.Element("="), LigatureValue.Element("6"))
            )
          )
        )
      )
    )
    assertEquals(result, expected)
  }
}
