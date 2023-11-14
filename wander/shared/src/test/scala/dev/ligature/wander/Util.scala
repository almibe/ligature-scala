/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.Token
import dev.ligature.wander.preludes.common

class WanderSuiteCommonMode extends munit.FunSuite {
  def check(script: String, expected: Either[WanderError, WanderValue]) =
    assertEquals(run(script, common()), expected)
}

// class WanderSuiteInstancePrelude extends munit.FunSuite {
//   def check(script: String, expected: String) =
//     createInMemoryLigature().use { instance =>
//       val res = run(script, instancePrelude(instance)).map(printWanderValue)
//       assertIO(res, expected)
//     }
// }