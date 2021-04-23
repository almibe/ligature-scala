/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import dev.ligature.*

import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class StatementRequests(val ligature: Ligature) {
    private val gson = Gson()

    suspend fun addStatements(rc: RoutingContext) {
        rc.response().setStatusCode(500).send("Error Adding Statement")
//                val statementJson = JsonParser.parseString(body).asJsonObject
//
//                val entityJson = statementJson.get("entity")
//                val entity = if (entityJson.isJsonNull) {
//                    null
//                } else {
//                    Entity(entityJson.asString)
//                }
//
//                val attribute = Attribute(statementJson.get("attribute").asString)
//
//                val valueJson = statementJson.get("value")
//                val valueJsonType = statementJson.get("value-type").asString
//                val value: Value? = when (valueJsonType) {
//                    "Entity" -> {
//                        if (valueJson.isJsonNull) {
//                            null
//                        } else {
//                            Entity(valueJson.asString)
//                        }
//                    }
//                    "StringLiteral" -> StringLiteral(valueJson.asString)
//                    "IntegerLiteral" -> IntegerLiteral(valueJson.asString.toLong())
//                    "FloatLiteral" -> FloatLiteral(valueJson.asString.toDouble())
//                    else -> throw RuntimeException("Bad value-type $valueJsonType")
//                }
//
//                GlobalScope.launch(vertx.dispatcher()) {
//                    val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
//                    ligature.write(dataset) { tx ->
//                        val statement = Statement(entity ?: tx.newAnonymousEntity().getOrThrow(),
//                            attribute,
//                            value ?: tx.newAnonymousEntity().getOrThrow())
//                        tx.addStatement(statement)
//                    }
//                    rc.response().send()
//                }
    }

    suspend fun removeStatements(rc: RoutingContext) {
        val body = rc.bodyAsString
        val statementJson = JsonParser.parseString(body).asJsonObject

        val entity = Entity(statementJson.get("entity").asString)
        val attribute = Attribute(statementJson.get("attribute").asString)
        val valueJson = statementJson.get("value")
        val valueJsonType = statementJson.get("value-type").asString
        val value: Value = when (valueJsonType) {
            "Entity" -> Entity(valueJson.asString)
            "StringLiteral" -> StringLiteral(valueJson.asString)
            "IntegerLiteral" -> IntegerLiteral(valueJson.asString.toLong())
            "FloatLiteral" -> FloatLiteral(valueJson.asString.toDouble())
            else -> throw RuntimeException("Bad value-type $valueJsonType")
        }

        val context = Entity(statementJson.get("context").asString)

        val statement = Statement(entity, attribute, value, context)

        val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        ligature.write(dataset) { tx ->
            tx.removeStatement(statement)
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

        val oneOrZero = { x: Int -> x == 1 || x == 0 }
        val bothOneOrZero = { x: Int, y: Int -> oneOrZero(x) || oneOrZero(y) }

        if (entity.isEmpty() && attribute.isEmpty() && value.isEmpty() && valueType.isEmpty() && valueStart.isEmpty() && valueEnd.isEmpty()) {
            //get all
            val res = JsonArray()
            ligature.query(dataset) { tx ->
                tx.allStatements().toList().forEach { statement ->
                    res.add(serializeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(res.toString())
        } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && bothOneOrZero(value.size, valueType.size) && valueStart.isEmpty() && valueEnd.isEmpty()) {
            //handle simple match
            val entity: Entity? = entity.firstOrNull()?.let { Entity(it) }
            val attribute: Attribute? = attribute.firstOrNull()?.let { Attribute(it) }
            val value: Value? = value.firstOrNull()?.let { deserializeValue(it, valueType.first()) }

            val res = JsonArray()
            ligature.query(dataset) { tx ->
                tx.matchStatements(entity, attribute, value).toList().forEach { statement ->
                    res.add(serializeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(res.toString())
        } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && value.isEmpty() && valueType.size == 1 && valueStart.size == 1 && valueEnd.size == 1) {
            //handle range match
            val entity: Entity? = entity.firstOrNull()?.let { Entity(it) }
            val attribute: Attribute? = attribute.firstOrNull()?.let { Attribute(it) }
            val valueRange: Range = deserializeValueRange(valueStart.first(), valueEnd.first(), valueType.first())

            val res = JsonArray()
            ligature.query(dataset) { tx ->
                tx.matchStatementsRange(entity, attribute, valueRange).toList().forEach { statement ->
                    res.add(serializeStatement(statement.getOrThrow()))
                }
            }
            rc.response().send(res.toString())
        } else {
            throw RuntimeException("Illegal State for Statement lookup")
        }
    }

    fun serializeStatement(statement: Statement): JsonObject {
        val out = JsonObject()
        out.addProperty("entity", statement.entity.id)
        out.addProperty("attribute", statement.attribute.name)
        out.addProperty("value", serializeValue(statement.value))
        out.addProperty("value-type", serializeValueType(statement.value))
        out.addProperty("context", statement.context.id)
        return out
    }

    fun serializeValue(value: Value): String =
        when (value) {
            is Entity -> value.id.toString()
            is StringLiteral -> value.value
            is FloatLiteral -> value.value.toString()
            is IntegerLiteral -> value.value.toString()
        }

    fun serializeValueType(value: Value): String  =
        when (value) {
            is Entity -> "Entity"
            is StringLiteral -> "StringLiteral"
            is FloatLiteral -> "FloatLiteral"
            is IntegerLiteral -> "IntegerLiteral"
        }

    fun deserializeValue(value: String, valueType: String): Value =
        when (valueType) {
            "Entity" -> Entity(value)
            "StringLiteral" -> StringLiteral(value)
            "FloatLiteral" -> FloatLiteral(value.toDouble())
            "IntegerLiteral" -> IntegerLiteral(value.toLong())
            else -> throw RuntimeException("Illegal value type $valueType")
        }

    fun deserializeValueRange(valueStart: String, valueEnd: String, valueType: String): Range =
        when (valueType) {
            "StringLiteral" -> StringLiteralRange(valueStart, valueEnd)
            "FloatLiteral" -> FloatLiteralRange(valueStart.toDouble(), valueEnd.toDouble())
            "IntegerLiteral" -> IntegerLiteralRange(valueStart.toLong(), valueEnd.toLong())
            else -> throw RuntimeException("Illegal value type $valueType")
        }
}
