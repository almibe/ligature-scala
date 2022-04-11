/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler

object Slonky {
  def main(args: Array[String]): Unit = {
    val vertx = Vertx.vertx
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    router.route().handler(ctx => {
      val response = ctx.response()
      response.putHeader("content-type", "text/plain")
      response.end("Hello World from Vert.x-Web!")
    })
    server.requestHandler(router).listen(8080)
  }
}
