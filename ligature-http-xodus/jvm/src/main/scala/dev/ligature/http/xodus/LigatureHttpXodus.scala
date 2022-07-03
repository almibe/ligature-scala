/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.xodus

import cats.effect.*
import dev.ligature.xodus.XodusLigature
import dev.ligature.http.runLigature
import dev.ligature.http.AuthMode
import com.comcast.ip4s.*
import java.io.File

case class LigatureConf(
  port: Port = Port.fromInt(4202).get,
  authMode: AuthMode = AuthMode.None,
  location: String = "~/ligature"
)

object MainLigatureHttp extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    val config = LigatureConf()
    runLigature(XodusLigature(File(config.location)), config.authMode, config.port)
}
