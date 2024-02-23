/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.bend.Token
import dev.ligature.bend.modules.std

class WanderSuiteCommonMode extends munit.FunSuite {
  def check(script: String, expected: WanderValue, environment: Environment = std()) =
    assertEquals(
      run(script, environment) match
        case Left(value)                 => throw RuntimeException(value.toString())
        case Right((value, environment)) => value
      ,
      expected
    )
}
