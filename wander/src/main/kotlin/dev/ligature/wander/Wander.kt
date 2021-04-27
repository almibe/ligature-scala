/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either

class Wander {
    private val reader = Reader()
    private val interpreter = Interpreter()
    private val writer = Writer()

    fun run(input: String): Either<WanderError, Primitive> { //TODO don't return String, should be an either
        val script = reader.read(input)
        return interpreter.run(script)
    }

    fun runAndPrint(input: String): String {
        val result = run(input)
        return writer.write(result)
    }
}
