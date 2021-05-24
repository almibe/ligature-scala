/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.Ligature
import dev.ligature.wander.interpreter.WanderError
import dev.ligature.wander.interpreter.Interpreter
import dev.ligature.wander.interpreter.WanderValue
import dev.ligature.wander.interpreter.Scope

class Wander(private val ligature: Ligature) {
    private val interpreter = Interpreter()

    fun runCommand(input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, createCommandScope())
    }

    fun runQuery(input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, createQueryScope())
    }

    fun run(input: String): Either<WanderError, WanderValue> {
        return interpreter.run(input, Scope(null))
    }

    private fun createCommandScope(): Scope {
        val scope = Scope(null)
        //TODO add default functions
        return scope
    }

    private fun createQueryScope(): Scope {
        val scope = Scope(null)
        //TODO add default functions
        return scope
    }
}
