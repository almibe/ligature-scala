/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import dev.ligature.inmemory.InMemoryLigature
import io.vertx.core.Vertx

object Slonky extends App {
  val ligature = InMemoryLigature() //TODO should eventually not be hardcoded
  val port = 5671
  val vertx = Vertx.vertx()

//    ligature.instance.use { ligatureInstance => IO.never.as {
//    //  vertx = Vertx.vertx()
//
//      vertx.deployVerticle(ServerVerticle(port, ligatureInstance)) //TODO should server be a resource or a verticle....it should probably a verticle?
//      vertx.deployVerticle(LigatureVerticle(ligatureInstance))
//      ExitCode.Success
//    }}
}
