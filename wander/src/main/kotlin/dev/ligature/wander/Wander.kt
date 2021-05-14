/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.wander.error.WanderError
import dev.ligature.wander.interpreter.Interpreter
import dev.ligature.wander.interpreter.Primitive
import dev.ligature.wander.writer.Writer

class Wander {
    private val interpreter = Interpreter()
    private val writer = Writer()

    fun runCommand(input: String): Either<WanderError, Primitive> {
        return interpreter.runCommand(input)
    }

    fun runCommandAndPrint(input: String): String {
        val result = runCommand(input)
        return writer.write(result)
    }

    fun runQuery(input: String): Either<WanderError, Primitive> {
        return interpreter.runQuery(input)
    }

    fun runQueryAndPrint(input: String): String {
        val result = runQuery(input)
        return writer.write(result)
    }
}
