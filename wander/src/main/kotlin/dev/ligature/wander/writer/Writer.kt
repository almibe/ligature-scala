/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.writer

import arrow.core.Either
import dev.ligature.lig.LigWriter
import dev.ligature.wander.error.*
import dev.ligature.wander.interpreter.*

class Writer {
    private val ligWriter = LigWriter()

    /**
     * The write function accepts a primitive and writes it as a human-readable String.
     * This value is what will be presented to users after executing a script.
     */
    fun write(result: Either<WanderError, Primitive>): String =
        when (result) {
            is Either.Right -> write(result.value)
            is Either.Left  -> write(result.value)
        }

    fun write(primitive: Primitive): String =
        when (primitive) {
            is IntegerPrimitive -> ligWriter.writeValue(primitive.value)
            is AttributePrimitive -> ligWriter.writeAttribute(primitive.value)
            is BooleanPrimitive -> primitive.value.toString()
            is EntityPrimitive -> ligWriter.writeEntity(primitive.value)
            is FloatPrimitive -> ligWriter.writeValue(primitive.value)
            is StringPrimitive -> ligWriter.writeValue(primitive.value)
            UnitPrimitive -> "Unit"
            is ValuePrimitive -> ligWriter.writeValue(primitive.value)
            is WanderFunction -> TODO()
            is StatementPrimitive -> ligWriter.writeStatement(primitive.value)
        }

    fun write(error: WanderError): String =
        when (error) {
            is ParserError -> "Parsing Error at ${error.position}: ${error.message}"
            is SymbolError -> "Symbol Error"
            is NotSupported -> error.message
        }
}
