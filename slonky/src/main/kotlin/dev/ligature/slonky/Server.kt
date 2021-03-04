/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import dev.ligature.Dataset
import dev.ligature.Ligature

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch

class Server(private val port: Int = 4444, private val ligature: Ligature) {
    private val server: HttpServer

    init {
        val vertx = Vertx.vertx()
        server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.post().handler(BodyHandler.create()).handler { rc ->
            val body = rc.bodyAsString
            if (body == null) { // create new dataset
                GlobalScope.launch(vertx.dispatcher()) {
                    val res = ligature.createDataset(Dataset(rc.normalizedPath().removePrefix("/")))
                    rc.response().send()
                }
                //TODO add statement
                // val statement = TODO()
                // ligature.write { tx ->
                //     tx.addStatement(statement)
                // }
            } else { // add statement to dataset
                //TODO create dataset
                // val dataset = TODO()
                // ligature.createDataset(dataset)
            }
        }
        router.delete().handler { rc ->
            val body = rc.bodyAsString
            if (body == null) {
                //TODO delete dataset
//        val dataset = TODO()
//        ligature.deleteDataset(dataset)
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
            val prefix = rc.queryParam("prefix")
            val rangeStart = rc.queryParam("start")
            val rangeEnd = rc.queryParam("end")
            if (prefix.size == 1 && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
                GlobalScope.launch(vertx.dispatcher()) {
                    val res = ligature.matchDatasetsPrefix(prefix.first()).fold("") { current, next ->
                        current + next.getOrThrow().name + "\n"
                    }
                    rc.response().send(res)
                }
            } else if (prefix.isEmpty() && rangeStart.size == 1 && rangeEnd.size == 1) {
                GlobalScope.launch(vertx.dispatcher()) {
                    val res = ligature.matchDatasetsRange(rangeStart.first(), rangeEnd.first()).fold("") { current, next ->
                        current + next.getOrThrow().name + "\n"
                    }
                    rc.response().send(res)
                }
            } else { //TODO make sure that pathParams are empty + other checks
                GlobalScope.launch(vertx.dispatcher()) {
                    val res = ligature.allDatasets().fold("") { current, next ->
                        current + next.getOrThrow().name + "\n"
                    }
                    rc.response().send(res)
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
}
