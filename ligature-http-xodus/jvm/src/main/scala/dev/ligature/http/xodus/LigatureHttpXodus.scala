/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.xodus

import cats.effect.*
import dev.ligature.xodus.createXodusLigature
import dev.ligature.http.runLigature
import dev.ligature.http.AuthMode
import com.comcast.ip4s.*
import java.io.File
import java.nio.file.Path

case class LigatureConfig(
  port: Port = Port.fromInt(4200).get,
  authMode: AuthMode = AuthMode.None,
  location: Option[Path] = None
)

object MainLigatureHttp extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    val config = LigatureConfig()
    val dbDirectory = config.location.getOrElse { 
      Path.of(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.ligature")
    }

    createXodusLigature(dbDirectory).use { instance =>
      runLigature(instance, config.authMode, config.port)
    }
}
