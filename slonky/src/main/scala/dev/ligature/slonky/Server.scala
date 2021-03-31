/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.ligature.*

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

import java.lang.RuntimeException

class Server(private val port: Int = 4444, private val ligature: LigatureInstance) {
    private val vertx = Vertx.vertx()
    private val server: HttpServer = vertx.createHttpServer()
    private val gson = Gson()

    def start() = {
        val router = Router.router(vertx)

//        router.post().handler(BodyHandler.create()).handler { rc ->
//            val body = rc.bodyAsString
//            if (body == null) { // create new dataset
//                GlobalScope.launch(vertx.dispatcher()) {
//                    ligature.createDataset(Dataset(rc.normalizedPath().removePrefix("/")))
//                    rc.response().send()
//                }
//            } else { // add statement to dataset
//                val statementJson = JsonParser.parseString(body).asJsonObject
//
//                val entityJson = statementJson.get("entity")
//                val entity = if (entityJson.isJsonNull) {
//                    null
//                } else {
//                    Entity(entityJson.asString.toLong())
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
//                            Entity(valueJson.asString.toLong())
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
//                        val statement = Statement(entity ?: tx.newEntity().getOrThrow(),
//                            attribute,
//                            value ?: tx.newEntity().getOrThrow())
//                        tx.addStatement(statement)
//                    }
//                    rc.response().send()
//                }
//            }
//        }
        router.delete().handler(BodyHandler.create()).handler { rc =>
            val body = rc.getBodyAsString()
            if (body == null) {
                GlobalScope.launch(vertx.dispatcher()) {
                    ligature.deleteDataset(Dataset(rc.normalizedPath().removePrefix("/")))
                    rc.response().send()
                }
            } else {
                val statementJson = JsonParser.parseString(body).asJsonObject

                val entity = Entity(statementJson.get("entity").asString.toLong())
                val attribute = Attribute(statementJson.get("attribute").asString)
                val valueJson = statementJson.get("value")
                val valueJsonType = statementJson.get("value-type").asString
                val value: Value = valueJsonType match {
                    case "Entity" => Entity(valueJson.asString)
                    case "StringLiteral" => StringLiteral(valueJson.asString)
                    case "IntegerLiteral" => IntegerLiteral(valueJson.asString.toLong)
                    case "FloatLiteral" => FloatLiteral(valueJson.asString.toDouble)
                    case _ => throw RuntimeException("Bad value-type $valueJsonType")
                }

                val context = Entity(statementJson.get("context").asString)

                val statement = PersistedStatement(Statement(entity, attribute, value), context)

                GlobalScope.launch(vertx.dispatcher()) {
                    val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
                    ligature.write(dataset) { tx ->
                        tx.removeStatement(statement)
                    }
                }
                rc.response().send()
            }
        }
//        router.get().handler { rc ->
//            val path = rc.normalizedPath()
//
//            if (path == "/") { //handle Datasets
//                val prefix = rc.queryParam("prefix")
//                val rangeStart = rc.queryParam("start")
//                val rangeEnd = rc.queryParam("end")
//                if (prefix.size == 1 && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
//                    GlobalScope.launch(vertx.dispatcher()) {
//                        val res = ligature.matchDatasetsPrefix(prefix.first()).map { it.getOrThrow().name }
//                        rc.response().send(gson.toJson(res.toList()))
//                    }
//                } else if (prefix.isEmpty() && rangeStart.size == 1 && rangeEnd.size == 1) {
//                    GlobalScope.launch(vertx.dispatcher()) {
//                        val res = ligature.matchDatasetsRange(rangeStart.first(), rangeEnd.first()).map { it.getOrThrow().name }
//                        rc.response().send(gson.toJson(res.toList()))
//                    }
//                } else { //TODO make sure that pathParams are empty + other checks
//                    GlobalScope.launch(vertx.dispatcher()) {
//                        val res = ligature.allDatasets().map { it.getOrThrow().name }
//                        rc.response().send(gson.toJson(res.toList()))
//                    }
//                }
//            } else { //handle Statements within a given Dataset
//                val dataset = Dataset(path.removePrefix("/"))
//                val entity = rc.queryParam("entity")
//                val attribute = rc.queryParam("attribute")
//                val value = rc.queryParam("value")
//                val valueType = rc.queryParam("value-type")
//                val valueStart = rc.queryParam("value-start")
//                val valueEnd = rc.queryParam("value-end")
//
//                val oneOrZero = { x: Int -> x == 1 || x == 0 }
//                val bothOneOrZero = { x: Int, y: Int -> oneOrZero(x) || oneOrZero(y) }
//
//                if (entity.isEmpty() && attribute.isEmpty() && value.isEmpty() && valueType.isEmpty() && valueStart.isEmpty() && valueEnd.isEmpty()) {
//                    //get all
//                    GlobalScope.launch(vertx.dispatcher()) {
//                        val res = JsonArray()
//                        ligature.query(dataset) { tx ->
//                            tx.allStatements().toList().forEach { statement ->
//                                res.add(serializeStatement(statement.getOrThrow()))
//                            }
//                        }
//                        rc.response().send(res.toString())
//                    }
//                } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && bothOneOrZero(value.size, valueType.size) && valueStart.isEmpty() && valueEnd.isEmpty()) {
//                    //handle simple match
//                    val entity: Entity? = entity.firstOrNull()?.let { Entity(it.toLong()) }
//                    val attribute: Attribute? = attribute.firstOrNull()?.let { Attribute(it) }
//                    val value: Value? = value.firstOrNull()?.let { deserializeValue(it, valueType.first()) }
//
//                    GlobalScope.launch(vertx.dispatcher()) {
//                        val res = JsonArray()
//                        ligature.query(dataset) { tx ->
//                            tx.matchStatements(entity, attribute, value).toList().forEach { statement ->
//                                res.add(serializeStatement(statement.getOrThrow()))
//                            }
//                        }
//                        rc.response().send(res.toString())
//                    }
//                } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && value.isEmpty() && valueType.size == 1 && valueStart.size == 1 && valueEnd.size == 1) {
//                    //handle range match
//                    val entity: Entity? = entity.firstOrNull()?.let { Entity(it.toLong()) }
//                    val attribute: Attribute? = attribute.firstOrNull()?.let { Attribute(it) }
//                    val valueRange: Range = deserializeValueRange(valueStart.first(), valueEnd.first(), valueType.first())
//
//                    GlobalScope.launch(vertx.dispatcher()) {
//                        val res = JsonArray()
//                        ligature.query(dataset) { tx ->
//                            tx.matchStatementsRange(entity, attribute, valueRange).toList().forEach { statement ->
//                                res.add(serializeStatement(statement.getOrThrow()))
//                            }
//                        }
//                        rc.response().send(res.toString())
//                    }
//                } else {
//                    throw RuntimeException("Illegal State for Statement lookup")
//                }
//            }
//        }

        server.requestHandler(router).listen(port)
    }

    def shutDown() = {
        server.close() //TODO make sure this completes
    }

    def serializeStatement(statement: PersistedStatement): JsonObject = {
        val out = JsonObject()
        out.addProperty("entity", statement.statement.entity.name.toString())
        out.addProperty("attribute", statement.statement.attribute.name)
        out.addProperty("value", serializeValue(statement.statement.value))
        out.addProperty("value-type", serializeValueType(statement.statement.value))
        out.addProperty("context", statement.context.name.toString())
        return out
    }

    def serializeValue(value: Value): String =
        value match {
            case Entity(name) => name
            case StringLiteral(value) => value
            case FloatLiteral(value) => value.toString()
            case IntegerLiteral(value) => value.toString()
        }

    def serializeValueType(value: Value): String  =
        value match {
            case Entity => "Entity"
            case StringLiteral => "StringLiteral"
            case FloatLiteral => "FloatLiteral"
            case IntegerLiteral => "IntegerLiteral"
        }

    def deserializeValue(value: String, valueType: String): Value =
        valueType match {
            case "Entity" => Entity(value)
            case "StringLiteral" => StringLiteral(value)
            case "FloatLiteral" => FloatLiteral(value.toDouble)
            case "IntegerLiteral" => IntegerLiteral(value.toLong)
            case _ => throw RuntimeException("Illegal value type $valueType")
        }

    def deserializeValueRange(valueStart: String, valueEnd: String, valueType: String): Range =
        valueType match {
            case "StringLiteral" => StringLiteralRange(valueStart, valueEnd)
            case "FloatLiteral" => FloatLiteralRange(valueStart.toDouble, valueEnd.toDouble)
            case "IntegerLiteral" => IntegerLiteralRange(valueStart.toLong, valueEnd.toLong)
            case _ => throw RuntimeException("Illegal value type $valueType")
        }
}
