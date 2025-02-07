/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

//import dev.ligature.wander.run as runWander
//import dev.ligature.wander.*
import com.typesafe.scalalogging.Logger
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

private class LigatureHttp(val port: Int) extends Runnable with AutoCloseable {
  val logger = Logger("LigatureHttp")
  val vertx = Vertx.vertx()

  override def run(): Unit =
    val server = vertx.createHttpServer()
    val router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.patch("/network/:networkName").handler { ctx =>
      val networkName = ctx.pathParam("networkName")
      val request = ctx.body().asString()
      println(networkName)
      println(request)
      val response = ctx.response();
      response.putHeader("content-type", "text/plain");
      // Write to the response and end it
      val _ = response.end(request);

    }

    router.route().handler { ctx =>

      // This handler will be called for every request
      val response = ctx.response();
      response.putHeader("content-type", "text/plain");

      // Write to the response and end it
      val _ = response.end("Hello World from Vert.x-Web!");
    };

    val _ = server.requestHandler(router).listen(8080);

  override def close(): Unit =
    val _ = vertx.close()
}

def printError(message: String): String = ???
//  printLigatureValue(LigatureValue.Module(Map(Field("error") -> LigatureValue.String(message))))

def runServer(port: Int): AutoCloseable = {
  val server = LigatureHttp(port)
  server
}

@main def main =
  val server = LigatureHttp(4200)
  server.run()
