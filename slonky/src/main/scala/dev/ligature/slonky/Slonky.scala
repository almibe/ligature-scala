/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import dev.ligature.inmemory.InMemoryLigature
import io.vertx.core.Vertx

object Slonky extends App {
  val ligature = InMemoryLigature() //TODO should eventually not be hardcoded

  ligature.instance.use { ligatureInstance => IO {
    val vertx = Vertx.vertx()
    val port: Int = 4444

    vertx.deployVerticle(ServerVerticle(port)) //TODO should server be a resource or a verticle....it should probably a verticle?
    vertx.deployVerticle(LigatureVerticle(ligatureInstance))
  }}.unsafeRunSync()
}
