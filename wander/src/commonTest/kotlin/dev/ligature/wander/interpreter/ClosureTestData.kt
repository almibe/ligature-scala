/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.getOrElse
import arrow.core.Either.Right
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.StringLiteral
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.FunctionCall
import dev.ligature.wander.parser.LetStatement
import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.Parameter
import dev.ligature.wander.Scope
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.WanderFunction
import dev.ligature.wander.parser.ScriptResult

val closureTestData = listOf(
  TestInstance(
    description = "function0 def",
    script = """let f = () -> Integer { 5 }
               |f()""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "function0 def with closing over variable",
    script = """let x = 5
               |let f = () -> Integer { x }
               |f()""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "function1 def exact type",
    script = """let identity = (identifier:Identifier) -> Identifier {
               |  identifier
               |}
               |identity(<testEntity>)""".trimMargin(),
    result = Right(
      ScriptResult(
        LigatureValue(Identifier.create("testEntity").getOrElse { throw Error("Unexpected value.") } )
      )
    )
  ),
  TestInstance(
    description = "function1 def super type",
    script = """let identity = (value:Value) -> Value {
             |  value
             |}
             |identity(<testEntity>)""".trimMargin(),
    result = Right(
      ScriptResult(
        LigatureValue(Identifier.create("testEntity").getOrElse { throw Error("Unexpected value.") })
      )
    )
  ),
  TestInstance(
    description = "function2 def",
    script = """let second = (value1:Value value2:Value) -> Value {
               |  value2
               |}
               |second(<testEntity> "hello")""".trimMargin(),
    result = Right(
      ScriptResult(
        LigatureValue(StringLiteral("hello"))
      )
    )
  ),
  TestInstance(
    description = "function3 def",
    script = """let middle = (value1:Value value2:Value value3:Value) -> Value {
               |  value2
               |}
               |middle(<testEntity> "hello" 24601)""".trimMargin(),
    result = Right(
      ScriptResult(
        LigatureValue(StringLiteral("hello"))
      )
    )
  ),
//  TestInstance(
//    description = "function vararg",
//    script = """let head = (values:Value*) -> Value {
//               |  value2
//               |}
//               |middle(<testEntity> "hello" 24601)""".trimMargin(),
//    tokens = List(
//      Token("let", TokenType.LetKeyword),
//      Token(" ", TokenType.Spaces),
//      Token("middle", TokenType.Name),
//      Token(" ", TokenType.Spaces),
//      Token("=", TokenType.EqualSign),
//      Token(" ", TokenType.Spaces),
//      Token("(", TokenType.OpenParen),
//      Token("value1", TokenType.Name),
//      Token(":", TokenType.Colon),
//      Token("Value", TokenType.Name),
//      Token(" ", TokenType.Spaces),
//      Token("value2", TokenType.Name),
//      Token(":", TokenType.Colon),
//      Token("Value", TokenType.Name),
//      Token(" ", TokenType.Spaces),
//      Token("value3", TokenType.Name),
//      Token(":", TokenType.Colon),
//      Token("Value", TokenType.Name),
//      Token(")", TokenType.CloseParen),
//      Token(" ", TokenType.Spaces),
//      Token("->", TokenType.Arrow),
//      Token(" ", TokenType.Spaces),
//      Token("Value", TokenType.Name),
//      Token(" ", TokenType.Spaces),
//      Token("{", TokenType.OpenBrace),
//      Token(newLine, TokenType.NewLine),
//      Token("  ", TokenType.Spaces),
//      Token("value2", TokenType.Name),
//      Token(newLine, TokenType.NewLine),
//      Token("}", TokenType.CloseBrace),
//      Token(newLine, TokenType.NewLine),
//      Token("middle", TokenType.Name),
//      Token("(", TokenType.OpenParen),
//      Token("testEntity", TokenType.Identifier),
//      Token(" ", TokenType.Spaces),
//      Token("hello", TokenType.String),
//      Token(" ", TokenType.Spaces),
//      Token("24601", TokenType.Integer),
//      Token(")", TokenType.CloseParen)
//    ),
//    ast = Script(
//      List(
//        LetStatement(
//          Name("middle"),
//          WanderFunction(
//            List(
//              Parameter(Name("value1"), WanderType.Value),
//              Parameter(Name("value2"), WanderType.Value),
//              Parameter(Name("value3"), WanderType.Value)
//            ),
//            WanderType.Value,
//            Scope(List(Name("value2")))
//          )
//        ),
//        FunctionCall(
//          Name("middle"),
//          List(
//            LigatureValue(Identifier.create("testEntity").getOrElse(???)),
//            LigatureValue(StringLiteral("hello")),
//            LigatureValue(IntegerLiteral(24601))
//          )
//        )
//      )
//    ),
//    result = Right(
//      ScriptResult(
//        LigatureValue(StringLiteral("hello"))
//      )
//    )
//  )
)