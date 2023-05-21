/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO
import dev.ligature.LigatureLiteral

class IfExpressionSuite extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)

  test("if true") {
    val script = "if true 7 else 6"
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(7))
    check(script, result)
  }
}

val ifExpression = List(
  // TestInstance(
  //   description = "if true",
  //   script = """if true {
  //              |  7
  //              |}""".stripMargin,
  //   tokens = null, //List(
  //   //   Token("if", TokenType.IfKeyword),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("true", TokenType.Boolean),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("{", TokenType.OpenBrace),
  //   //   Token(newLine, TokenType.NewLine),
  //   //   Token("  ", TokenType.Spaces),
  //   //   Token("7", TokenType.Integer),
  //   //   Token(newLine, TokenType.NewLine),
  //   //   Token("}", TokenType.CloseBrace)
  //   // ),
  //   ast = Script(
  //     List(
  //       IfExpression(
  //         BooleanValue(true),
  //         Scope(List(LigatureValue(IntegerLiteral(7))))
  //       )
  //     )
  //   ),
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  // ),
  // TestInstance(
  //   description = "if false",
  //   script = "if and(false true) { 24601 }",
  //   tokens = null, //List(
  //   //   Token("if", TokenType.IfKeyword),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("and", TokenType.Name),
  //   //   Token("(", TokenType.OpenParen),
  //   //   Token("false", TokenType.Boolean),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("true", TokenType.Boolean),
  //   //   Token(")", TokenType.CloseParen),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("{", TokenType.OpenBrace),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("24601", TokenType.Integer),
  //   //   Token(" ", TokenType.Spaces),
  //   //   Token("}", TokenType.CloseBrace)
  //   // ),
  //   ast = Script(
  //     List(
  //       IfExpression(
  //         FunctionCall(
  //           Name("and"),
  //           List(BooleanValue(false), BooleanValue(true))
  //         ),
  //         Scope(List(LigatureValue(IntegerLiteral(24601))))
  //       )
  //     )
  //   ),
  //   result = Right(ScriptResult(Nothing))
  // ),
  // TestInstance(
  //   description = "if else",
  //   script = """let x = true
  //              |let y = false
  //              |if y {
  //              |    1
  //              |} else if x {
  //              |    2
  //              |} else if false {
  //              |    3    
  //              |} else {
  //              |    4
  //              |}""".stripMargin,
  //   tokens = null,
  //   ast = Script(
  //     List(
  //       LetStatement(Name("x"), BooleanValue(true)),
  //       LetStatement(Name("y"), BooleanValue(false)),
  //       IfExpression(
  //         Name("y"),
  //         Scope(List(LigatureValue(IntegerLiteral(1)))),
  //         List(
  //           ElseIf(Name("x"), Scope(List(LigatureValue(IntegerLiteral(2))))),
  //           ElseIf(
  //             BooleanValue(false),
  //             Scope(List(LigatureValue(IntegerLiteral(3))))
  //           )
  //         ),
  //         Some(Else(Scope(List(LigatureValue(IntegerLiteral(4))))))
  //       )
  //     )
  //   ),
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(2))))
  // ),
  // TestInstance(
  //   description = "else",
  //   script = """let x = true
  //              |let y = false
  //              |if y {
  //              |    1
  //              |} else if not(x) {
  //              |    2
  //              |} else {
  //              |    3
  //              |}""".stripMargin,
  //   tokens = null,
  //   ast = null,
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(3))))
  // )
)
