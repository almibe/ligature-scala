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
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class Server(private val port: Int = 4444, private val ligature: Ligature) {
    private val server: HttpServer
    private val gson = Gson()

    init {
        val vertx = Vertx.vertx()
        server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.post().handler(BodyHandler.create()).handler { rc ->
            val body = rc.bodyAsString
            if (body == null) { // create new dataset
                GlobalScope.launch(vertx.dispatcher()) {
                    ligature.createDataset(Dataset(rc.normalizedPath().removePrefix("/")))
                    rc.response().send()
                }
            } else { // add statement to dataset
                val statementJson = JsonParser.parseString(body).asJsonObject

                val entityJson = statementJson.get("entity")
                val entity = if (entityJson.isJsonNull) {
                    null
                } else {
                    Entity(entityJson.asString.toLong())
                }

                val attribute = Attribute(statementJson.get("attribute").asString)

                val valueJson = statementJson.get("value")
                val valueJsonType = statementJson.get("value-type").asString
                val value: Value? = when {
                    valueJsonType == "Entity" -> {
                        if (entityJson.isJsonNull) {
                            null
                        } else {
                            Entity(entityJson.asString.toLong())
                        }
                    }
                    else -> TODO()
                }

                GlobalScope.launch(vertx.dispatcher()) {
                    val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
                    ligature.write(dataset) { tx ->
                        val statement = Statement(entity ?: tx.newEntity().getOrThrow(),
                            attribute,
                            value ?: tx.newEntity().getOrThrow())
                        tx.addStatement(statement)
                    }
                    rc.response().send()
                }
            }
        }
        router.delete().handler(BodyHandler.create()).handler { rc ->
            val body = rc.bodyAsString
            if (body == null) {
                GlobalScope.launch(vertx.dispatcher()) {
                    ligature.deleteDataset(Dataset(rc.normalizedPath().removePrefix("/")))
                    rc.response().send()
                }
            } else {
                //TODO remove statement
//        val dataset = TODO()
//        val statement = TODO()
//        ligature.write { tx ->
//            tx.removeStatement(statement)
//        }
            }
        }
        router.get().handler { rc ->
            val path = rc.normalizedPath()

            if (path == "/") { //handle Datasets
                val prefix = rc.queryParam("prefix")
                val rangeStart = rc.queryParam("start")
                val rangeEnd = rc.queryParam("end")
                if (prefix.size == 1 && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
                    GlobalScope.launch(vertx.dispatcher()) {
                        val res = ligature.matchDatasetsPrefix(prefix.first()).map { it.getOrThrow().name }
                        rc.response().send(gson.toJson(res.toList()))
                    }
                } else if (prefix.isEmpty() && rangeStart.size == 1 && rangeEnd.size == 1) {
                    GlobalScope.launch(vertx.dispatcher()) {
                        val res = ligature.matchDatasetsRange(rangeStart.first(), rangeEnd.first()).map { it.getOrThrow().name }
                        rc.response().send(gson.toJson(res.toList()))
                    }
                } else { //TODO make sure that pathParams are empty + other checks
                    GlobalScope.launch(vertx.dispatcher()) {
                        val res = ligature.allDatasets().map { it.getOrThrow().name }
                        rc.response().send(gson.toJson(res.toList()))
                    }
                }
            } else { //handle Statements within a given Dataset
                val dataset = Dataset(path.removePrefix("/"))
                val entity = rc.queryParam("entity")
                val attribute = rc.queryParam("attribute")
                val value = rc.queryParam("value")
                val valueStart = rc.queryParam("value-start")
                val valueEnd = rc.queryParam("value-end")
                if (entity.isEmpty() && attribute.isEmpty() && value.isEmpty() && valueStart.isEmpty() && valueEnd.isEmpty()) { //get all
                    GlobalScope.launch(vertx.dispatcher()) {
                        val res = JsonArray()
                        ligature.query(dataset) { tx ->
                            tx.allStatements().toList().forEach { statement ->
                                res.add(serializeStatement(statement.getOrThrow()))
                            }
                        }
                        rc.response().send(res.toString())
                    }
                } else { //TODO handle simple match and range match
                    TODO()
                }
            }
        }
        //TODO everything below should be merged into the above route
        router.getWithRegex("todo").handler { rc ->
            //TODO query statements
            //TODO figure out if it's a range or prefix search
//        val prefix = TODO()
//        val rangeStart = TODO()
//        val rangeEnd = TODO()
//        if (prefix != null && rangeStart == null && rangeEnd == null) {
//            TODO()
//        } else if (prefix == null && rangeStart != null && rangeEnd != null) {
//            TODO()
//        } else {
//            TODO()
//        }
        }

        server.requestHandler(router).listen(port)
    }

    fun shutDown() {
        server.close().run {  }
    }

    fun serializeStatement(statement: PersistedStatement): JsonObject {
        val out = JsonObject()
        out.addProperty("entity", statement.statement.entity.id.toString())
        out.addProperty("attribute", statement.statement.attribute.name)
        out.addProperty("value", serializeValue(statement.statement.value))
        out.addProperty("value-type", serializeValueType(statement.statement.value))
        out.addProperty("context", statement.context.id.toString())
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
}
