/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import dev.ligature.Ligature

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

enum class AuthMode {
  None
}

//fun runLigatureServer(ligature: Ligature, authMode: AuthMode, port: Int): Server {
//  val instance = LigatureHttp(
//    ligature, authMode, port
//  )
//  instance.startLocal()
//  return instance
//}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.routes(ligature: Ligature) {
  val handlers = Handlers(ligature)
  routing {
    get("/") {
      call.respondText("Hello, world!")
    }

//    case GET -> Root / "datasets"                => getDatasets()
    get("/datasets") {
      handlers.getDatasets(call)
    }

//    case POST -> Root / "datasets" / datasetName => addDataset(datasetName)
    post("/datasets/{datasetName}") {
      handlers.addDataset(call, call.parameters["datasetName"]!!)
    }

//    case DELETE -> Root / "datasets" / datasetName =>
//    deleteDataset(datasetName)
    delete("/datasets/{datasetName}") {
      handlers.deleteDataset(call, call.parameters["datasetName"]!!)
    }

//    case GET -> Root / "datasets" / datasetName / "statements" =>
//    getAllStatements(datasetName)
    get("/datasets/{datasetName}/statements") {
      handlers.getAllStatements(call, call.parameters["datasetName"]!!)
    }

//    case req @ POST -> Root / "datasets" / datasetName / "statements" =>
//    addStatements(datasetName, req)
    post("/datasets/{datasetName}/statements") {
      handlers.addStatements(call, call.parameters["datasetName"]!!)
    }

//    case req @ DELETE -> Root / "datasets" / datasetName / "statements" =>
//    deleteStatements(datasetName, req)
    delete("/datasets/{datasetName}/statements") {
      handlers.deleteStatements(call, call.parameters["datasetName"]!!)
    }

//    case req @ POST -> Root / "datasets" / datasetName / "wander" =>
//    runWanderQuery(datasetName, req)
    post("/datasets/{datasetName}/wander") {
      handlers.runWanderQuery(call, call.parameters["datasetName"]!!)
    }
  }
}

class Handlers(val ligature: Ligature) {
  suspend fun getDatasets(call: ApplicationCall) {
    call.respondText("[]") //TODO rewrite
//  for {
//    out <- ligature
//      .allDatasets()
//      .map(ds => s"\"${ds.name}\"")
//    .intersperse(",")
//      .compile
//      .string
//    res <- Ok(s"[${out}]")
//  } yield res
  }

  suspend fun addDataset(call: ApplicationCall, datasetName: String) {
      TODO()
    }
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//      for {
//        _ <- ligature.createDataset(dataset)
//        res <- Ok("Dataset added.")
//      } yield res
//      case Left(error) =>
//      BadRequest(error.message)
//    }

  suspend fun deleteDataset(call: ApplicationCall, datasetName: String) {
      TODO()
    }
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//      for {
//        _ <- ligature.deleteDataset(dataset)
//        res <- Ok("Dataset deleted.")
//      } yield res
//      case Left(error) =>
//      BadRequest(error.message)
//    }

  suspend fun getAllStatements(call: ApplicationCall, datasetName: String) {
      TODO()
    }
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//      val statements: IO[String] = ligature
//      .query(dataset) { qx =>
//      qx.allStatements().compile.toList
//    }
//      .map((statements: List[Statement]) => write(statements.iterator))
//      Ok(statements)
//      case Left(error) =>
//      BadRequest(error.message)
//    }

  suspend fun addStatements(cal: ApplicationCall, datasetName: String) {
      TODO()
    }
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//      val body: IO[String] = request.bodyText.compile.string
//      body.map(read).flatMap {
//        case Right(statements) =>
//        ligature
//          .write(dataset) { tx =>
//            statements
//              .map(statement => tx.addStatement(statement))
//            .sequence_
//          }
//          .flatMap { _ =>
//            Ok()
//          }
//        case Left(err) => BadRequest(err.message)
//      }
//      case Left(err) =>
//      BadRequest(err.message)
//    }

  suspend fun deleteStatements(call: ApplicationCall, datasetName: String) {
      TODO()
    }
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//      val body: IO[String] = request.bodyText.compile.string
//      body.map(read).flatMap {
//        case Right(statements) =>
//        ligature
//          .write(dataset) { tx =>
//            statements
//              .map(statement => tx.removeStatement(statement))
//            .sequence_
//          }
//          .flatMap { _ =>
//            Ok()
//          }
//        case Left(err) => BadRequest(err.message)
//      }
//      case Left(err) =>
//      BadRequest(err.message)
//    }

  suspend fun runWanderQuery(call: ApplicationCall, datasetName: String) {
      TODO()
    }
//    Dataset.fromString(datasetName) match {
//      case Right(dataset) =>
//      val body: IO[String] = request.bodyText.compile.string
//      body.map(script => run(script, dataset)).flatMap {
//      case Right(result) =>
//      Ok(result.toString)
//      case Left(err) => BadRequest(err.message)
//    }
//      case Left(err) =>
//      BadRequest(err.message)
//    }
}
