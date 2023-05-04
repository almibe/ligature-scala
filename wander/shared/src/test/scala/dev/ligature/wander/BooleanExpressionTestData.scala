/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.Token
import dev.ligature.wander.parser.{BooleanValue, FunctionCall, Name, Script, ScriptResult}

val booleanExpression = List(
  TestInstance(
    description = "not function",
    script = "not(true)",
    tokens = List(
      Token.Name("not"),
      Token.OpenParen,
      Token.BooleanLiteral(true),
      Token.CloseParen
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
      Token.Name("or"),
      Token.OpenParen,
      Token.BooleanLiteral(true),
      Token.Spaces(" "),
      Token.Name("and"),
      Token.OpenParen,
      Token.BooleanLiteral(false),
      Token.Spaces(" "),
      Token.BooleanLiteral(false),
      Token.CloseParen,
      Token.CloseParen
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
      Token.Name("and"),
      Token.OpenParen,
      Token.Name("or"),
      Token.OpenParen,
      Token.BooleanLiteral(true),
      Token.Spaces(" "),
      Token.BooleanLiteral(false),
      Token.CloseParen,
      Token.Spaces(" "),
      Token.BooleanLiteral(false),
      Token.CloseParen
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
