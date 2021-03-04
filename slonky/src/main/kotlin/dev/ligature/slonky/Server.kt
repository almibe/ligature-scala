/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import dev.ligature.*

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.lang.RuntimeException

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
                //TODO needs rewrite, this is a very short term, busted solution
                val parts = body.split(" ")

                //handle entity
                val entity = if (parts[0].matches("^#\\d+$".toRegex())) {
                    Entity(parts[0].removePrefix("#").toLong())
                } else null

                //handle attribute
                val attribute = Attribute(parts[1])

                //handle value
                val valueContent = body.trim().removePrefix(parts[0]).trim().removePrefix(parts[1]).trim()
                val value: Value? = when {
                    valueContent.matches("^\\d+$".toRegex()) -> IntegerLiteral(valueContent.toLong())
                    valueContent.matches("^\\d+\\.\\d+$".toRegex()) -> FloatLiteral(valueContent.toDouble())
                    valueContent == "_" -> null //null means make new Entity
                    valueContent.matches("^#\\d+$".toRegex()) -> Entity(parts[0].removePrefix("#").toLong())
                    else -> throw RuntimeException("Invalid Value")
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

            if (path == "/") {
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
            } else {
                val dataset = Dataset(path.removePrefix("/"))
                val entity = rc.queryParam("entity")
                val attribute = rc.queryParam("attribute")
                val value = rc.queryParam("value")
                val valueStart = rc.queryParam("value-start")
                val valueEnd = rc.queryParam("value-end")
                if (entity.isEmpty() && attribute.isEmpty() && value.isEmpty() && valueStart.isEmpty() && valueEnd.isEmpty()) {
                    GlobalScope.launch(vertx.dispatcher()) {
                        val res = ligature.query(dataset) { tx ->
                            tx.allStatements().fold("") { current, next ->
                                ""//current + next.getOrThrow().name + "\n"
                            }
                        }
                        rc.response().send(res)
                    }
                } else {
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

    fun serializeStatement(statement: Statement): JsonObject {
        TODO()
    }

    fun deserializeStatement(statement: String): Statement {
        TODO()
    }
}
