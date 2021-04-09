/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import cats.effect.{IO, IOApp, ExitCode}
import cats.effect.unsafe.implicits.global
import dev.ligature.inmemory.InMemoryLigature

object Slonky extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val ligature = InMemoryLigature() //TODO should eventually not be hardcoded
    val port = 5671

    for {
      res <- IO(ExitCode.Success)
    } yield res

    // ligature.instance.use { ligatureInstance => IO {
    //   ServerInstance.instance(ligatureInstance).use { server => {
        
    //     ExitCode.Success
    //   }}
    // }}
  }
}
