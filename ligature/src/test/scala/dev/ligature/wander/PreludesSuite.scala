// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// package dev.ligature.wander.preludes

// import dev.ligature.{Word, LigatureValue, LigatureError, Triple}
// import dev.ligature.wander.Token
// import dev.ligature.wander.ScriptResult
// import cats.effect.IO
// import scala.collection.mutable.ListBuffer
// import dev.ligature.wander.preludes.termsToTriples
// import dev.ligature.wander.*

// def i(i: String): Word = Word.fromString(i).getOrElse(???)

// class LibrarysSuite extends munit.FunSuite {
//   test("termsToTriples empty input") {
//     val input = Seq()
//     val expected = Right(Seq())
//     assertEquals(termsToTriples(input, ListBuffer()), expected)
//   }
//   test("termsToTriples single Triple") {
//     val input = Seq(Term.List(
//         Seq(
//             Term.WordLiteral(i("a")),
//             Term.WordLiteral(i("b")),
//             Term.WordLiteral(i("c")))))
//     val expected = Right(Seq(Triple(i("a"), i("b"), i("c"))))
//     assertEquals(termsToTriples(input, ListBuffer()), expected)
//   }
// }

// class InstanceLibrarySuite extends WanderSuiteInstanceLibrary {
//   test("add/remove Datasets") {
//     val input = """addDataset("hello") addDataset("hello2") removeDataset("hello2") datasets()"""
//     val result = """["hello"]""" //LigatureValue.ListValue(Seq(LigatureValue.LigatureValue(LigatureValue.String("hello"))))
//     check(input, result)
//   }
//   test("add Triples") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>]]) allTriples("hello")"""
//     val result = "[[<a> <b> <c>]]"
//     check(input, result)
//   }
//   test("add/remove Triples") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |removeTriples("hello" [[<a2> <b2> <c2>]]) allTriples("hello")""".stripMargin
//     val result = "[[<a> <b> <c>] [<e> <f> <g>]]"
//     check(input, result)
//   }
//   test("query Triples") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |query("hello" <a> <b> <c>)""".stripMargin
//     val result = "[[<a> <b> <c>]]"
//     check(input, result)
//   }
//   test("query Triples no match") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |query("hello" <a1> <b> <c>)""".stripMargin
//     val result = "[]"
//     check(input, result)
//   }
//   test("query Triples full wildcard match") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>][<a2> <b2> <c2>][<e> <f> <g>]])
//                   |query("hello" ? ? ?)""".stripMargin
//     val result = "[[<a> <b> <c>] [<a2> <b2> <c2>] [<e> <f> <g>]]"
//     check(input, result)
//   }
//   test("query Triples partial wildcard match") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>][<a2> <b> <c2>][<e> <f> <g>]])
//                   |query("hello" ? <b> ?)""".stripMargin
//     val result = "[[<a> <b> <c>] [<a2> <b> <c2>]]"
//     check(input, result)
//   }
//   test("query with closure") {
//     val input = """addDataset("hello") addTriples("hello" [[<a> <b> <c>][<a2> <b> <c2>][<e> <f> <g>]])
//                   |query("hello" { match -> match(? <b> ?)})""".stripMargin
//     val result = "[[<a> <b> <c>] [<a2> <b> <c2>]]"
//     check(input, result)
//   }
// }
