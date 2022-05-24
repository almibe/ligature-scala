/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, IntegerLiteral, StringLiteral}
import dev.ligature.wander.lexer.{Token, TokenType}
import dev.ligature.wander.parser.{FunctionCall, LetStatement, LigatureValue, Name, Parameter, Scope, Script, ScriptResult, WanderFunction}

val functionTestData = List(
  TestInstance(
    description = "function0 def",
    script = """let f = () -> { 5 }
               |f()""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("f", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer),
      Token(" ", TokenType.Spaces),
      Token("}", TokenType.CloseBrace),
      Token(newLine, TokenType.NewLine),
      Token("f", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("f"),
          WanderFunction(
            List(),
            Scope(List(LigatureValue(IntegerLiteral(5))))
          )
        ),
        FunctionCall(Name("f"), List())
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "function1 def",
    script = """let identity = (value) -> {
               |  value
               |}
               |identity(<testEntity>)""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("identity", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token("value", TokenType.Name),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("value", TokenType.Name),
      Token(newLine, TokenType.NewLine),
      Token("}", TokenType.CloseBrace),
      Token(newLine, TokenType.NewLine),
      Token("identity", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("testEntity", TokenType.Identifier),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("identity"),
          WanderFunction(
            List(Parameter(Name("value"))),
            Scope(List(Name("value")))
          )
        ),
        FunctionCall(
          Name("identity"),
          List(
            LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
          )
        )
      )
    ),
    result = Right(
      ScriptResult(
        LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
      )
    )
  ),
  TestInstance(
    description = "function2 def",
    script = """let second = (value1 value2) -> {
               |  value2
               |}
               |second(<testEntity> "hello")""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("second", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token("value1", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token(newLine, TokenType.NewLine),
      Token("}", TokenType.CloseBrace),
      Token(newLine, TokenType.NewLine),
      Token("second", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("testEntity", TokenType.Identifier),
      Token(" ", TokenType.Spaces),
      Token("hello", TokenType.String),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("second"),
          WanderFunction(
            List(Parameter(Name("value1")), Parameter(Name("value2"))),
            Scope(List(Name("value2")))
          )
        ),
        FunctionCall(
          Name("second"),
          List(
            LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
            LigatureValue(StringLiteral("hello"))
          )
        )
      )
    ),
    result = Right(
      ScriptResult(
        LigatureValue(StringLiteral("hello"))
      )
    )
  ),
  TestInstance(
    description = "function3 def",
    script = """let middle = (value1 value2 value3) -> {
               |  value2
               |}
               |middle(<testEntity> "hello" 24601)""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("middle", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token("value1", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("value3", TokenType.Name),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token(newLine, TokenType.NewLine),
      Token("}", TokenType.CloseBrace),
      Token(newLine, TokenType.NewLine),
      Token("middle", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("testEntity", TokenType.Identifier),
      Token(" ", TokenType.Spaces),
      Token("hello", TokenType.String),
      Token(" ", TokenType.Spaces),
      Token("24601", TokenType.Integer),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("middle"),
          WanderFunction(
            List(
              Parameter(Name("value1")),
              Parameter(Name("value2")),
              Parameter(Name("value3"))
            ),
            Scope(List(Name("value2")))
          )
        ),
        FunctionCall(
          Name("middle"),
          List(
            LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
            LigatureValue(StringLiteral("hello")),
            LigatureValue(IntegerLiteral(24601))
          )
        )
      )
    ),
    result = Right(
      ScriptResult(
        LigatureValue(StringLiteral("hello"))
      )
    )
  )
)
