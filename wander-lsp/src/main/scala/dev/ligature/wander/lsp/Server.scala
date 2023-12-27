/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lsp

import io.vertx.core.Vertx

// import dev.ligature.inmemory.createLigatureInMemory
// import dev.ligature.Ligature
// import dev.ligature.wander.run
// import dev.ligature.wander.preludes.instanceLibrary
// import dev.ligature.wander.WanderValue
// import dev.ligature.wander.printWanderValue

@main
def main() =
  val vertx = Vertx.vertx()
  val server = vertx.createHttpServer()
  server.requestHandler { request =>
    val response = request.response();
    response.putHeader("content-type", "text/plain");
    response.end("Hello World!")
  }
  server.listen(8080)
