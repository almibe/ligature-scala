/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.Ligature
import dev.ligature.wander.error.WanderError
import dev.ligature.wander.interpreter.Interpreter
import dev.ligature.wander.interpreter.WanderValue
import dev.ligature.wander.interpreter.Scope

class Wander(ligature: Ligature) {
    private val interpreter = Interpreter(ligature)

    fun runCommand(input: String): Either<WanderError, WanderValue> {
        return interpreter.runCommand(input)
    }

    fun runQuery(input: String): Either<WanderError, WanderValue> {
        return interpreter.runQuery(input)
    }

    fun run(input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, Scope(null))
    }
}
