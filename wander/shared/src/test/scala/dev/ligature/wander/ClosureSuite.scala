/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
// import dev.ligature.wander.{
//   FunctionCall,
//   LetStatement,
//   LigatureValue,
//   Name,
//   Parameter,
//   Scope,
//   Script,
//   ScriptResult,
//   WanderFunction
// }
import dev.ligature.wander.WanderType
import cats.effect.IO

class ClosureSuite extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)

  // test("function0 def") {
  //   val script = """let f = () -> Integer { 5 }
  //                  |f()""".stripMargin
  //   val result = Right(ScriptResult(WanderValue.Nothing))
  //   check(script, result)
  // }
//   TestInstance(
//     ast = Script(
//       List(
//         LetStatement(
//           Name("f"),
//           WanderFunction(
//             List(),
//             WanderType.Integer,
//             Scope(List(LigatureValue(IntegerLiteral(5))))
//           )
//         ),
//         FunctionCall(Name("f"), List())
//       )
//     ),
//     result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
//   ),
}

val closureTestData = List(
//   TestInstance(
//     description = "function0 def with closing over variable",
//     script = """let x = 5
//                |let f = () -> Integer { x }
//                |f()""".stripMargin,
//     tokens = null, //List(
//       // Token("let", TokenType.LetKeyword),
//       // Token(" ", TokenType.Spaces),
//       // Token("x", TokenType.Name),
//       // Token(" ", TokenType.Spaces),
//       // Token("=", TokenType.EqualSign),
//       // Token(" ", TokenType.Spaces),
//       // Token("5", TokenType.Integer),
//       // Token(newLine, TokenType.NewLine),
//       // Token("let", TokenType.LetKeyword),
//       // Token(" ", TokenType.Spaces),
//       // Token("f", TokenType.Name),
//       // Token(" ", TokenType.Spaces),
//       // Token("=", TokenType.EqualSign),
//       // Token(" ", TokenType.Spaces),
//       // Token("(", TokenType.OpenParen),
//       // Token(")", TokenType.CloseParen),
//       // Token(" ", TokenType.Spaces),
//       // Token("->", TokenType.Arrow),
//       // Token(" ", TokenType.Spaces),
//       // Token("Integer", TokenType.Name),
//       // Token(" ", TokenType.Spaces),
//       // Token("{", TokenType.OpenBrace),
//       // Token(" ", TokenType.Spaces),
//       // Token("x", TokenType.Name),
//       // Token(" ", TokenType.Spaces),
//       // Token("}", TokenType.CloseBrace),
//       // Token(newLine, TokenType.NewLine),
//       // Token("f", TokenType.Name),
//       // Token("(", TokenType.OpenParen),
//       // Token(")", TokenType.CloseParen)
// //    ),
//     ast = Script(
//       List(
//         LetStatement(
//           Name("x"),
//           LigatureValue(IntegerLiteral(5))
//         ),
//         LetStatement(
//           Name("f"),
//           WanderFunction(
//             List(),
//             WanderType.Integer,
//             Scope(List(Name("x")))
//           )
//         ),
//         FunctionCall(Name("f"), List())
//       )
//     ),
//     result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
//   ),
//   TestInstance(
//     description = "function1 def exact type",
//     script = """let identity = (identifier:Identifier) -> Identifier {
//                |  identifier
//                |}
//                |identity(<testEntity>)""".stripMargin,
//     tokens = null, //List(
//     //   Token("let", TokenType.LetKeyword),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("identity", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("=", TokenType.EqualSign),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("(", TokenType.OpenParen),
//     //   Token("identifier", TokenType.Name),
//     //   Token(":", TokenType.Colon),
//     //   Token("Identifier", TokenType.Name),
//     //   Token(")", TokenType.CloseParen),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("->", TokenType.Arrow),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("Identifier", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("{", TokenType.OpenBrace),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("  ", TokenType.Spaces),
//     //   Token("identifier", TokenType.Name),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("}", TokenType.CloseBrace),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("identity", TokenType.Name),
//     //   Token("(", TokenType.OpenParen),
//     //   Token("testEntity", TokenType.Identifier),
//     //   Token(")", TokenType.CloseParen)
//     // ),
//     ast = Script(
//       List(
//         LetStatement(
//           Name("identity"),
//           WanderFunction(
//             List(Parameter(Name("identifier"), WanderType.Identifier)),
//             WanderType.Identifier,
//             Scope(List(Name("identifier")))
//           )
//         ),
//         FunctionCall(
//           Name("identity"),
//           List(
//             LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
//           )
//         )
//       )
//     ),
//     result = Right(
//       ScriptResult(
//         LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
//       )
//     )
//   ),
//   TestInstance(
//     description = "function1 def super type",
//     script = """let identity = (value:Value) -> Value {
//              |  value
//              |}
//              |identity(<testEntity>)""".stripMargin,
//     tokens = null,
//     ast = Script(
//       List(
//         LetStatement(
//           Name("identity"),
//           WanderFunction(
//             List(Parameter(Name("value"), WanderType.Value)),
//             WanderType.Value,
//             Scope(List(Name("value")))
//           )
//         ),
//         FunctionCall(
//           Name("identity"),
//           List(
//             LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
//           )
//         )
//       )
//     ),
//     result = Right(
//       ScriptResult(
//         LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
//       )
//     )
//   ),
//   TestInstance(
//     description = "function2 def",
//     script = """let second = (value1:Value value2:Value) -> Value {
//                |  value2
//                |}
//                |second(<testEntity> "hello")""".stripMargin,
//     tokens = null, //List(
//     //   Token("let", TokenType.LetKeyword),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("second", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("=", TokenType.EqualSign),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("(", TokenType.OpenParen),
//     //   Token("value1", TokenType.Name),
//     //   Token(":", TokenType.Colon),
//     //   Token("Value", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("value2", TokenType.Name),
//     //   Token(":", TokenType.Colon),
//     //   Token("Value", TokenType.Name),
//     //   Token(")", TokenType.CloseParen),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("->", TokenType.Arrow),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("Value", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("{", TokenType.OpenBrace),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("  ", TokenType.Spaces),
//     //   Token("value2", TokenType.Name),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("}", TokenType.CloseBrace),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("second", TokenType.Name),
//     //   Token("(", TokenType.OpenParen),
//     //   Token("testEntity", TokenType.Identifier),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("hello", TokenType.String),
//     //   Token(")", TokenType.CloseParen)
//     // ),
//     ast = Script(
//       List(
//         LetStatement(
//           Name("second"),
//           WanderFunction(
//             List(
//               Parameter(Name("value1"), WanderType.Value),
//               Parameter(Name("value2"), WanderType.Value)
//             ),
//             WanderType.Value,
//             Scope(List(Name("value2")))
//           )
//         ),
//         FunctionCall(
//           Name("second"),
//           List(
//             LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
//             LigatureValue(StringLiteral("hello"))
//           )
//         )
//       )
//     ),
//     result = Right(
//       ScriptResult(
//         LigatureValue(StringLiteral("hello"))
//       )
//     )
//   ),
//   TestInstance(
//     description = "function3 def",
//     script = """let middle = (value1:Value value2:Value value3:Value) -> Value {
//                |  value2
//                |}
//                |middle(<testEntity> "hello" 24601)""".stripMargin,
//     tokens = null, //List(
//     //   Token("let", TokenType.LetKeyword),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("middle", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("=", TokenType.EqualSign),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("(", TokenType.OpenParen),
//     //   Token("value1", TokenType.Name),
//     //   Token(":", TokenType.Colon),
//     //   Token("Value", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("value2", TokenType.Name),
//     //   Token(":", TokenType.Colon),
//     //   Token("Value", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("value3", TokenType.Name),
//     //   Token(":", TokenType.Colon),
//     //   Token("Value", TokenType.Name),
//     //   Token(")", TokenType.CloseParen),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("->", TokenType.Arrow),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("Value", TokenType.Name),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("{", TokenType.OpenBrace),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("  ", TokenType.Spaces),
//     //   Token("value2", TokenType.Name),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("}", TokenType.CloseBrace),
//     //   Token(newLine, TokenType.NewLine),
//     //   Token("middle", TokenType.Name),
//     //   Token("(", TokenType.OpenParen),
//     //   Token("testEntity", TokenType.Identifier),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("hello", TokenType.String),
//     //   Token(" ", TokenType.Spaces),
//     //   Token("24601", TokenType.Integer),
//     //   Token(")", TokenType.CloseParen)
//     // ),
//     ast = Script(
//       List(
//         LetStatement(
//           Name("middle"),
//           WanderFunction(
//             List(
//               Parameter(Name("value1"), WanderType.Value),
//               Parameter(Name("value2"), WanderType.Value),
//               Parameter(Name("value3"), WanderType.Value)
//             ),
//             WanderType.Value,
//             Scope(List(Name("value2")))
//           )
//         ),
//         FunctionCall(
//           Name("middle"),
//           List(
//             LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
//             LigatureValue(StringLiteral("hello")),
//             LigatureValue(IntegerLiteral(24601))
//           )
//         )
//       )
//     ),
//     result = Right(
//       ScriptResult(
//         LigatureValue(StringLiteral("hello"))
//       )
//     )
//   )
// //  TestInstance(
// //    description = "function vararg",
// //    script = """let head = (values:Value*) -> Value {
// //               |  value2
// //               |}
// //               |middle(<testEntity> "hello" 24601)""".stripMargin,
// //    tokens = List(
// //      Token("let", TokenType.LetKeyword),
// //      Token(" ", TokenType.Spaces),
// //      Token("middle", TokenType.Name),
// //      Token(" ", TokenType.Spaces),
// //      Token("=", TokenType.EqualSign),
// //      Token(" ", TokenType.Spaces),
// //      Token("(", TokenType.OpenParen),
// //      Token("value1", TokenType.Name),
// //      Token(":", TokenType.Colon),
// //      Token("Value", TokenType.Name),
// //      Token(" ", TokenType.Spaces),
// //      Token("value2", TokenType.Name),
// //      Token(":", TokenType.Colon),
// //      Token("Value", TokenType.Name),
// //      Token(" ", TokenType.Spaces),
// //      Token("value3", TokenType.Name),
// //      Token(":", TokenType.Colon),
// //      Token("Value", TokenType.Name),
// //      Token(")", TokenType.CloseParen),
// //      Token(" ", TokenType.Spaces),
// //      Token("->", TokenType.Arrow),
// //      Token(" ", TokenType.Spaces),
// //      Token("Value", TokenType.Name),
// //      Token(" ", TokenType.Spaces),
// //      Token("{", TokenType.OpenBrace),
// //      Token(newLine, TokenType.NewLine),
// //      Token("  ", TokenType.Spaces),
// //      Token("value2", TokenType.Name),
// //      Token(newLine, TokenType.NewLine),
// //      Token("}", TokenType.CloseBrace),
// //      Token(newLine, TokenType.NewLine),
// //      Token("middle", TokenType.Name),
// //      Token("(", TokenType.OpenParen),
// //      Token("testEntity", TokenType.Identifier),
// //      Token(" ", TokenType.Spaces),
// //      Token("hello", TokenType.String),
// //      Token(" ", TokenType.Spaces),
// //      Token("24601", TokenType.Integer),
// //      Token(")", TokenType.CloseParen)
// //    ),
// //    ast = Script(
// //      List(
// //        LetStatement(
// //          Name("middle"),
// //          WanderFunction(
// //            List(
// //              Parameter(Name("value1"), WanderType.Value),
// //              Parameter(Name("value2"), WanderType.Value),
// //              Parameter(Name("value3"), WanderType.Value)
// //            ),
// //            WanderType.Value,
// //            Scope(List(Name("value2")))
// //          )
// //        ),
// //        FunctionCall(
// //          Name("middle"),
// //          List(
// //            LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
// //            LigatureValue(StringLiteral("hello")),
// //            LigatureValue(IntegerLiteral(24601))
// //          )
// //        )
// //      )
// //    ),
// //    result = Right(
// //      ScriptResult(
// //        LigatureValue(StringLiteral("hello"))
// //      )
// //    )
// //  )
)
