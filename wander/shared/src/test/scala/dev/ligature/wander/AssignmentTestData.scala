/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.IntegerLiteral
import dev.ligature.wander.lexer.{Token, TokenType}
import dev.ligature.wander.parser.{
  BooleanValue,
  LetStatement,
  LigatureValue,
  Name,
  Nothing,
  Scope,
  Script,
  ScriptResult
}

val assignmentTestData = List(
  TestInstance(
    description = "basic let",
    script = "let x = 5",
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer)
    ),
    ast = Script(List(LetStatement(Name("x"), LigatureValue(IntegerLiteral(5))))),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "make sure keyword parser is greedy",
    script = "let trued = true",
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("trued", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("true", TokenType.Boolean)
    ),
    ast = Script(List(LetStatement(Name("trued"), BooleanValue(true)))),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "let with result",
    script = """let hello = 5
               |hello""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("hello", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer),
      Token(newLine, TokenType.NewLine),
      Token("hello", TokenType.Name)
    ),
    ast = Script(
      List(
        LetStatement(Name("hello"), LigatureValue(IntegerLiteral(5))),
        Name("hello")
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "basic scope",
    script = """{
               |  let x = 7
               |  x
               |}""".stripMargin,
    tokens = List(
      Token("{", TokenType.OpenBrace),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("7", TokenType.Integer),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(newLine, TokenType.NewLine),
      Token("}", TokenType.CloseBrace)
    ),
    ast = Script(
      List(
        Scope(
          List(
            LetStatement(Name("x"), LigatureValue(IntegerLiteral(7))),
            Name("x")
          )
        )
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  ),
  TestInstance(
    description = "scope shadowing",
    script = """let x = 5
               |{
               |  let x = 7
               |  x
               |}""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer),
      Token(newLine, TokenType.NewLine),
      Token("{", TokenType.OpenBrace),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("7", TokenType.Integer),
      Token(newLine, TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(newLine, TokenType.NewLine),
      Token("}", TokenType.CloseBrace)
    ),
    ast = Script(
      List(
        LetStatement(Name("x"), LigatureValue(IntegerLiteral(5))),
        Scope(
          List(
            LetStatement(Name("x"), LigatureValue(IntegerLiteral(7))),
            Name("x")
          )
        )
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  )
)
