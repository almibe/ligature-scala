/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze
import dev.ligature.lig.CommonSuite

class DLigParserSuite extends FunSuite {
  test("basic prefix") {
    val res = parsePrefix(Gaze.from("prefix name = prefixed:identifier:")).getOrElse(???).get
    assertEquals(res, ("name", "prefixed:identifier:"))
  }
  
  test("id gen") {
    val input = parseIdentifier(Gaze.from("{}"), Map()).getOrElse(???)
    val resultRegEx = "[0-9_-a-fA-F]{12}".r
    assert(resultRegEx.matches(input.name))
  }

  test("id gen as entity") {
    val input = "<{}>"
    val resultRegEx = "[0-9_-a-fA-F]{12}".r
    ???
  }

  test("id gen with prefix") {
    val input = "<this:is:a/prefix{}>"
    val resultRegEx = "<this:is:a/prefix[0-9_-a-fA-F]{12}>".r
    ???
  }

  test("id gen in infix") {
    val input = "<this{}is:a/infix>"
    val resultRegEx = "<this[0-9_-a-fA-F]{12}:is:a/infix>".r
    ???
  }

  test("id gen in postfix") {
    val input = "<this::is:a/postfix/{}>"
    val resultRegEx = "<this::is:a/postfix[0-9_-a-fA-F]{12}>".r
    ???
  }

}
