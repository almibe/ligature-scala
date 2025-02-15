/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import cats.effect._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
//import dev.ligature.wander.run as runWander
//import dev.ligature.wander.*
//import com.typesafe.scalalogging.Logger

object Main extends IOApp {

  val wanderService = HttpRoutes
    .of[IO] { case POST -> Root =>
      Ok(s"Hello")
    }
    .orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(wanderService)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
