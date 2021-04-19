/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.*

class Lig {
    fun parse(input: String): Iterator<Statement> {
        TODO()
    }

    fun write(statements: Iterator<Statement>): String {
        val sb = StringBuffer()
        statements.forEach { statement ->
            sb.append("${writeEntity(statement.entity)} ${writeAttribute(statement.attribute)} " +
            "${writeValue(statement.value)} ${writeEntity(statement.context)}\n")
        }
        return sb.toString()
    }

    private fun writeEntity(entity: Entity): String {
        return "<${entity.id}>"
    }

    private fun writeAttribute(attribute: Attribute): String {
        return "@<${attribute.name}>"
    }

    private fun writeValue(value: Value): String {
        return when(value) {
            is Entity -> writeEntity(value)
            is IntegerLiteral -> value.toString()
            is FloatLiteral -> value.toString()
            is StringLiteral -> "\"$value\"" //TODO this needs to handle all characters
        }
    }

    private fun parseEntity(entity: String): Entity {
        return Entity(entity.removePrefix("<").removeSuffix(">")) //TODO needs validation
    }

    private fun parseAttribute(attribute: String): Attribute {
        return Attribute(attribute.removePrefix("@<").removeSuffix(">")) //TODO needs validation
    }

    //TODO all of the patterns below are overly simplistic
    private val entityPattern = "<[a-zA-Z0-9_]+>".toRegex()
    private val integerPattern = "\\d+".toRegex()
    private val floatPattern = "\\d+\\.\\d+".toRegex()
    private val stringPattern = "\"[a-zA-Z0-9_ \t\n]\"".toRegex()

    private fun parseValue(value: String): Value {
        return when {
            entityPattern.matches(value) -> parseEntity(value)
            integerPattern.matches(value) -> IntegerLiteral(value.toLong())
            floatPattern.matches(value) -> FloatLiteral(value.toDouble())
            stringPattern.matches(value) -> StringLiteral(value.removePrefix("\"").removeSuffix("\""))
            else -> TODO()
        }
    }
}
