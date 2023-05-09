/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.gaze.Gaze
import dev.ligature.{Identifier, Statement, LigatureLiteral}
import munit.FunSuite

class LigInputSuite extends FunSuite {
  test("id gen") {
    val input = parseIdentifier(Gaze.from("<{}>"), Map(), None).getOrElse(???)
    val resultRegEx = "[0-9_\\-a-fA-F]{12}".r
    assert(resultRegEx.matches(input.name))
  }

  test("id gen with prefix") {
    val input =
      parseIdentifier(Gaze.from("<this:is:a/prefix{}>"), Map(), None)
        .getOrElse(???)
    val resultRegEx = "this:is:a/prefix[0-9_\\-a-fA-F]{12}".r
    assert(resultRegEx.matches(input.name))
  }

  test("id gen in infix") {
    val input =
      parseIdentifier(Gaze.from("<this{}is:a/infix>"), Map(), None)
        .getOrElse(???)
    val resultRegEx = "this[0-9_\\-a-fA-F]{12}is:a/infix".r
    assert(resultRegEx.matches(input.name))
  }

  test("id gen in postfix") {
    val input =
      parseIdentifier(Gaze.from("<this::is:a/postfix/{}>"), Map(), None)
        .getOrElse(???)
    val resultRegEx = "this::is:a/postfix/[0-9_\\-a-fA-F]{12}".r
    assert(resultRegEx.matches(input.name))
  }

  test("basic prefix definition") {
    val res = parsePrefix(Gaze.from("prefix name = prefixed:identifier:"))
      .getOrElse(???)
      .get
    assertEquals(res, ("name", "prefixed:identifier:"))
  }

  test("prefixed id") {
    val input =
      parseIdentifier(
        Gaze.from("prefix:world"),
        Map("prefix" -> "hello:"),
        None
      )
        .getOrElse(???)
    assertEquals(input.name, "hello:world")
  }

  test("copy character test with entity and attribute") {
    val input = "<e> <a> 234\n^ ^ 432"
    val expected = List(
      Statement(
        Identifier.fromString("e").getOrElse(???),
        Identifier.fromString("a").getOrElse(???),
        LigatureLiteral.IntegerLiteral(234)
      ),
      Statement(
        Identifier.fromString("e").getOrElse(???),
        Identifier.fromString("a").getOrElse(???),
        LigatureLiteral.IntegerLiteral(432)
      )
    )
    val res = read(input)
    res match {
      case Right(statements) => assertEquals(statements, expected)
      case Left(err)         => fail("failed", clues(err))
    }
  }

  test("error copy character test") {
    val input = "<this:is:an:error> <a> ^"
    val res = read(input)
    assert(res.isLeft)
  }

  test("copy character test with attribute and value") {
    val input = "<e> <a> 234\n<e2> ^ ^"
    val expected = List(
      Statement(
        Identifier.fromString("e").getOrElse(???),
        Identifier.fromString("a").getOrElse(???),
        LigatureLiteral.IntegerLiteral(234)
      ),
      Statement(
        Identifier.fromString("e2").getOrElse(???),
        Identifier.fromString("a").getOrElse(???),
        LigatureLiteral.IntegerLiteral(234)
      )
    )
    val result = read(input)
    result match {
      case Right(statements) => assertEquals(statements, expected)
      case Left(err)         => fail("failed", clues(err))
    }
  }

  test("prefix error test") {
    val input = "prefix x = this:\nx x:is:a x:prefix"
    val result = read(input)
    assert(result.isLeft)
  }

  test("error prefix test") {
    val input = "x x:is:an x:error"
    val result = read(input)
    assert(result.isLeft)
  }

  test("basic prefix test") {
    val input = "prefix x = this:\nx:hello x:cruel x:world"
    val result = read(input)
    result match {
      case Right(statements) =>
        assertEquals(statements.length, 1)
        assertEquals(
          statements(0).entity,
          Identifier.fromString("this:hello").getOrElse(???)
        )
        assertEquals(
          statements(0).attribute,
          Identifier.fromString("this:cruel").getOrElse(???)
        )
        assertEquals(
          statements(0).value,
          Identifier.fromString("this:world").getOrElse(???)
        )
      case Left(err) => fail("failed", clues(err))
    }
  }

  test("entity gen id prefix test") {
    val input = "prefix x = this:\nx:hello{} x:cruel x:world"
    val result = read(input)
    result match {
      case Right(statements) =>
        assertEquals(statements.length, 1)
//        assertEquals(statements(0).entity, Identifier.fromString("this:hello").getOrElse(???))
        assertEquals(
          statements(0).attribute,
          Identifier.fromString("this:cruel").getOrElse(???)
        )
        assertEquals(
          statements(0).value,
          Identifier.fromString("this:world").getOrElse(???)
        )
      case Left(err) => fail("failed", clues(err))
    }
  }

  test("complex prefix test") {
    val input = "prefix x = this:\nx:{} x:{}is:a x:prefix{}"
    val result = read(input)
    result match {
      case Right(statements) =>
        assertEquals(statements.length, 1)
      // TODO add more checks
      case Left(err) => fail("failed", clues(err))
    }
  }
}
