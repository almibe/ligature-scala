/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.memory

import cats.effect.*
import com.comcast.ip4s.Port
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.http.{AuthMode, runLigature}

case class LigatureConfig(
    authMode: AuthMode = AuthMode.None,
    port: Port = Port.fromInt(4202).get
)

object MainLigatureHttp extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    val config = LigatureConfig()
    runLigature(InMemoryLigature(), config.authMode, config.port)
}

//} else {
//  IO {
//  println("Could not start application.")
//  println("A single mode argument is required.")
//  println("Supported modes:")
//  println("  --local")
//  ExitCode.Error
//}
