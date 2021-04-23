/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.*

class LigWriter {
    fun write(statements: Iterator<Statement>): String {
        val sb = StringBuffer()
        statements.forEach { statement ->
            sb.append("${writeEntity(statement.entity)} ${writeAttribute(statement.attribute)} " +
                    "${writeValue(statement.value)} ${writeEntity(statement.context)}\n")
        }
        return sb.toString()
    }

    fun writeEntity(entity: Entity): String {
        return "<${entity.id}>"
    }

    fun writeAttribute(attribute: Attribute): String {
        return "@<${attribute.name}>"
    }

    fun writeValue(value: Value): String {
        return when(value) {
            is Entity -> writeEntity(value)
            is IntegerLiteral -> value.toString()
            is FloatLiteral -> value.toString()
            is StringLiteral -> "\"$value\"" //TODO this needs to handle all characters
        }
    }
}
