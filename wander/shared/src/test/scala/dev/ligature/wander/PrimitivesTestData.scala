/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, IntegerLiteral, StringLiteral}
import dev.ligature.wander.Token
import dev.ligature.wander.{BooleanValue, LigatureValue, Nothing, Script, ScriptResult}

class PrimitivesSuite extends munit.FunSuite {
  test("true boolean primitive") {
    val script = "true"
    val result = Right(ScriptResult(BooleanValue(true)))
    assertEquals(run("true", common()), result)
  }
}

val primitivesTestData = List(
  TestInstance(
    description = "false boolean primitive",
    script = "false",
    tokens = List(Token.BooleanLiteral(false)),
    ast = Script(List(BooleanValue(false))),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "true boolean primitive with trailing whitespace",
    script = "true   ",
    tokens = List(Token.BooleanLiteral(true), Token.Spaces("   ")),
    ast = Script(List(BooleanValue(true))),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "identifier",
    script = "<test>",
    tokens = List(Token.Identifier(Identifier.fromString("test").getOrElse(???))),
    ast = Script(List(LigatureValue(Identifier.fromString("test").getOrElse(???)))),
    result = Right(
      ScriptResult(LigatureValue(Identifier.fromString("test").getOrElse(???)))
    )
  ),
  TestInstance(
    description = "integer",
    script = "24601",
    tokens = List(Token.IntegerLiteral(24601)),
    ast = Script(List(LigatureValue(IntegerLiteral(24601)))),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(24601))))
  ),
  TestInstance(
    description = "negative integer",
    script = "-111",
    tokens = List(Token.IntegerLiteral(-111)),
    ast = Script(List(LigatureValue(IntegerLiteral(-111)))),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(-111))))
  ),
  TestInstance(
    description = "comment + nothing test",
    script = "#nothing   " + newLine,
    tokens = List(
      Token.Comment,
      Token.NewLine
    ),
    ast = Script(List()),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "string",
    script = "\"hello world\" ",
    tokens = List(
      Token.StringLiteral("hello world"),
      Token.Spaces(" ")
    ),
    ast = Script(List(LigatureValue(StringLiteral("hello world")))),
    result = Right(ScriptResult(LigatureValue(StringLiteral("hello world"))))
  )
)
