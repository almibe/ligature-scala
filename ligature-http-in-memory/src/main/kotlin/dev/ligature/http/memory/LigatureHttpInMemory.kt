/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.memory

import io.ktor.server.engine.*
import io.ktor.server.netty.*

import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.http.*

fun main() {
  val ligature = InMemoryLigature()
  embeddedServer(Netty, port = 4200, host = "0.0.0.0") {
    routes(ligature)
  }.start(wait = true)
}
