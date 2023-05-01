/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

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
import dev.ligature.{Dataset, Identifier, Ligature, LigatureError, Statement}
import dev.ligature.lig.{LigError, read, write}
import dev.ligature.wander.`new`.run
import dev.ligature.wander.`new`.printWanderValue

enum AuthMode:
  case None

def runLigature(ligature: Ligature, authMode: AuthMode, port: Port): IO[ExitCode] =
    val instance = LigatureHttp(
      ligature, authMode, port
    )
    instance.startLocal()

class LigatureHttp(val ligature: Ligature, val mode: AuthMode, port: Port) {
  private[http] def startLocal(): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port)
      .withHttpApp(routes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

  val routes = HttpRoutes
    .of[IO] {
      case req @ POST -> Root / "wander" =>
        runWanderQuery(req)
    }
    .orNotFound

  def getDatasets(): IO[Response[IO]] =
    for {
      out <- ligature
        .allDatasets()
        .map(ds => s"\"${ds.name}\"")
        .intersperse(",")
        .compile
        .string
      res <- Ok(s"[${out}]")
    } yield res

  def addDataset(datasetName: String): IO[Response[IO]] =
    Dataset.fromString(datasetName) match {
      case Right(dataset) =>
        for {
          _ <- ligature.createDataset(dataset)
          res <- Ok("Dataset added.")
        } yield res
      case Left(error) =>
        BadRequest(error.message)
    }

  def deleteDataset(datasetName: String): IO[Response[IO]] =
    Dataset.fromString(datasetName) match {
      case Right(dataset) =>
        for {
          _ <- ligature.deleteDataset(dataset)
          res <- Ok("Dataset deleted.")
        } yield res
      case Left(error) =>
        BadRequest(error.message)
    }

  def getAllStatements(datasetName: String): IO[Response[IO]] =
    Dataset.fromString(datasetName) match {
      case Right(dataset) =>
        val statements: IO[String] = ligature
          .query(dataset) { qx =>
            qx.allStatements().compile.toList
          }
          .map((statements: List[Statement]) => write(statements.iterator))
        Ok(statements)
      case Left(error) =>
        BadRequest(error.message)
    }

  def addStatements(
      datasetName: String,
      request: Request[IO]
  ): IO[Response[IO]] =
    Dataset.fromString(datasetName) match {
      case Right(dataset) =>
        val body: IO[String] = request.bodyText.compile.string
        body.map(read).flatMap {
          case Right(statements) =>
            ligature
              .write(dataset) { tx =>
                statements
                  .map(statement => tx.addStatement(statement))
                  .sequence_
              }
              .flatMap { _ =>
                Ok()
              }
          case Left(err) => BadRequest(err.message)
        }
      case Left(err) =>
        BadRequest(err.message)
    }

  def deleteStatements(
      datasetName: String,
      request: Request[IO]
  ): IO[Response[IO]] =
    Dataset.fromString(datasetName) match {
      case Right(dataset) =>
        val body: IO[String] = request.bodyText.compile.string
        body.map(read).flatMap {
          case Right(statements) =>
            ligature
              .write(dataset) { tx =>
                statements
                  .map(statement => tx.removeStatement(statement))
                  .sequence_
              }
              .flatMap { _ =>
                Ok()
              }
          case Left(err) => BadRequest(err.message)
        }
      case Left(err) =>
        BadRequest(err.message)
    }

  def runWanderQuery(
      request: Request[IO]
  ): IO[Response[IO]] =
    val body: IO[String] = request.bodyText.compile.string
    body.map(script => run(script)).flatMap {
      case Right(result) =>
        Ok(printWanderValue(result))
      case Left(err) => BadRequest(err.message)
    }
}
