/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import dev.ligature.IntegerLiteral
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.BooleanValue
import dev.ligature.wander.parser.LetStatement
import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.Nothing
import dev.ligature.wander.parser.Scope
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.ScriptResult

val assignmentTestData = listOf(
  TestInstance(
    description = "basic let",
    script = "let x = 5",
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "make sure keyword parser is greedy",
    script = "let trued = true",
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "let with result",
    script = "let hello = 5\nhello",
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "basic scope",
    script = """{
               |  let x = 7
               |  x
               |}""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  ),
  TestInstance(
    description = "scope shadowing",
    script = """let x = 5
               |{
               |  let x = 7
               |  x
               |}""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  )
)
