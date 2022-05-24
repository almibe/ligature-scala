/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, IntegerLiteral, StringLiteral}
import dev.ligature.wander.lexer.{Token, TokenType}
import dev.ligature.wander.parser.{BooleanValue, LigatureValue, Nothing, Script, ScriptResult}

val primitivesTestData = List(
  TestInstance(
    description = "true boolean primitive",
    script = "true",
    tokens = List(Token("true", TokenType.Boolean)),
    ast = Script(List(BooleanValue(true))),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "false boolean primitive",
    script = "false",
    tokens = List(Token("false", TokenType.Boolean)),
    ast = Script(List(BooleanValue(false))),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "true boolean primitive with trailing whitespace",
    script = "true   ",
    tokens =
      List(Token("true", TokenType.Boolean), Token("   ", TokenType.Spaces)),
    ast = Script(List(BooleanValue(true))),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "identifier",
    script = "<test>",
    tokens = List(Token("test", TokenType.Identifier)),
    ast =
      Script(List(LigatureValue(Identifier.fromString("test").getOrElse(???)))),
    result = Right(
      ScriptResult(LigatureValue(Identifier.fromString("test").getOrElse(???)))
    )
  ),
  TestInstance(
    description = "integer",
    script = "24601",
    tokens = List(Token("24601", TokenType.Integer)),
    ast = Script(List(LigatureValue(IntegerLiteral(24601)))),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(24601))))
  ),
  TestInstance(
    description = "negative integer",
    script = "-111",
    tokens = List(Token("-111", TokenType.Integer)),
    ast = Script(List(LigatureValue(IntegerLiteral(-111)))),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(-111))))
  ),
  TestInstance(
    description = "comment + nothing test",
    script = "#nothing   " + newLine,
    tokens = List(
      Token("#nothing   ", TokenType.Comment),
      Token(newLine, TokenType.NewLine)
    ),
    ast = Script(List()),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "string",
    script = "\"hello world\" ",
    tokens = List(
      Token("hello world", TokenType.String),
      Token(" ", TokenType.Spaces)
    ),
    ast = Script(List(LigatureValue(StringLiteral("hello world")))),
    result = Right(ScriptResult(LigatureValue(StringLiteral("hello world"))))
  )
)
