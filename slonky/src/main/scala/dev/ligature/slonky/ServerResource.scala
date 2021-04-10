/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import scala.collection.JavaConverters._

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.core.AbstractVerticle

import dev.ligature.*

import cats.effect.{IO, Resource}

import java.lang.RuntimeException

object ServerInstance {
  private def acquire(ligature: LigatureInstance, port: Int): IO[ServerResource] = IO {
    val server = new ServerResource(ligature, port)
    server.start()
    server
  }
  private val release: ServerResource => IO[Unit] = server => IO { server.stop(); () }
  def instance(ligature: LigatureInstance, port: Int): Resource[IO, ServerResource] = {
    Resource.make(acquire(ligature, port))(release)
  }
}

class ServerResource(private val instance: LigatureInstance, private val port: Int = 4444) {
  private val vertx = Vertx.vertx

  def stop() = {
    vertx.close
  }

  def start() = {
    val bus = vertx.eventBus()
    val server: HttpServer = vertx.createHttpServer()
    val gson = Gson()
    val router = Router.router(vertx)

    router.post().handler(BodyHandler.create()).handler { rc =>
      val body = rc.getBodyAsString
      if (body == null) { // create new dataset
        //TODO post to event bus to create dataset
        // GlobalScope.launch(vertx.dispatcher()) {
          // ligature.createDataset(Dataset(rc.normalizedPath().removePrefix("/")))
          // rc.response().send()
        // }
      } else { // add statement to dataset
        val statementJson = JsonParser.parseString(body).getAsJsonObject

        val entityJson = statementJson.get("entity")
        val entity = if (entityJson.isJsonNull) {
          null
        } else {
          Entity(entityJson.getAsString)
        }

        val attribute = Attribute.fromString(statementJson.get("attribute").getAsString)

        val valueJson = statementJson.get("value")
        val valueJsonType = statementJson.get("value-type").getAsString
        val value: Value = valueJsonType match {
          case "Entity" => {
            if (valueJson.isJsonNull) {
              null
            } else {
              Entity(valueJson.getAsString)
            }
          }
          case "StringLiteral" => StringLiteral(valueJson.getAsString)
          case "IntegerLiteral" => IntegerLiteral(valueJson.getAsString.toLong)
          case "FloatLiteral" => FloatLiteral(valueJson.getAsString.toDouble)
          case _ => throw RuntimeException(s"Bad value-type $valueJsonType")
        }

        //TODO post to event bus to add Statement
        // GlobalScope.launch(vertx.dispatcher()) {
        //   val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        //   ligature.write(dataset) { tx ->
        //     val statement = Statement(entity ?: tx.newEntity().getOrThrow(),
        //       attribute,
        //       value ?: tx.newEntity().getOrThrow())
        //       tx.addStatement(statement)
        //     }
        //   rc.response().send()
        // }
      }
    }
    router.delete().handler(BodyHandler.create()).handler { rc =>
      val body = rc.getBodyAsString()
      if (body == null) {
        //TODO post to event bus to remove dataset
        // GlobalScope.launch(vertx.dispatcher()) {
        //     ligature.deleteDataset(Dataset(rc.normalizedPath().removePrefix("/")))
        //     rc.response().send()
        // }
      } else {
        val statementJson = JsonParser.parseString(body).getAsJsonObject()

        val entity = Entity(statementJson.get("entity").getAsString())
        val attribute = Attribute.fromString(statementJson.get("attribute").getAsString()).get //TODO error handling
        val valueJson = statementJson.get("value")
        val valueJsonType = statementJson.get("value-type").getAsString()
        val value: Value = valueJsonType match {
          case "Entity" => Entity(valueJson.getAsString())
          case "StringLiteral" => StringLiteral(valueJson.getAsString())
          case "IntegerLiteral" => IntegerLiteral(valueJson.getAsString().toLong)
          case "FloatLiteral" => FloatLiteral(valueJson.getAsString().toDouble)
          case _ => throw RuntimeException("Bad value-type $valueJsonType")
        }

        val context = Entity(statementJson.get("context").getAsString())

        val statement = PersistedStatement(Statement(entity, attribute, value), context)

        //TODO post to event bus to remove statement
        // GlobalScope.launch(vertx.dispatcher()) {
        //     val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        //     ligature.write(dataset) { tx =>
        //         tx.removeStatement(statement)
        //     }
        // }
        rc.response().send()
      }
    }
    router.get().handler { rc =>
      val path = rc.normalizedPath()

      if (path == "/") { //handle Datasets
        val prefix = rc.queryParam("prefix")
        val rangeStart = rc.queryParam("start")
        val rangeEnd = rc.queryParam("end")
        if (prefix.size == 1 && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
          //TODO post to event bus to match dataset prefix
          // GlobalScope.launch(vertx.dispatcher()) {
          //   val res = ligature.matchDatasetsPrefix(prefix.first()).map { it.getOrThrow().name }
          //   rc.response().send(gson.toJson(res.toList()))
          // }
        } else if (prefix.isEmpty() && rangeStart.size == 1 && rangeEnd.size == 1) {
          //TODO post to event bus to match dataset range
          // GlobalScope.launch(vertx.dispatcher()) {
          //   val res = ligature.matchDatasetsRange(rangeStart.first(), rangeEnd.first()).map { it.getOrThrow().name }
          //   rc.response().send(gson.toJson(res.toList()))
          // }
        } else { //TODO make sure that pathParams are empty + other checks
          //TODO post to event bus to get all datasets
          // GlobalScope.launch(vertx.dispatcher()) {
          //   val res = ligature.allDatasets().map { it.getOrThrow().name }
          //   rc.response().send(gson.toJson(res.toList()))
          // }
        }
      } else { //handle Statements within a given Dataset
        val dataset = Dataset.fromString(path.stripPrefix("/")).get //TODO error handling
        val entity = rc.queryParam("entity").asScala.toList
        val attribute = rc.queryParam("attribute").asScala.toList
        val value = rc.queryParam("value").asScala.toList
        val valueType = rc.queryParam("value-type").asScala.toList
        val valueStart = rc.queryParam("value-start").asScala.toList
        val valueEnd = rc.queryParam("value-end").asScala.toList

        val oneOrZero = { (x: Int) => x == 1 || x == 0 }
        val bothOneOrZero = { (x: Int, y: Int) => oneOrZero(x) || oneOrZero(y) }

        if (entity.isEmpty && attribute.isEmpty && value.isEmpty && valueType.isEmpty && valueStart.isEmpty && valueEnd.isEmpty) {
          //get all
          //TODO post to event bus to get all statements
          // GlobalScope.launch(vertx.dispatcher()) {
          //   val res = JsonArray()
          //   ligature.query(dataset) { tx =>
          //     tx.allStatements().toList().forEach { statement ->
          //       res.add(serializeStatement(statement.getOrThrow()))
          //     }
          //   }
          //   rc.response().send(res.toString())
          // }
        } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && bothOneOrZero(value.size, valueType.size) && valueStart.isEmpty && valueEnd.isEmpty) {
          //handle simple match
          val entityRes: Option[Entity] = entity.headOption.map { Entity(_) }
          val attributeRes: Option[Attribute] = attribute.headOption.map { Attribute.fromString(_).get } //TODO error handling
          val valueRes: Option[Value] = value.headOption.map { deserializeValue(_, valueType.head) }

          //TODO post to event bus to match statements
          // GlobalScope.launch(vertx.dispatcher()) {
          //   val res = JsonArray()
          //   ligature.query(dataset) { tx ->
          //     tx.matchStatements(entity, attribute, value).toList().forEach { statement ->
          //       res.add(serializeStatement(statement.getOrThrow()))
          //     }
          //   }
          //   rc.response().send(res.toString())
          // }
        } else if (oneOrZero(entity.size) && oneOrZero(attribute.size) && value.isEmpty && valueType.size == 1 && valueStart.size == 1 && valueEnd.size == 1) {
          //handle range match
          val entityRes: Option[Entity] = entity.headOption.map { Entity(_) }
          val attributeRes: Option[Attribute] = attribute.headOption.map { Attribute.fromString(_).get } //TODO error handling
          val valueRangeRes: Range = deserializeValueRange(valueStart.head, valueEnd.head, valueType.head)

          //TODO post to event bus to match statements w/ ranges
          // GlobalScope.launch(vertx.dispatcher()) {
          //   val res = JsonArray()
          //   ligature.query(dataset) { tx ->
          //     tx.matchStatementsRange(entity, attribute, valueRange).toList().forEach { statement ->
          //       res.add(serializeStatement(statement.getOrThrow()))
          //     }
          //   }
          //   rc.response().send(res.toString())
          // }
        } else {
          throw RuntimeException("Illegal State for Statement lookup")
        }
      }
    }
    server.requestHandler(router).listen(port)
  }

  def serializeStatement(statement: PersistedStatement): JsonObject = {
    val out = JsonObject()
    out.addProperty("entity", statement.statement.entity.name.toString())
    out.addProperty("attribute", statement.statement.attribute.name)
    out.addProperty("value", serializeValue(statement.statement.value))
    out.addProperty("value-type", serializeValueType(statement.statement.value))
    out.addProperty("context", statement.context.name.toString())
    return out
  }

  def serializeValue(value: Value): String =
    value match {
      case Entity(name) => name
      case StringLiteral(value) => value
      case FloatLiteral(value) => value.toString()
      case IntegerLiteral(value) => value.toString()
    }

  def serializeValueType(value: Value): String  =
    value match {
      case Entity(_) => "Entity"
      case StringLiteral(_) => "StringLiteral"
      case FloatLiteral(_) => "FloatLiteral"
      case IntegerLiteral(_) => "IntegerLiteral"
    }

  def deserializeValue(value: String, valueType: String): Value =
    valueType match {
      case "Entity" => Entity(value)
      case "StringLiteral" => StringLiteral(value)
      case "FloatLiteral" => FloatLiteral(value.toDouble)
      case "IntegerLiteral" => IntegerLiteral(value.toLong)
      case _ => throw RuntimeException(s"Illegal value type $valueType")
    }

  def deserializeValueRange(valueStart: String, valueEnd: String, valueType: String): Range =
    valueType match {
      case "StringLiteral" => StringLiteralRange(valueStart, valueEnd)
      case "FloatLiteral" => FloatLiteralRange(valueStart.toDouble, valueEnd.toDouble)
      case "IntegerLiteral" => IntegerLiteralRange(valueStart.toLong, valueEnd.toLong)
      case _ => throw RuntimeException(s"Illegal value type $valueType")
    }
}
