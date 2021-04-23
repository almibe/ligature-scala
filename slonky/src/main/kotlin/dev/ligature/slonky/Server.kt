/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

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
import java.lang.RuntimeException

class Server(private val port: Int = 4444, private val ligature: Ligature) {
    private val server: HttpServer
    private val datasetRequests = DatasetRequests(ligature)
    private val statementRequests = StatementRequests(ligature)

    init {
        val vertx = Vertx.vertx()
        server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.post().handler(BodyHandler.create()).handler { rc ->
            val body = rc.bodyAsString
            if (body == null) { // create new dataset
                GlobalScope.launch(vertx.dispatcher()) { datasetRequests.createDataset(rc) }
            } else { // add statement to dataset
                GlobalScope.launch(vertx.dispatcher()) { statementRequests.addStatements(rc) }
            }
        }
        router.delete().handler(BodyHandler.create()).handler { rc ->
            val body = rc.bodyAsString
            if (body == null) {
                GlobalScope.launch(vertx.dispatcher()) { datasetRequests.deleteDataset(rc) }
            } else {
                GlobalScope.launch(vertx.dispatcher()) { statementRequests.removeStatements(rc) }
            }
        }
        router.get().handler { rc ->
            val path = rc.normalizedPath()

            if (path == "/") { //handle Datasets
                GlobalScope.launch(vertx.dispatcher()) { datasetRequests.queryDatasets(rc) }
            } else { //handle Statements within a given Dataset
                GlobalScope.launch(vertx.dispatcher()) { statementRequests.queryStatements(rc) }
            }
        }

        server.requestHandler(router).listen(port)
    }

    fun shutDown() {
        server.close().run {  }
    }
}
