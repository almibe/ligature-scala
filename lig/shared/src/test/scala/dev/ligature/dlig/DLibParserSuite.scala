/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze
import dev.ligature.lig.CommonSuite

class DLigParserSuite extends FunSuite {
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
}
