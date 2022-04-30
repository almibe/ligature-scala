/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import cats.effect._, org.http4s._, org.http4s.dsl.io._
import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import scala.concurrent.duration._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.Ligature

object LigatureHttp extends IOApp {
  // NOTE: for now while developing I'm just using a hard coded in-memory instance of Ligature.
  // Eventually I'll probably flip this dependency and each implementation of Ligature will call into Ligature-HTTP.
  val ligature = InMemoryLigature()

  def run(args: List[String]): IO[ExitCode] = {
    if (args.length == 1 && args(0) == "--local") { // currently only supports --local mode
      startLocal()
    } else {
      IO {
        println("Could not start application.")
        println("A single mode argument is required.")
        println("Supported modes:")
        println("  --local")
        ExitCode.Error
      }
    }
  }

  private def startLocal(): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

  val routes = HttpRoutes
    .of[IO] {
//    case req @ GET -> Root / "hello" / name => {
//      for {
//        body <- req.bodyText.compile.string
//        res <- Ok(body.toUpperCase)
//      } yield res
//    }
      case GET -> Root / "datasets"                => getDatasets()
      case POST -> Root / "datasets" / datasetName => addDataset(datasetName)
      case DELETE -> Root / "datasets" / datasetName =>
        deleteDataset(datasetName)
      case GET -> Root / "datasets" / datasetName / "statements" =>
        getAllStatements(datasetName)
      case req @ POST -> Root / "datasets" / datasetName / "statements" =>
        addStatements(datasetName, req)
      case req @ DELETE -> Root / "datasets" / datasetName / "statements" =>
        deleteStatements(datasetName, req)
      case req @ POST -> Root / "datasets" / datasetName / "wander" =>
        runWanderQuery(datasetName, req)
    }
    .orNotFound

  def getDatasets(): IO[Response[IO]] = {
    //val datasetsStream = ligature.allDatasets()
    Ok("[]")
  }

  def addDataset(datasetName: String): IO[Response[IO]] = {
    Dataset.fromString(datasetName) match {
      Right (dataset) => {

      }
      Left (error) => {

      }
    }
    ???
  }

  def deleteDataset(datasetName: String): IO[Response[IO]] = {
    ???
  }

  def getAllStatements(datasetName: String): IO[Response[IO]] = {
    ???
  }

  def addStatements(
      datasetName: String,
      request: Request[IO]
  ): IO[Response[IO]] = {
    ???
  }

  def deleteStatements(
      datasetName: String,
      request: Request[IO]
  ): IO[Response[IO]] = {
    ???
  }

  def runWanderQuery(
      datasetName: String,
      request: Request[IO]
  ): IO[Response[IO]] = {
    ???
  }
}
