/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import dev.ligature.IntegerLiteral
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.BooleanValue
import dev.ligature.wander.parser.Else
import dev.ligature.wander.parser.Elsif
import dev.ligature.wander.parser.FunctionCall
import dev.ligature.wander.parser.IfExpression
import dev.ligature.wander.parser.LetStatement
import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.Nothing
import dev.ligature.wander.parser.Scope
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.ScriptResult

val ifExpression = listOf(
  TestInstance(
    description = "if true",
    script = """if true {
               |  7
               |}""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  ),
  TestInstance(
    description = "if false",
    script = "if and(false true) { 24601 }",
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "if else",
    script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if x {
               |    2
               |} else if false {
               |    3    
               |} else {
               |    4
               |}""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(2))))
  ),
  TestInstance(
    description = "else",
    script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if not(x) {
               |    2
               |} else {
               |    3
               |}""".trimMargin(),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(3))))
  )
)
