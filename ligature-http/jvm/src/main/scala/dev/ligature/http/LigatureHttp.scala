/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import cats.data.EitherT
import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.Router

import scala.concurrent.duration.*
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.{Dataset, Identifier, Ligature, LigatureError, Statement}
import dev.ligature.dlig.{DLigError, readDLig}
import dev.ligature.lig.write

object MainLigatureHttp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    if (args.length == 1 && args(0) == "--local") { // currently only supports --local mode
      val instance = LigatureHttp(InMemoryLigature()) //hard-coded InMemory version for now
      instance.startLocal()
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
}

class LigatureHttp(val ligature: Ligature) {
  private[http] def startLocal(): IO[ExitCode] = {
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
    for {
      out <- ligature
        .allDatasets()
        .map(ds => s"\"${ds.name}\"")
        .intersperse(",")
        .compile
        .string
      res <- Ok(s"[${out}]")
    } yield res
  }

  def addDataset(datasetName: String): IO[Response[IO]] = {
    Dataset.fromString(datasetName) match {
      case Right(dataset) => {
        for {
          _ <- ligature.createDataset(dataset)
          res <- Ok("Dataset added.")
        } yield res
      }
      case Left(error) => {
        BadRequest(error.message)
      }
    }
  }

  def deleteDataset(datasetName: String): IO[Response[IO]] = {
    Dataset.fromString(datasetName) match {
      case Right(dataset) => {
        for {
          _ <- ligature.deleteDataset(dataset)
          res <- Ok("Dataset deleted.")
        } yield res
      }
      case Left(error) => {
        BadRequest(error.message)
      }
    }
  }

  def getAllStatements(datasetName: String): IO[Response[IO]] = {
    Dataset.fromString(datasetName) match {
      case Right(dataset) => {
        val statements: IO[String] = ligature.query(dataset) { qx =>
          qx.allStatements().compile.toList
        }.map((statements: List[Statement]) => write(statements.iterator))
        Ok(statements)
      }
      case Left(error) => {
        BadRequest(error.message)
      }
    }
  }

  def addStatements(
      datasetName: String,
      request: Request[IO]
  ): IO[Response[IO]] = {
    Dataset.fromString(datasetName) match {
      case Right(dataset) => {
        val body: IO[String] = request.bodyText.compile.string
        body.map(readDLig).flatMap {
          case Right(statements) => {
            ligature.write(dataset) { tx =>
              statements.map(statement => tx.addStatement(statement)).sequence_
            }.flatMap { _ =>
              Ok()
            }
          }
          case Left(err) => BadRequest(err.message)
        }
      }
      case Left(err) => {
        BadRequest(err.message)
      }
    }
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
