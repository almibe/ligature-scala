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
        val entityQp = rc.queryParam("entity")
        val attributeQp = rc.queryParam("attribute")
        val valueQp = rc.queryParam("value")
        val valueTypeQp = rc.queryParam("value-type")
        val valueStartQp = rc.queryParam("value-start")
        val valueEndQp = rc.queryParam("value-end")
        //TODO needs context as well

        val oneOrZero = { x: Int -> x == 1 || x == 0 }
        val bothOneOrZero = { x: Int, y: Int -> (x == 0 && y == 0) || (x == 1 && y == 1) }

        if (entityQp.isEmpty() && attributeQp.isEmpty() && valueQp.isEmpty() && valueTypeQp.isEmpty() && valueStartQp.isEmpty() && valueEndQp.isEmpty()) {
            //get all
            val sb = StringBuilder()
            ligature.query(dataset) { tx ->
                tx.allStatements().toList().forEach { statement ->
                    sb.appendLine(ligWriter.writeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(sb.toString())
        } else if (oneOrZero(entityQp.size) && oneOrZero(attributeQp.size) && bothOneOrZero(valueQp.size, valueTypeQp.size) && valueStartQp.isEmpty() && valueEndQp.isEmpty()) {
            //handle simple match
            val sb = StringBuilder()
            val entity: Entity? = entityQp.firstOrNull()?.let { Entity(it) }
            val attribute: Attribute? = attributeQp.firstOrNull()?.let { Attribute(it) }
            val value: Value? = valueQp.firstOrNull()?.let { deserializeValue(it, valueTypeQp.first()) }
            //TODO needs context as well

            ligature.query(dataset) { tx ->
                tx.matchStatements(entity, attribute, value).toList().forEach { statement ->
                    sb.appendLine(ligWriter.writeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(sb.toString())
        } else if (oneOrZero(entityQp.size) && oneOrZero(attributeQp.size) && valueQp.isEmpty() && valueTypeQp.size == 1 && valueStartQp.size == 1 && valueEndQp.size == 1) {
            //handle range match
            val sb = StringBuilder()
            val entity: Entity? = entityQp.firstOrNull()?.let { Entity(it) }
            val attribute: Attribute? = attributeQp.firstOrNull()?.let { Attribute(it) }
            val valueRange: Range = deserializeValueRange(valueStartQp.first(), valueEndQp.first(), valueTypeQp.first())
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
