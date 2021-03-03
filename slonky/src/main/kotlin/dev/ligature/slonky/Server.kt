/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import dev.ligature.Ligature

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router

class Server(private val port: Int = 4444, private val ligature: Ligature) {
    private val server: HttpServer

    init {
        val vertx = Vertx.vertx()
        server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.post("/").handler { rc ->
            //TODO create dataset
//        val dataset = TODO()
//        ligature.createDataset(dataset)
        }
        router.postWithRegex("todo").handler { rc ->
            //TODO add statement
//        val statement = TODO()
//        ligature.write { tx ->
//            tx.addStatement(statement)
//        }
        }
        router.delete("/").handler { rc ->
            //TODO delete dataset
//        val dataset = TODO()
//        ligature.deleteDataset(dataset)
        }
        router.deleteWithRegex("todo").handler { rc ->
            //TODO remove statement
//        val dataset = TODO()
//        val statement = TODO()
//        ligature.write { tx ->
//            tx.removeStatement(statement)
//        }
        }
        router.get("/").handler { rc ->
            rc.response().send("")
            //TODO query datasets
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

        server.requestHandler(router).listen(port) //TODO should have a configurable port
    }

    fun shutDown() {
        server.close().run {  }
    }
}
