/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token
import dev.ligature.wander.ScriptResult
import cats.effect.IO
import dev.ligature.LigatureLiteral
import dev.ligature.inmemory.createInMemoryLigature
import dev.ligature.wander.preludes.{instancePrelude, common}

class WanderSuiteCommonMode extends munit.CatsEffectSuite {
  def check(script: String, expected: ScriptResult) =
    assertIO(run(script, common()), expected)
}

class WanderSuiteInstancePrelude extends munit.CatsEffectSuite {
  def check(script: String, expected: String) =
    createInMemoryLigature().use { instance =>
      val res = run(script, instancePrelude(instance)).map(printWanderValue)
      assertIO(res, expected)
    }
}
