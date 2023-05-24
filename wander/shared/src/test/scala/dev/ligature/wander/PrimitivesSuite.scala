/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO

class PrimitivesSuite extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)

  test("true boolean primitive") {
    val script = "true"
    val result = WanderValue.BooleanValue(true)
    check(script, result)
  }
  test("false boolean primitive") {
    val script = "false"
    val result = WanderValue.BooleanValue(false)
    check(script, result)
  }
  test("true boolean primitive with trailing whitespace") {
    val script = "true   "
    val result = WanderValue.BooleanValue(true)
    check(script, result)
  }
  test("identifier") {
    val script = "<test>"
    val result = WanderValue.LigatureValue(Identifier.fromString("test").getOrElse(???))
    check(script, result)
  }
  test("integer") {
    val script = "24601"
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(24601))
    check(script, result)
  }
  test("negative integer") {
    val script = "-111"
    val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(-111))
    check(script, result)
  }
  test("comment + nothing test") {
    val script = "--nothing   " + System.lineSeparator()
    val result = WanderValue.Nothing
    check(script, result)
  }
  test("string primitives") {
    val script = "\"hello world\" "
    val result = WanderValue.LigatureValue(LigatureLiteral.StringLiteral("hello world"))
    check(script, result)
  }
}
