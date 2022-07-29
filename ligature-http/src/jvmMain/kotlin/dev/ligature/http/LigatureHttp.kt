/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import dev.ligature.Dataset
import dev.ligature.Identifier
import dev.ligature.Ligature
import dev.ligature.LigatureError
import dev.ligature.Statement
import dev.ligature.lig.LigError
import dev.ligature.lig.read
import dev.ligature.lig.write
import dev.ligature.wander.run
import java.lang.AutoCloseable

import io.vertx.ext.web.RoutingContext
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject

enum class AuthMode {
  None
}

interface Server: AutoCloseable

fun runLigatureServer(ligature: Ligature, authMode: AuthMode, port: Int): Server {
  val instance = LigatureHttp(
    ligature, authMode, port
  )
  instance.startLocal()
  return instance
}

internal class LigatureHttp(val ligature: Ligature, val mode: AuthMode, val port: Int): Server {
  private val vertx = Vertx.vertx()

  internal fun startLocal() = {
    val server: HttpServer = vertx.createHttpServer()
    val router: Router = Router.router(vertx)

//    case GET -> Root / "datasets"                => getDatasets()
    router
      .get("/datasets")
      .respond(::getDatasets)
//      .respond { ctx -> Future.succeededFuture(JsonObject().put("hello", "world")) }

//    case POST -> Root / "datasets" / datasetName => addDataset(datasetName)


//    case DELETE -> Root / "datasets" / datasetName =>
//    deleteDataset(datasetName)

//    case GET -> Root / "datasets" / datasetName / "statements" =>
//    getAllStatements(datasetName)

//    case req @ POST -> Root / "datasets" / datasetName / "statements" =>
//    addStatements(datasetName, req)

//    case req @ DELETE -> Root / "datasets" / datasetName / "statements" =>
//    deleteStatements(datasetName, req)

//    case req @ POST -> Root / "datasets" / datasetName / "wander" =>
//    runWanderQuery(datasetName, req)

    server.requestHandler(router).listen(port)
  }

  private fun getDatasets(ctx: RoutingContext): Future<Unit> {
    TODO()
  }

//  private fun getDatasets(): IO[Response[IO]] =
//    for {
//      out <- ligature
//        .allDatasets()
//        .map(ds => s"\"${ds.name}\"")
//        .intersperse(",")
//        .compile
//        .string
//      res <- Ok(s"[${out}]")
//    } yield res
//
//  def addDataset(datasetName: String): IO[Response[IO]] =
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//        for {
//          _ <- ligature.createDataset(dataset)
//          res <- Ok("Dataset added.")
//        } yield res
//      case Left(error) =>
//        BadRequest(error.message)
//    }
//
//  def deleteDataset(datasetName: String): IO[Response[IO]] =
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//        for {
//          _ <- ligature.deleteDataset(dataset)
//          res <- Ok("Dataset deleted.")
//        } yield res
//      case Left(error) =>
//        BadRequest(error.message)
//    }
//
//  def getAllStatements(datasetName: String): IO[Response[IO]] =
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//        val statements: IO[String] = ligature
//          .query(dataset) { qx =>
//            qx.allStatements().compile.toList
//          }
//          .map((statements: List[Statement]) => write(statements.iterator))
//        Ok(statements)
//      case Left(error) =>
//        BadRequest(error.message)
//    }
//
//  def addStatements(
//      datasetName: String,
//      request: Request[IO]
//  ): IO[Response[IO]] =
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//        val body: IO[String] = request.bodyText.compile.string
//        body.map(read).flatMap {
//          case Right(statements) =>
//            ligature
//              .write(dataset) { tx =>
//                statements
//                  .map(statement => tx.addStatement(statement))
//                  .sequence_
//              }
//              .flatMap { _ =>
//                Ok()
//              }
//          case Left(err) => BadRequest(err.message)
//        }
//      case Left(err) =>
//        BadRequest(err.message)
//    }
//
//  def deleteStatements(
//      datasetName: String,
//      request: Request[IO]
//  ): IO[Response[IO]] =
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//        val body: IO[String] = request.bodyText.compile.string
//        body.map(read).flatMap {
//          case Right(statements) =>
//            ligature
//              .write(dataset) { tx =>
//                statements
//                  .map(statement => tx.removeStatement(statement))
//                  .sequence_
//              }
//              .flatMap { _ =>
//                Ok()
//              }
//          case Left(err) => BadRequest(err.message)
//        }
//      case Left(err) =>
//        BadRequest(err.message)
//    }
//
//  def runWanderQuery(
//      datasetName: String,
//      request: Request[IO]
//  ): IO[Response[IO]] =
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//        val body: IO[String] = request.bodyText.compile.string
//        body.map(script => run(script, dataset)).flatMap {
//          case Right(result) =>
//            Ok(result.toString)
//          case Left(err) => BadRequest(err.message)
//        }
//      case Left(err) =>
//        BadRequest(err.message)
//    }
  override fun close(): Unit {
    TODO()
  }
}
