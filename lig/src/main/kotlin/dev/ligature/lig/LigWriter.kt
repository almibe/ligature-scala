/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.*

class LigWriter {
    fun write(statements: Iterator<Statement>): String {
        val sb = StringBuilder()
        statements.forEach { statement ->
            sb.appendLine(writeStatement(statement))
        }
        return sb.toString()
    }

    fun writeStatement(statement: Statement): String =
        StringBuilder().append(writeEntity(statement.entity))
            .append(' ')
            .append(writeAttribute(statement.attribute))
            .append(' ')
            .append(writeValue(statement.value))
            .append(' ')
            .append(writeEntity(statement.context))
            .toString()

    fun writeEntity(entity: Entity): String {
        return "<${entity.id}>"
    }

    fun writeAttribute(attribute: Attribute): String {
        return "@<${attribute.name}>"
    }

    fun writeValue(value: Value): String {
        return when(value) {
            is Entity -> writeEntity(value)
            is IntegerLiteral -> value.value.toString()
            is FloatLiteral -> value.value.toString()
            is StringLiteral -> "\"${value.value}\"" //TODO this needs to handle escaping special characters
        }
    }
}
