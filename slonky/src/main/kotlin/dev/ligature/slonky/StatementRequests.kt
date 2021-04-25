/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import dev.ligature.*
import dev.ligature.lig.LigParser
import dev.ligature.lig.LigWriter

import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.flow.toList

class StatementRequests(private val ligature: Ligature) {
    private val ligWriter = LigWriter()
    private val ligParser = LigParser()

    suspend fun addStatements(rc: RoutingContext) {
        val body = rc.bodyAsString
        println(body)
        val statements = ligParser.parse(body)
        val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        ligature.write(dataset) { tx ->
            statements.forEach { statement ->
                tx.addStatement(statement)
            }
        }
        rc.response().send()
    }

    suspend fun removeStatements(rc: RoutingContext) {
        val body = rc.bodyAsString
        val statements = ligParser.parse(body)

        val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        ligature.write(dataset) { tx ->
            statements.forEach { statement ->
                tx.removeStatement(statement)
            }
        }
        rc.response().send()
    }

    suspend fun queryStatements(rc: RoutingContext) {
        val path = rc.normalizedPath()
        val dataset = Dataset(path.removePrefix("/"))
        val entity = rc.queryParam("entity")
        val attribute = rc.queryParam("attribute")
        val value = rc.queryParam("value")
        val valueType = rc.queryParam("value-type")
        val valueStart = rc.queryParam("value-start")
        val valueEnd = rc.queryParam("value-end")
        //TODO needs context as well

        val oneOrZero = { x: Int -> x == 1 || x == 0 }
        val bothOneOrZero = { x: Int, y: Int -> (x == 0 && y == 0) || (x == 1 && y == 1) }

        if (entity.isEmpty() && attribute.isEmpty() && value.isEmpty() && valueType.isEmpty() && valueStart.isEmpty() && valueEnd.isEmpty()) {
            //get all
            val sb = StringBuilder()
            ligature.query(dataset) { tx ->
                tx.allStatements().toList().forEach { statement ->
                    sb.appendLine(ligWriter.writeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(sb.toString())
        } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && bothOneOrZero(value.size, valueType.size) && valueStart.isEmpty() && valueEnd.isEmpty()) {
            //handle simple match
            val sb = StringBuilder()
            val entity: Entity? = entity.firstOrNull()?.let { Entity(it) }
            val attribute: Attribute? = attribute.firstOrNull()?.let { Attribute(it) }
            val value: Value? = value.firstOrNull()?.let { deserializeValue(it, valueType.first()) }
            //TODO needs context as well

            ligature.query(dataset) { tx ->
                tx.matchStatements(entity, attribute, value).toList().forEach { statement ->
                    sb.appendLine(ligWriter.writeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(sb.toString())
        } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && value.isEmpty() && valueType.size == 1 && valueStart.size == 1 && valueEnd.size == 1) {
            //handle range match
            val sb = StringBuilder()
            val entity: Entity? = entity.firstOrNull()?.let { Entity(it) }
            val attribute: Attribute? = attribute.firstOrNull()?.let { Attribute(it) }
            val valueRange: Range = deserializeValueRange(valueStart.first(), valueEnd.first(), valueType.first())
            //TODO needs context as well

            ligature.query(dataset) { tx ->
                tx.matchStatementsRange(entity, attribute, valueRange).toList().forEach { statement ->
                    sb.appendLine(ligWriter.writeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(sb.toString())
        } else {
            throw RuntimeException("Illegal State for Statement lookup")
        }
    }

    private fun deserializeValue(value: String, valueType: String): Value =  //TODO eventually remove
        when (valueType) {
            "Entity" -> Entity(value)
            "StringLiteral" -> StringLiteral(value)
            "FloatLiteral" -> FloatLiteral(value.toDouble())
            "IntegerLiteral" -> IntegerLiteral(value.toLong())
            else -> throw RuntimeException("Illegal value type $valueType")
        }

    private fun deserializeValueRange(valueStart: String, valueEnd: String, valueType: String): Range = //TODO eventually remove
        when (valueType) {
            "StringLiteral" -> StringLiteralRange(valueStart, valueEnd)
            "FloatLiteral" -> FloatLiteralRange(valueStart.toDouble(), valueEnd.toDouble())
            "IntegerLiteral" -> IntegerLiteralRange(valueStart.toLong(), valueEnd.toLong())
            else -> throw RuntimeException("Illegal value type $valueType")
        }
}
