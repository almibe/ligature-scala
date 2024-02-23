// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// package dev.ligature.bend.preludes

// import dev.ligature.{Identifier, LigatureValue, LigatureError, Statement}
// import dev.ligature.bend.Token
// import dev.ligature.bend.ScriptResult
// import cats.effect.IO
// import scala.collection.mutable.ListBuffer
// import dev.ligature.bend.preludes.termsToStatements
// import dev.ligature.bend.*

// def i(i: String): Identifier = Identifier.fromString(i).getOrElse(???)

// class LibrarysSuite extends munit.FunSuite {
//   test("termsToStatements empty input") {
//     val input = Seq()
//     val expected = Right(Seq())
//     assertEquals(termsToStatements(input, ListBuffer()), expected)
//   }
//   test("termsToStatements single Statement") {
//     val input = Seq(Term.List(
//         Seq(
//             Term.IdentifierLiteral(i("a")),
//             Term.IdentifierLiteral(i("b")),
//             Term.IdentifierLiteral(i("c")))))
//     val expected = Right(Seq(Statement(i("a"), i("b"), i("c"))))
//     assertEquals(termsToStatements(input, ListBuffer()), expected)
//   }
// }

// class InstanceLibrarySuite extends WanderSuiteInstanceLibrary {
//   test("add/remove Datasets") {
//     val input = """addDataset("hello") addDataset("hello2") removeDataset("hello2") datasets()"""
//     val result = """["hello"]""" //WanderValue.ListValue(Seq(WanderValue.LigatureValue(LigatureValue.StringLiteral("hello"))))
//     check(input, result)
//   }
//   test("add Statements") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>]]) allStatements("hello")"""
//     val result = "[[<a> <b> <c>]]"
//     check(input, result)
//   }
//   test("add/remove Statements") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |removeStatements("hello" [[<a2> <b2> <c2>]]) allStatements("hello")""".stripMargin
//     val result = "[[<a> <b> <c>] [<e> <f> <g>]]"
//     check(input, result)
//   }
//   test("query Statements") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |query("hello" <a> <b> <c>)""".stripMargin
//     val result = "[[<a> <b> <c>]]"
//     check(input, result)
//   }
//   test("query Statements no match") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |query("hello" <a1> <b> <c>)""".stripMargin
//     val result = "[]"
//     check(input, result)
//   }
//   test("query Statements full wildcard match") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |query("hello" ? ? ?)""".stripMargin
//     val result = "[[<a> <b> <c>] [<a2> <b2> <c2>] [<e> <f> <g>]]"
//     check(input, result)
//   }
//   test("query Statements partial wildcard match") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b> <c2>][<e> <f> <g>]])
//                   |query("hello" ? <b> ?)""".stripMargin
//     val result = "[[<a> <b> <c>] [<a2> <b> <c2>]]"
//     check(input, result)
//   }
//   test("query with closure") {
//     val input = """addDataset("hello") addStatements("hello" [[<a> <b> <c>][<a2> <b> <c2>][<e> <f> <g>]])
//                   |query("hello" { match -> match(? <b> ?)})""".stripMargin
//     val result = "[[<a> <b> <c>] [<a2> <b> <c2>]]"
//     check(input, result)
//   }
// }
