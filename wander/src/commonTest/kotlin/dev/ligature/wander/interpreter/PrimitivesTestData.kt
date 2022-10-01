/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import arrow.core.getOrElse
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.StringLiteral
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.BooleanValue
import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.Nothing
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.ScriptResult

val primitivesTestData = listOf(
  TestInstance(
    description = "true boolean primitive",
    script = "true",
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "false boolean primitive",
    script = "false",
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "true boolean primitive with trailing whitespace",
    script = "true   ",
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "identifier",
    script = "<test>",
    result = Right(
      ScriptResult(LigatureValue(Identifier.create("test").getOrElse { throw Error("Unexpected error.") } ))
    )
  ),
  TestInstance(
    description = "integer",
    script = "24601",
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(24601))))
  ),
  TestInstance(
    description = "negative integer",
    script = "-111",
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(-111))))
  ),
  TestInstance(
    description = "comment + nothing test",
    script = "--nothing   $newLine",
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "string",
    script = "\"hello world\" ",
    result = Right(ScriptResult(LigatureValue(StringLiteral("hello world"))))
  )
)
