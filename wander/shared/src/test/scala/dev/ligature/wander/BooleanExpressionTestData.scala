/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.{Token, TokenType}
import dev.ligature.wander.parser.{BooleanValue, FunctionCall, Name, Script, ScriptResult}

val booleanExpression = List(
  TestInstance(
    description = "not function",
    script = "not(true)",
    tokens = List(
      Token("not", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("true", TokenType.Boolean),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        FunctionCall(Name("not"), List(BooleanValue(true)))
      )
    ),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "boolean1 test",
    script = "or(true and(false false))",
    tokens = List(
      Token("or", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("true", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("and", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("false", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("false", TokenType.Boolean),
      Token(")", TokenType.CloseParen),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        FunctionCall(
          Name("or"),
          List(
            BooleanValue(true),
            FunctionCall(
              Name("and"),
              List(BooleanValue(false), BooleanValue(false))
            )
          )
        )
      )
    ),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "boolean2 test",
    script = "and(or(true false) false)",
    tokens = List(
      Token("and", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("or", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("true", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("false", TokenType.Boolean),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("false", TokenType.Boolean),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        FunctionCall(
          Name("and"),
          List(
            FunctionCall(
              Name("or"),
              List(BooleanValue(true), BooleanValue(false))
            ),
            BooleanValue(false)
          )
        )
      )
    ),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "boolean3 test with variables",
    script = """let t = not(or(false false))
               |let f = false
               |let res = or(t and(f false))
               |res""".stripMargin,
    tokens = null,
    ast = null,
    result = Right(ScriptResult(BooleanValue(true)))
  )
)
