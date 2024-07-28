// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

// package dev.ligature.lig

// import munit.FunSuite
// import dev.ligature.{Word, LigatureValue, Triple}
// import dev.ligature.gaze.Gaze

// class LigSuite extends FunSuite {
//   val testWord = Word.fromString("test").getOrElse(???)
//   def word(id: String) = Word.fromString(id).getOrElse(???)

//   test("basic Triple with all Entities") {
//     val triple = Triple(
//       word("e1"),
//       word("a1"),
//       word("e2")
//     )
//     val lines = write(List(triple).iterator)
//     val resTriples = read(lines)
//     resTriples match {
//       case Right(triples) => assertEquals(triples, List(triple))
//       case Left(err)         => fail("failed", clues(err))
//     }
//   }

//   test("list of Triples with Literal Values") {
//     val triples = List(
//       Triple(
//         word("e1"),
//         word("a1"),
//         word("e2")
//       ),
//       Triple(
//         word("e2"),
//         word("a2"),
//         LigatureValue.StringValue("string literal")
//       ),
//       Triple(
//         word("e2"),
//         word("a3"),
//         LigatureValue.IntegerValue(Long.MaxValue)
//       )
//     )
//     val lines = write(triples.iterator)
//     val resTriples = read(lines)
//     resTriples match {
//       case Right(res) => assertEquals(res, triples)
//       case Left(err)  => fail("failed", clues(err))
//     }
//   }

//   test("parse Triple from multi-line String") {
//     val triples =
//       """
//         |<a> <b> <c>
//         |<a> <b> 123
//         |<a> <b> "Test"
//         |""".stripMargin
//     val expectedTriples = Set(
//       Triple(word("a"), word("b"), word("c")),
//       Triple(word("a"), word("b"), LigatureValue.IntegerValue(123)),
//       Triple(word("a"), word("b"), LigatureValue.StringValue("Test")),
//     )
//     val resTriples = read(triples)
//     resTriples match {
//       case Right(res) => assertEquals(res.toSet, expectedTriples)
//       case Left(err)  => fail("failed", clues(err))
//     }
//   }

//   test("parse words") {
//     val test = "<test>"
//     val word = parseWord(Gaze.from(test), Map(), None)
//     assertEquals(word, Right(testWord))
//   }

//   test("parse complex word") {
//     val wordS = "<http$://&&this@2]34.[42;342?#--__>"
//     val wordRes = parseWord(Gaze.from(wordS), Map(), None)
//     assertEquals(
//       wordRes,
//       Right(word("http$://&&this@2]34.[42;342?#--__"))
//     )
//   }

//   test("parse IntegerValue") {
//     val test = "3452345"
//     val res: Either[LigError, LigatureValue] = parseIntegerValue(Gaze.from(test))
//     assertEquals(res, Right(LigatureValue.IntegerValue(3452345)))
//   }

//   test("parse StringValue") {
//     val test = "\"3452345\\nHello\""
//     val res: Either[LigError, LigatureValue] = parseStringValue(Gaze.from(test))
//     assertEquals(res, Right(LigatureValue.StringValue("3452345\\nHello")))
//   }

//   test("write words") {
//     val res = writeWord(testWord)
//     assertEquals(res, "<test>")
//   }

//   test("write IntegerValue") {
//     val test = LigatureValue.IntegerValue(3535)
//     val res = writeValue(test)
//     assertEquals(res, "3535")
//   }

//   test("write StringValue") {
//     val test = LigatureValue.StringValue("3535 55Hello")
//     val res = writeValue(test)
//     assertEquals(res, "\"3535 55Hello\"")
//   }
// }
