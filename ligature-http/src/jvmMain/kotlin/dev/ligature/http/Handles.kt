/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.Dataset
import dev.ligature.Ligature
import dev.ligature.lig.writeStatement
import dev.ligature.wander.library.common
import dev.ligature.wander.library.datasetQueryBindings
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class Handlers(private val ligature: Ligature) {
  suspend fun getDatasets(call: ApplicationCall) {
    val result = StringBuilder()
    ligature.allDatasets().collect {
      result.append("${it.name}\n")
    }
    call.respondText(result.toString())
  }

  suspend fun addDataset(call: ApplicationCall, datasetName: String) {
    when (val dataset = Dataset.create(datasetName)) {
      is Right -> {
        ligature.createDataset(dataset.value)
        call.respond(HttpStatusCode.OK, "Dataset $datasetName Added.")
      }
      is Left -> TODO("Return invalid Dataset name error")
    }
  }

  suspend fun deleteDataset(call: ApplicationCall, datasetName: String) {
    when (val dataset = Dataset.create(datasetName)) {
      is Right -> {
        ligature.deleteDataset(dataset.value)
        call.respond(HttpStatusCode.OK, "Dataset $datasetName Removed.")
      }
      is Left -> TODO("Return invalid Dataset name error")
    }
  }

  suspend fun getAllStatements(call: ApplicationCall, datasetName: String) {
    when (val dataset = Dataset.create(datasetName)) {
      is Right -> {
        val result = StringBuilder()
        ligature.query(dataset.value) { qx ->
          qx.allStatements().collect { result.append("${writeStatement(it)}\n") }
        }
        call.respondText(result.toString())
      }
      is Left -> TODO("Return invalid Dataset name error")
    }
  }

  suspend fun addStatements(call: ApplicationCall, datasetName: String) {
    when (val dataset = Dataset.create(datasetName)) {
      is Right -> {
        val body = call.receiveText()
        when (val statements = dev.ligature.lig.read(body)) {
          is Right -> {
            ligature.write(dataset.value) { wx ->
              statements.value.forEach {
                wx.addStatement(it)
              }
            }
            call.respondText("Added ${statements.value.size} statements.")
          }
          is Left -> TODO("Report error reading Lig.")
        }
      }
      is Left -> TODO("Return invalid Dataset name error")
    }
  }

  suspend fun deleteStatements(call: ApplicationCall, datasetName: String) {
    when (val dataset = Dataset.create(datasetName)) {
      is Right -> {
        val body = call.receiveText()
        when (val statements = dev.ligature.lig.read(body)) {
          is Right -> {
            ligature.write(dataset.value) { wx ->
              statements.value.forEach {
                wx.removeStatement(it)
              }
            }
            call.respondText("Removed ${statements.value.size} statements.")
          }
          is Left -> TODO("Report error reading Lig.")
        }
      }
      is Left -> TODO("Return invalid Dataset name error")
    }
  }

  suspend fun runWanderQuery(call: ApplicationCall, datasetName: String) {
    when (val dataset = Dataset.create(datasetName)) {
      is Right -> {
        val script = call.receiveText()
        val bindings = datasetQueryBindings(dataset.value)
        when (val res = dev.ligature.wander.run(script, bindings)) {
          is Right -> call.respondText(res.value.printResult())
          is Left -> TODO("Return error message from running script")
        }
      }
      is Left -> TODO("Return invalid Dataset name error")
    }
  }
}
