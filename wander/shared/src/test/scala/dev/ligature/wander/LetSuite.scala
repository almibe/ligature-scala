/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO
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

class LetSuite extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)

  test("basic let") {
    val script = "let x = 5"
    val result = WanderValue.Nothing
    check(script, result)
  }
  test("make sure name parser is greedy") {
    val script = "let trued = true trued"
    val result = WanderValue.BooleanValue(true)
    check(script, result)
  }


}

val assignmentTestData = List(
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
