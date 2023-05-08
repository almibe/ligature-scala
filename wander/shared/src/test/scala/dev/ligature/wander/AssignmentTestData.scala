/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.LigatureLiteral
// import dev.ligature.wander.Token
// import dev.ligature.wander.{
//   BooleanValue,
//   LetStatement,
//   LigatureValue,
//   Name,
//   Nothing,
//   Scope,
//   Script,
//   ScriptResult
// }

val assignmentTestData = List(
  // TestInstance(
  //   description = "basic let",
  //   script = "let x = 5",
  //   tokens = List(
  //     Token.LetKeyword,
  //     Token.Spaces(" "),
  //     Token.Name("x"),
  //     Token.Spaces(" "),
  //     Token.EqualSign,
  //     Token.Spaces(" "),
  //     Token.IntegerLiteral(5)
  //   ),
  //   ast = Script(List(LetStatement(Name("x"), LigatureValue(IntegerLiteral(5))))),
  //   result = Right(ScriptResult(Nothing))
  // ),
  // TestInstance(
  //   description = "make sure keyword parser is greedy",
  //   script = "let trued = true",
  //   tokens = List(
  //     Token.LetKeyword,
  //     Token.Spaces(" "),
  //     Token.Name("trued"),
  //     Token.Spaces(" "),
  //     Token.EqualSign,
  //     Token.Spaces(" "),
  //     Token.BooleanLiteral(true)
  //   ),
  //   ast = Script(List(LetStatement(Name("trued"), BooleanValue(true)))),
  //   result = Right(ScriptResult(Nothing))
  // ),
  // TestInstance(
  //   description = "let with result",
  //   script = """let hello = 5
  //              |hello""".stripMargin,
  //   tokens = List(
  //     Token.LetKeyword,
  //     Token.Spaces(" "),
  //     Token.Name("hello"),
  //     Token.Spaces(" "),
  //     Token.EqualSign,
  //     Token.Spaces(" "),
  //     Token.IntegerLiteral(5),
  //     Token.NewLine,
  //     Token.Name("hello")
  //   ),
  //   ast = Script(
  //     List(
  //       LetStatement(Name("hello"), LigatureValue(IntegerLiteral(5))),
  //       Name("hello")
  //     )
  //   ),
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  // ),
  // TestInstance(
  //   description = "basic scope",
  //   script = """{
  //              |  let x = 7
  //              |  x
  //              |}""".stripMargin,
  //   tokens = List(
  //     Token.OpenBrace,
  //     Token.NewLine,
  //     Token.Spaces("  "),
  //     Token.LetKeyword,
  //     Token.Spaces(" "),
  //     Token.Name("x"),
  //     Token.Spaces(" "),
  //     Token.EqualSign,
  //     Token.Spaces(" "),
  //     Token.IntegerLiteral(7),
  //     Token.NewLine,
  //     Token.Spaces("  "),
  //     Token.Name("x"),
  //     Token.NewLine,
  //     Token.CloseBrace
  //   ),
  //   ast = Script(
  //     List(
  //       Scope(
  //         List(
  //           LetStatement(Name("x"), LigatureValue(IntegerLiteral(7))),
  //           Name("x")
  //         )
  //       )
  //     )
  //   ),
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  // ),
  // TestInstance(
  //   description = "scope shadowing",
  //   script = """let x = 5
  //              |{
  //              |  let x = 7
  //              |  x
  //              |}""".stripMargin,
  //   tokens = List(
  //     Token.LetKeyword,
  //     Token.Spaces(" "),
  //     Token.Name("x"),
  //     Token.Spaces(" "),
  //     Token.EqualSign,
  //     Token.Spaces(" "),
  //     Token.IntegerLiteral(5),
  //     Token.NewLine,
  //     Token.OpenBrace,
  //     Token.NewLine,
  //     Token.Spaces("  "),
  //     Token.LetKeyword,
  //     Token.Spaces(" "),
  //     Token.Name("x"),
  //     Token.Spaces(" "),
  //     Token.EqualSign,
  //     Token.Spaces(" "),
  //     Token.IntegerLiteral(7),
  //     Token.NewLine,
  //     Token.Spaces("  "),
  //     Token.Name("x"),
  //     Token.NewLine,
  //     Token.CloseBrace
  //   ),
  //   ast = Script(
  //     List(
  //       LetStatement(Name("x"), LigatureValue(IntegerLiteral(5))),
  //       Scope(
  //         List(
  //           LetStatement(Name("x"), LigatureValue(IntegerLiteral(7))),
  //           Name("x")
  //         )
  //       )
  //     )
  //   ),
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  // )
)
