/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.printer

import arrow.core.Either
import dev.ligature.lig.LigWriter
import dev.ligature.wander.interpreter.*

class Printer {
    private val ligWriter = LigWriter()

    /**
     * The write function accepts a primitive and writes it as a human-readable String.
     * This value is what will be presented to users after executing a script.
     */
    fun print(result: Either<WanderError, WanderValue>): String =
        when (result) {
            is Either.Right -> print(result.value)
            is Either.Left  -> print(result.value)
        }

    fun print(wanderValue: WanderValue): String =
        when (wanderValue) {
            is IntegerWanderValue -> ligWriter.writeValue(wanderValue.value)
            is AttributeWanderValue -> ligWriter.writeAttribute(wanderValue.value)
            is BooleanWanderValue -> wanderValue.value.toString()
            is EntityWanderValue -> ligWriter.writeEntity(wanderValue.value)
            is FloatWanderValue -> ligWriter.writeValue(wanderValue.value)
            is StringWanderValue -> ligWriter.writeValue(wanderValue.value)
            UnitWanderValue -> "Unit"
            is ValueWanderValue -> ligWriter.writeValue(wanderValue.value)
            is WanderFunction -> TODO()
            is StatementWanderValue -> ligWriter.writeStatement(wanderValue.value)
        }

    fun print(error: WanderError): String =
        when (error) {
            is ParserError -> "Parsing Error at ${error.position}: ${error.message}"
            is NotSupported -> error.message
            is SymbolExits -> TODO()
            is UnknownSymbol -> TODO()
        }
}
