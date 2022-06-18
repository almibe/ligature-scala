/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.memory

import cats.effect.*
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.http.runLigature

object MainLigatureHttp extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    runLigature(InMemoryLigature(), args)
}
