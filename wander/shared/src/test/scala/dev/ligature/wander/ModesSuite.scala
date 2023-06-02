/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError, Statement}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO
import scala.collection.mutable.ListBuffer

def i(i: String): Identifier = Identifier.fromString(i).getOrElse(???)

class ModesSuite extends munit.FunSuite {
  test("termsToStatements empty input") {
    val input = Seq()
    val expected = Right(Seq())
    assertEquals(termsToStatements(input, ListBuffer()), expected)
  }
  test("termsToStatements single Statement") {
    val input = Seq(
      Term.List(
        Seq(
          Term.IdentifierLiteral(i("a")),
          Term.IdentifierLiteral(i("b")),
          Term.IdentifierLiteral(i("c"))
        )
      )
    )
    val expected = Right(Seq(Statement(i("a"), i("b"), i("c"))))
    assertEquals(termsToStatements(input, ListBuffer()), expected)
  }
}

class InstanceModeSuite extends WanderSuiteInstanceMode {
  test("add/remove Datasets") {
    val input = """addDataset("hello") addDataset("hello2") removeDataset("hello2") datasets()"""
    val result =
      """["hello"]""" // WanderValue.ListValue(Seq(WanderValue.LigatureValue(LigatureLiteral.StringLiteral("hello"))))
    check(input, result)
  }
  test("add Statements") {
    val input =
      """addDataset("hello") addStatements("hello" [[<a> <b> <c>]]) allStatements("hello")"""
    val result = "[[<a> <b> <c>]]"
    check(input, result)
  }
  test("add/remove Statements") {
    val input =
      """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]]) 
                  |removeStatements("hello" [[<a2> <b2> <c2>]]) allStatements("hello")""".stripMargin
    val result = "[[<a> <b> <c>] [<e> <f> <g>]]"
    check(input, result)
  }
  test("query Statements") {
    val input =
      """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]]) 
                  |query("hello" <a> <b> <c>)""".stripMargin
    val result = "[[<a> <b> <c>]]"
    check(input, result)
  }
  test("query Statements no match") {
    val input =
      """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]]) 
                  |query("hello" <a1> <b> <c>)""".stripMargin
    val result = "[]"
    check(input, result)
  }
  test("query Statements full wildcard match") {
    val input =
      """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]]) 
                  |query("hello" ? ? ?)""".stripMargin
    val result = "[[<a> <b> <c>] [<a2> <b2> <c2>] [<e> <f> <g>]]"
    check(input, result)
  }
  test("query Statements partial wildcard match") {
    val input =
      """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b> <c2>][<e> <f> <g>]]) 
                  |query("hello" ? <b> ?)""".stripMargin
    val result = "[[<a> <b> <c>] [<a2> <b> <c2>]]"
    check(input, result)
  }
}
