// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

// package dev.ligature.lig

// import munit.FunSuite
// import dev.ligature.{Identifier, LigatureValue, Statement}
// import dev.ligature.gaze.Gaze

// class LigSuite extends FunSuite {
//   val testIdentifier = Identifier.fromString("test").getOrElse(???)
//   def identifier(id: String) = Identifier.fromString(id).getOrElse(???)

//   test("basic Statement with all Entities") {
//     val statement = Statement(
//       identifier("e1"),
//       identifier("a1"),
//       identifier("e2")
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
//         identifier("e1"),
//         identifier("a1"),
//         identifier("e2")
//       ),
//       Statement(
//         identifier("e2"),
//         identifier("a2"),
//         LigatureValue.StringValue("string literal")
//       ),
//       Statement(
//         identifier("e2"),
//         identifier("a3"),
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
//       Statement(identifier("a"), identifier("b"), identifier("c")),
//       Statement(identifier("a"), identifier("b"), LigatureValue.IntegerValue(123)),
//       Statement(identifier("a"), identifier("b"), LigatureValue.StringValue("Test")),
//     )
//     val resStatements = read(statements)
//     resStatements match {
//       case Right(res) => assertEquals(res.toSet, expectedStatements)
//       case Left(err)  => fail("failed", clues(err))
//     }
//   }

//   test("parse identifiers") {
//     val test = "<test>"
//     val identifier = parseIdentifier(Gaze.from(test), Map(), None)
//     assertEquals(identifier, Right(testIdentifier))
//   }

//   test("parse complex identifier") {
//     val identifierS = "<http$://&&this@2]34.[42;342?#--__>"
//     val identifierRes = parseIdentifier(Gaze.from(identifierS), Map(), None)
//     assertEquals(
//       identifierRes,
//       Right(identifier("http$://&&this@2]34.[42;342?#--__"))
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

//   test("write identifiers") {
//     val res = writeIdentifier(testIdentifier)
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
