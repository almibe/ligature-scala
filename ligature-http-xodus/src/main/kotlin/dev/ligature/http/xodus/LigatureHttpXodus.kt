/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.xodus

import io.ktor.server.engine.*
import io.ktor.server.netty.*

import dev.ligature.xodus.XodusLigature
import dev.ligature.http.AuthMode
import dev.ligature.http.routes
import dev.ligature.http.*
import java.io.File
import java.nio.file.Files

data class LigatureConf(
  val port: Int = 4202,
  val authMode: AuthMode = AuthMode.None,
  val location: String? = null
)

fun main() {
  val directory = File("${System.getProperty("user.home")}${System.getProperty("file.separator")}.ligature")
  val ligature = XodusLigature(directory)
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    routes(ligature)
  }.start(wait = true)
}
