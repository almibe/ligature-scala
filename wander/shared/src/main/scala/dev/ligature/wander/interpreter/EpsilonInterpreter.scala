/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import dev.ligature.wander.*
import dev.ligature.wander.interpreter.*

class EpsilonInterpreter extends Interpreter {
  def eval(
      expressions: Seq[Expression],
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] =
    Right(WanderValue.Nothing, environment)
}
