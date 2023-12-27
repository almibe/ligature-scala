/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.libraries.common
import dev.ligature.wander.libraries.*
import dev.ligature.wander.{WanderValue, Name, Token}

class LambdaSuite extends munit.FunSuite {
  def check(script: String, expected: WanderValue): Unit =
    assertEquals(
      run(script, common()).getOrElse(???)._1,
      expected
  )

  test("partially apply a host function") {
    val script = "Int.add 1"
    val expected = WanderValue.PartialHostFunction(Seq(WanderValue.Int(1)), intLibrary(0))
    check(script, expected)
  }
}

// import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
// import dev.ligature.wander.Token
// // import dev.ligature.wander.{
// //   FunctionCall,
// //   LetStatement,
// //   LigatureValue,
// //   Name,
// //   Parameter,
// //   Scope,
// //   Script,
// //   ScriptResult,
// //   WanderFunction
// // }
// import dev.ligature.wander.WanderType
// import cats.effect.IO
// import dev.ligature.wander.preludes.common

// class ClosureSuite extends munit.CatsEffectSuite {
//   def check(script: String, expected: ScriptResult) =
//     assertIO(run(script, common()), expected)

//   test("function0 def") {
//     val script = """let f = { -> 5 }
//                    |f()""".stripMargin
//     val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(5))
//     check(script, result)
//   }
//   test("function0 def with closing over variable") {
//     val script = """let x = 5
//                    |let f = { -> x }
//                    |f()""".stripMargin
//     val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(5))
//     check(script, result)
//   }
//   test("function1 def exact type") {
//     val script = """let identity = { i -> i }
//                    |identity(<testEntity>)""".stripMargin
//     val result = WanderValue.LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
//     check(script, result)
//   }
// }

// val closureTestData = List(
// //   TestInstance(
// //     description = "function1 def super type",
// //     script = """let identity = (value:Value) -> Value {
// //              |  value
// //              |}
// //              |identity(<testEntity>)""".stripMargin,
// //     tokens = null,
// //     ast = Script(
// //       List(
// //         LetStatement(
// //           Name("identity"),
// //           WanderFunction(
// //             List(Parameter(Name("value"), WanderType.Value)),
// //             WanderType.Value,
// //             Scope(List(Name("value")))
// //           )
// //         ),
// //         FunctionCall(
// //           Name("identity"),
// //           List(
// //             LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
// //           )
// //         )
// //       )
// //     ),
// //     result = Right(
// //       ScriptResult(
// //         LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
// //       )
// //     )
// //   ),
// //   TestInstance(
// //     description = "function2 def",
// //     script = """let second = (value1:Value value2:Value) -> Value {
// //                |  value2
// //                |}
// //                |second(<testEntity> "hello")""".stripMargin,
// //     tokens = null, //List(
// //     //   Token("let", TokenType.LetKeyword),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("second", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("=", TokenType.EqualSign),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("(", TokenType.OpenParen),
// //     //   Token("value1", TokenType.Name),
// //     //   Token(":", TokenType.Colon),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("value2", TokenType.Name),
// //     //   Token(":", TokenType.Colon),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(")", TokenType.CloseParen),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("->", TokenType.Arrow),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("{", TokenType.OpenBrace),
// //     //   Token(newLine, TokenType.NewLine),
// //     //   Token("  ", TokenType.Spaces),
// //     //   Token("value2", TokenType.Name),
// //     //   Token(newLine, TokenType.NewLine),
// //     //   Token("}", TokenType.CloseBrace),
// //     //   Token(newLine, TokenType.NewLine),
// //     //   Token("second", TokenType.Name),
// //     //   Token("(", TokenType.OpenParen),
// //     //   Token("testEntity", TokenType.Identifier),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("hello", TokenType.String),
// //     //   Token(")", TokenType.CloseParen)
// //     // ),
// //     ast = Script(
// //       List(
// //         LetStatement(
// //           Name("second"),
// //           WanderFunction(
// //             List(
// //               Parameter(Name("value1"), WanderType.Value),
// //               Parameter(Name("value2"), WanderType.Value)
// //             ),
// //             WanderType.Value,
// //             Scope(List(Name("value2")))
// //           )
// //         ),
// //         FunctionCall(
// //           Name("second"),
// //           List(
// //             LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
// //             LigatureValue(StringLiteral("hello"))
// //           )
// //         )
// //       )
// //     ),
// //     result = Right(
// //       ScriptResult(
// //         LigatureValue(StringLiteral("hello"))
// //       )
// //     )
// //   ),
// //   TestInstance(
// //     description = "function3 def",
// //     script = """let middle = (value1:Value value2:Value value3:Value) -> Value {
// //                |  value2
// //                |}
// //                |middle(<testEntity> "hello" 24601)""".stripMargin,
// //     tokens = null, //List(
// //     //   Token("let", TokenType.LetKeyword),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("middle", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("=", TokenType.EqualSign),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("(", TokenType.OpenParen),
// //     //   Token("value1", TokenType.Name),
// //     //   Token(":", TokenType.Colon),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("value2", TokenType.Name),
// //     //   Token(":", TokenType.Colon),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("value3", TokenType.Name),
// //     //   Token(":", TokenType.Colon),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(")", TokenType.CloseParen),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("->", TokenType.Arrow),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("Value", TokenType.Name),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("{", TokenType.OpenBrace),
// //     //   Token(newLine, TokenType.NewLine),
// //     //   Token("  ", TokenType.Spaces),
// //     //   Token("value2", TokenType.Name),
// //     //   Token(newLine, TokenType.NewLine),
// //     //   Token("}", TokenType.CloseBrace),
// //     //   Token(newLine, TokenType.NewLine),
// //     //   Token("middle", TokenType.Name),
// //     //   Token("(", TokenType.OpenParen),
// //     //   Token("testEntity", TokenType.Identifier),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("hello", TokenType.String),
// //     //   Token(" ", TokenType.Spaces),
// //     //   Token("24601", TokenType.Integer),
// //     //   Token(")", TokenType.CloseParen)
// //     // ),
// //     ast = Script(
// //       List(
// //         LetStatement(
// //           Name("middle"),
// //           WanderFunction(
// //             List(
// //               Parameter(Name("value1"), WanderType.Value),
// //               Parameter(Name("value2"), WanderType.Value),
// //               Parameter(Name("value3"), WanderType.Value)
// //             ),
// //             WanderType.Value,
// //             Scope(List(Name("value2")))
// //           )
// //         ),
// //         FunctionCall(
// //           Name("middle"),
// //           List(
// //             LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
// //             LigatureValue(StringLiteral("hello")),
// //             LigatureValue(IntegerLiteral(24601))
// //           )
// //         )
// //       )
// //     ),
// //     result = Right(
// //       ScriptResult(
// //         LigatureValue(StringLiteral("hello"))
// //       )
// //     )
// //   )
// // //  TestInstance(
// // //    description = "function vararg",
// // //    script = """let head = (values:Value*) -> Value {
// // //               |  value2
// // //               |}
// // //               |middle(<testEntity> "hello" 24601)""".stripMargin,
// // //    tokens = List(
// // //      Token("let", TokenType.LetKeyword),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("middle", TokenType.Name),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("=", TokenType.EqualSign),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("(", TokenType.OpenParen),
// // //      Token("value1", TokenType.Name),
// // //      Token(":", TokenType.Colon),
// // //      Token("Value", TokenType.Name),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("value2", TokenType.Name),
// // //      Token(":", TokenType.Colon),
// // //      Token("Value", TokenType.Name),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("value3", TokenType.Name),
// // //      Token(":", TokenType.Colon),
// // //      Token("Value", TokenType.Name),
// // //      Token(")", TokenType.CloseParen),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("->", TokenType.Arrow),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("Value", TokenType.Name),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("{", TokenType.OpenBrace),
// // //      Token(newLine, TokenType.NewLine),
// // //      Token("  ", TokenType.Spaces),
// // //      Token("value2", TokenType.Name),
// // //      Token(newLine, TokenType.NewLine),
// // //      Token("}", TokenType.CloseBrace),
// // //      Token(newLine, TokenType.NewLine),
// // //      Token("middle", TokenType.Name),
// // //      Token("(", TokenType.OpenParen),
// // //      Token("testEntity", TokenType.Identifier),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("hello", TokenType.String),
// // //      Token(" ", TokenType.Spaces),
// // //      Token("24601", TokenType.Integer),
// // //      Token(")", TokenType.CloseParen)
// // //    ),
// // //    ast = Script(
// // //      List(
// // //        LetStatement(
// // //          Name("middle"),
// // //          WanderFunction(
// // //            List(
// // //              Parameter(Name("value1"), WanderType.Value),
// // //              Parameter(Name("value2"), WanderType.Value),
// // //              Parameter(Name("value3"), WanderType.Value)
// // //            ),
// // //            WanderType.Value,
// // //            Scope(List(Name("value2")))
// // //          )
// // //        ),
// // //        FunctionCall(
// // //          Name("middle"),
// // //          List(
// // //            LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
// // //            LigatureValue(StringLiteral("hello")),
// // //            LigatureValue(IntegerLiteral(24601))
// // //          )
// // //        )
// // //      )
// // //    ),
// // //    result = Right(
// // //      ScriptResult(
// // //        LigatureValue(StringLiteral("hello"))
// // //      )
// // //    )
// // //  )
// )
