/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import dev.ligature.Ligature
import io.ktor.http.*

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
    get("/status") {
      call.respondText("""{"status":"ok"}""", ContentType.parse("application/json"))
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
