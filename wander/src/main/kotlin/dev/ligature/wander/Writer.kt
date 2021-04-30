/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.lig.LigWriter

class Writer {
    private val ligWriter = LigWriter()

    /**
     * The write function accepts a primitive and writes it as a human-readable String.
     * This value is what will be presented to users after executing a script.
     */
    fun write(result: Either<WanderError, Primitive>): String =
        when (result) {
            is Either.Right -> writePrimitive(result.value)
            is Either.Left  -> writeError(result.value)
        }

    private fun writePrimitive(primitive: Primitive): String =
        when (primitive) {
            is IntegerPrimitive -> ligWriter.writeValue(primitive.value)
            is AttributePrimitive -> ligWriter.writeAttribute(primitive.value)
            is BooleanPrimitive -> primitive.value.toString()
            is EntityPrimitive -> ligWriter.writeEntity(primitive.value)
            is FloatPrimitive -> ligWriter.writeValue(primitive.value)
            is StringPrimitive -> ligWriter.writeValue(primitive.value)
            UnitPrimitive -> "Unit"
            is ValuePrimitive -> ligWriter.writeValue(primitive.value)
        }

    private fun writeError(error: WanderError): String =
        when (error) {
            is ParsingError -> "Parsing Error: ${error.error}"
            is SymbolError -> "Symbol Error"
            is NotSupported -> error.message
        }
}
