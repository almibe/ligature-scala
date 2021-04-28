/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either

class Wander {
    private val reader = Reader()
    private val interpreter = Interpreter()
    private val writer = Writer()

    fun runCommand(input: String): Either<WanderError, Primitive> {
        return when (val script = reader.read(input)) {
            is Either.Left  -> script
            is Either.Right -> interpreter.run(script.value)
        }
    }

    fun runCommandAndPrint(input: String): String {
        val result = runCommand(input)
        return writer.write(result)
    }

    fun runQuery(input: String): Either<WanderError, Primitive> {
        return when (val script = reader.read(input)) {
            is Either.Left  -> script
            is Either.Right -> interpreter.run(script.value)
        }
    }

    fun runQueryAndPrint(input: String): String {
        val result = runQuery(input)
        return writer.write(result)
    }
}
