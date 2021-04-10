/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import cats.effect.{IO, IOApp, ExitCode}
import cats.effect.unsafe.implicits.global
import dev.ligature.inmemory.InMemoryLigature
import io.vertx.core.Future
import scala.jdk.FutureConverters._

object Slonky extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val ligature = InMemoryLigature() //TODO should eventually not be hardcoded
    val port = 5678

    (for {
        ligature <- ligature.instance
        server <- ServerResource().instance(ligature, port)
    } yield (ligature, server))
    .use { case (ligature, server) =>
      for {
        _ <- IO.never
        res <- IO(ExitCode.Success)
      } yield res
    }
  }
}

extension [T](future: Future[T]) {
  def asIO: IO[T] = {
    IO.fromFuture(IO(future.toCompletionStage.asScala))
  }
}
