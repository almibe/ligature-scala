/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO
import dev.ligature.wander.preludes.common

class ListSuite extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)

  test("empty list") {
    val script = "[]"
    val result = WanderValue.ListValue(Seq())
    check(script, result)
  }
  test("basic list") {
    val script = "[1 2 \"three\"]"
    val result = WanderValue.ListValue(
      Seq(
        WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(1)),
        WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(2)),
        WanderValue.LigatureValue(LigatureLiteral.StringLiteral("three"))
      )
    )
    check(script, result)
  }
}
