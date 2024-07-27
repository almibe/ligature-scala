// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

// package dev.ligature.lig

// import munit.FunSuite
// import dev.ligature.{Word, LigatureValue, Statement}
// import dev.ligature.gaze.Gaze

// class LigSuite extends FunSuite {
//   val testWord = Word.fromString("test").getOrElse(???)
//   def word(id: String) = Word.fromString(id).getOrElse(???)

//   test("basic Statement with all Entities") {
//     val statement = Statement(
//       word("e1"),
//       word("a1"),
//       word("e2")
//     )
//     val lines = write(List(statement).iterator)
//     val resStatements = read(lines)
//     resStatements match {
//       case Right(statements) => assertEquals(statements, List(statement))
//       case Left(err)         => fail("failed", clues(err))
//     }
//   }

//   test("list of Statements with Literal Values") {
//     val statements = List(
//       Statement(
//         word("e1"),
//         word("a1"),
//         word("e2")
//       ),
//       Statement(
//         word("e2"),
//         word("a2"),
//         LigatureValue.StringValue("string literal")
//       ),
//       Statement(
//         word("e2"),
//         word("a3"),
//         LigatureValue.IntegerValue(Long.MaxValue)
//       )
//     )
//     val lines = write(statements.iterator)
//     val resStatements = read(lines)
//     resStatements match {
//       case Right(res) => assertEquals(res, statements)
//       case Left(err)  => fail("failed", clues(err))
//     }
//   }

//   test("parse Statement from multi-line String") {
//     val statements =
//       """
//         |<a> <b> <c>
//         |<a> <b> 123
//         |<a> <b> "Test"
//         |""".stripMargin
//     val expectedStatements = Set(
//       Statement(word("a"), word("b"), word("c")),
//       Statement(word("a"), word("b"), LigatureValue.IntegerValue(123)),
//       Statement(word("a"), word("b"), LigatureValue.StringValue("Test")),
//     )
//     val resStatements = read(statements)
//     resStatements match {
//       case Right(res) => assertEquals(res.toSet, expectedStatements)
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
