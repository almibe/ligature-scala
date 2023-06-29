/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO
import dev.ligature.LigatureLiteral
import dev.ligature.wander.preludes.common

class IfExpressionSuite extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)

  test("if true") {
    val script = "if true 7 else 6"
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(7))
    check(script, result)
  }
  test("if false w/ function call") {
    val script = "if and(false true) 24601 else -1"
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(24601))
  }
  test("elsif") {
    val script = """let x = true
                   |let y = false
                   |if y {
                   |    1
                   |} else if x {
                   |    2
                   |} else if false {
                   |    3
                   |} else {
                   |    4
                   |}""".stripMargin
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(2))
    check(script, result)
  }
  test("conditional w/ variables and functions") {
    val script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if not(x) {
               |    2
               |} else {
               |    3
               |}""".stripMargin
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(3))
    check(script, result)
  }
}
