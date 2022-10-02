/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.Dataset
import dev.ligature.wander.WanderError
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.ScriptResult

data class ScriptError(override val message: String): WanderError

//TODO get rid of this file?

fun interpret(
  script: Script,
  bindings: Bindings
): Either<ScriptError, ScriptResult> = script.eval(bindings)

fun interpret(
    script: Script,
    dataset: Dataset,
    bindings: Bindings
): Either<ScriptError, ScriptResult> {
  return script.eval(bindings)
}
