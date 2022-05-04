/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import dev.ligature.Dataset

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import fs2.Stream

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import dev.ligature.inmemory.InMemoryLigature
import munit._

import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.http4s.syntax.all._
import dev.ligature.inmemory.InMemoryLigature

import munit.Clue.generate

class LigatureHttpSuite extends FunSuite {
  def createInstance() = LigatureHttp(InMemoryLigature()) //hard-coded for now

  test("Datasets should initially be empty") {
    val instance = createInstance()
    val response = instance.routes
      .run(Request(method = Method.GET, uri = uri"/datasets"))
      .unsafeRunSync()
    val res = response.bodyText.compile.string.unsafeRunSync()
    assertEquals(res, "[]")
  }

  test("Add Datasets") {
    val instance = createInstance()
    instance.routes
      .run(Request(method = Method.POST, uri = uri"/datasets/new"))
      .unsafeRunSync()
    val response = instance.routes
      .run(Request(method = Method.GET, uri = uri"/datasets"))
      .unsafeRunSync()
    val res = response.bodyText.compile.string.unsafeRunSync()
    assertEquals(res, "[\"new\"]")
  }

  // test("Query Datasets w/ prefix") {
  //   val writes = List("test/test1", "test/test2", "test3/test")
  //     .map { ds => toIO(() => client.post(port, local, s"/$ds").send()) }
  //     .reduce(_ >> _)

  //   val res = runServer(port, createLigature()) { _ =>
  //     for {
  //       _ <- writes
  //       res <- toIO(() => client.get(port, local, "/?prefix=test%2F").send())
  //     } yield res
  //   }.unsafeRunSync()
  //   assertEquals(
  //     JsonParser.parseString(res.bodyAsString()).getAsJsonArray,
  //     JsonParser.parseString("[\"test/test1\",\"test/test2\"]").getAsJsonArray
  //   )
  // }

  // test("Query Datasets w/ range") {
  //   val writes = List("test", "test1/test1", "test2/test2", "test3/test")
  //     .map { ds => toIO(() => client.post(port, local, s"/$ds").send()) }
  //     .reduce(_ >> _)
  //   val res = runServer(port, createLigature()) { _ =>
  //     for {
  //       _ <- writes
  //       res <- toIO(() =>
  //         client.get(port, local, "/?start=test1&end=test3").send()
  //       )
  //     } yield res
  //   }.unsafeRunSync()
  //   assertEquals(
  //     JsonParser.parseString(res.bodyAsString()).getAsJsonArray(),
  //     JsonParser
  //       .parseString("[\"test1/test1\",\"test2/test2\"]")
  //       .getAsJsonArray()
  //   )
  // }

  test("Delete Datasets") {
    val instance = createInstance()
    instance.routes
      .run(Request(method = Method.POST, uri = uri"/datasets/new2"))
      .unsafeRunSync()
    instance.routes
      .run(Request(method = Method.POST, uri = uri"/datasets/new3"))
      .unsafeRunSync()
    instance.routes
      .run(Request(method = Method.DELETE, uri = uri"/datasets/new3"))
      .unsafeRunSync()
    val response = instance.routes
      .run(Request(method = Method.GET, uri = uri"/datasets"))
      .unsafeRunSync()
    val res = response.bodyText.compile.string.unsafeRunSync()
    assertEquals(res, "[\"new2\"]")
  }

  test("Statements in new Dataset should start empty") {
    val instance = createInstance()
    instance.routes
      .run(Request(method = Method.POST, uri = uri"/datasets/new"))
      .unsafeRunSync()
    val response = instance.routes
      .run(Request(method = Method.GET, uri = uri"/datasets/new/statements"))
      .unsafeRunSync()
    val res = response.bodyText.compile.string.unsafeRunSync()
    assertEquals(res, "")
  }

  test("Add a single Statement") {
    val instance = createInstance()
    instance.routes
      .run(Request(method = Method.POST, uri = uri"/datasets/new"))
      .unsafeRunSync()
    val writeResponse = instance.routes
      .run(
        Request(method = Method.POST, uri = uri"/datasets/new/statements")
          .withEntity("<a> <b> <c>")
      )
      .unsafeRunSync().bodyText.compile.string.unsafeRunSync()
    assertEquals(writeResponse, "")
    val response = instance.routes
      .run(Request(method = Method.GET, uri = uri"/datasets/new/statements"))
      .unsafeRunSync()
    val res = response.bodyText.compile.string.unsafeRunSync()
    assertEquals(res, "<a> <b> <c>\n")

//     val input = List(
//       AtomicApiStatement("1", "attribute", "2", "Entity", "stat1/"),
//       AtomicApiStatement("3", "attribute", "1", "Entity", "stat2/"),
//       AtomicApiStatement("4", "attribute2", "Hello", "StringLiteral", "stat3/"),
//       AtomicApiStatement("5", "attribute3", "3453", "IntegerLiteral", "stat4/"),
//       AtomicApiStatement("1", "attribute4", "4.2", "FloatLiteral", "stat5/")
//     )
//
//     awaitResult < HttpResponse < Buffer >> {
//       h -> // create Dataset
//         client.post(port, local, "/testDataset").send(h)
//     }
//     input.forEach {
//       statement ->
//       val res = awaitResult < HttpResponse < Buffer >> {
//         h -> // add Statement
//           client
//             .post(port, local, "/testDataset")
//             .sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
//       }
//     }
//     val res = awaitResult < HttpResponse < Buffer >> {
//       h -> // get all Statements
//         client.get(port, local, "/testDataset").send(h)
//     }
//     JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
//       JsonParser.parseString(expected).asJsonArray
  }

//TODO add test for adding multiple Statements

  // //     test("Match Statements") {
  // //         val input = listOf(
  // //             AtomicApiStatement(null, "attribute", null, "Entity"),
  // //             AtomicApiStatement(null, "attribute", "1", "Entity"),
  // //             AtomicApiStatement(null, "attribute2", "Hello", "StringLiteral"),
  // //             AtomicApiStatement(null, "attribute3", "3453", "IntegerLiteral"),
  // //             AtomicApiStatement("1", "attribute4", "4.2", "FloatLiteral"),
  // //         )

  // //         val expected1 = gson.toJson(listOf(
  // //             AtomicApiPersistedStatement("1", "attribute", "2", "Entity", "3"),
  // //             AtomicApiPersistedStatement("1", "attribute4", "4.2", "FloatLiteral", "10"),
  // //         ))
  // //         val expected2 = gson.toJson(listOf(
  // //             AtomicApiPersistedStatement("1", "attribute", "2", "Entity", "3"),
  // //             AtomicApiPersistedStatement("4", "attribute", "1", "Entity", "5"),
  // //         ))
  // //         val expected3 = gson.toJson(listOf(
  // //             AtomicApiPersistedStatement("8", "attribute3", "3453", "IntegerLiteral", "9"),
  // //         ))

  // //         awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
  // //             client.post(port, local, "/testDataset").send(h)
  // //         }
  // //         input.forEach { statement ->
  // //             awaitResult<HttpResponse<Buffer>> { h -> //add Statement
  // //                 client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
  // //             }
  // //         }

  // //         val res1 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
  // //             client.get(port, local, "/testDataset?entity=1").send(h)
  // //         }
  // //         val res2 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
  // //             client.get(port, local, "/testDataset?attribute=attribute").send(h)
  // //         }
  // //         val res3 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
  // //             client.get(port, local, "/testDataset?entity=8&value=3453&value-type=IntegerLiteral&context=9").send(h)
  // //         }
  // //         JsonParser.parseString(res1.bodyAsString()).asJsonArray shouldBe
  // //                 JsonParser.parseString(expected1).asJsonArray
  // //         JsonParser.parseString(res2.bodyAsString()).asJsonArray shouldBe
  // //                 JsonParser.parseString(expected2).asJsonArray
  // //         JsonParser.parseString(res3.bodyAsString()).asJsonArray shouldBe
  // //                 JsonParser.parseString(expected3).asJsonArray
  // //     }

  // //     test("Match Statements with ranges") {
  // //         val input = listOf(
  // //             AtomicApiStatement(null, "attribute", "1", "IntegerLiteral"),
  // //             AtomicApiStatement(null, "attribute", "2", "IntegerLiteral"),
  // //             AtomicApiStatement(null, "attribute", "3", "IntegerLiteral"),
  // //             AtomicApiStatement(null, "attribute", "4.2", "FloatLiteral"),
  // //             AtomicApiStatement(null, "attribute", "4.3", "FloatLiteral"),
  // //         )

  // //         val expected1 = gson.toJson(listOf(
  // //             AtomicApiPersistedStatement("1", "attribute", "1", "IntegerLiteral", "2"),
  // //             AtomicApiPersistedStatement("3", "attribute", "2", "IntegerLiteral", "4"),
  // //         ))
  // //         val expected2 = gson.toJson(listOf(
  // //             AtomicApiPersistedStatement("7", "attribute", "4.2", "FloatLiteral", "8"),
  // //         ))

  // //         awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
  // //             client.post(port, local, "/testDataset").send(h)
  // //         }
  // //         input.forEach { statement ->
  // //             awaitResult<HttpResponse<Buffer>> { h -> //add Statement
  // //                 client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
  // //             }
  // //         }

  // //         val res1 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
  // //             client.get(port, local, "/testDataset?value-start=1&value-end=3&value-type=IntegerLiteral").send(h)
  // //         }
  // //         val res2 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
  // //             client.get(port, local, "/testDataset?value-start=4.1&value-end=4.3&value-type=FloatLiteral").send(h)
  // //         }
  // //        JsonParser.parseString(res1.bodyAsString()).asJsonArray shouldBe
  // //                 JsonParser.parseString(expected1).asJsonArray
  // //         JsonParser.parseString(res2.bodyAsString()).asJsonArray shouldBe
  // //                 JsonParser.parseString(expected2).asJsonArray
  // //     }

  // //     test("Delete Statements") {
  // //         val input = listOf(
  // //             AtomicApiStatement(null, "attribute", null, "Entity"),
  // //             AtomicApiStatement(null, "attribute", "1", "Entity"),
  // //             AtomicApiStatement(null, "attribute2", "Hello", "StringLiteral"),
  // //             AtomicApiStatement(null, "attribute3", "3453", "IntegerLiteral"),
  // //             AtomicApiStatement("1", "attribute4", "4.2", "FloatLiteral"),
  // //         )

  // //         val toDelete = listOf(
  // //             AtomicApiPersistedStatement("1", "attribute", "2", "Entity", "3"),
  // //             AtomicApiPersistedStatement("4", "attribute", "1", "Entity", "5"),
  // //             AtomicApiPersistedStatement("6", "attribute2", "Hello", "StringLiteral", "98"), //doesn't match
  // //             AtomicApiPersistedStatement("8", "attribute3", "3454", "IntegerLiteral", "9"), //doesn't match
  // //             AtomicApiPersistedStatement("2", "attribute4", "4.2", "FloatLiteral", "10"), //doesn't match
  // //         )

  // //         val out = listOf(
  // //             AtomicApiPersistedStatement("6", "attribute2", "Hello", "StringLiteral", "7"), //doesn't match
  // //             AtomicApiPersistedStatement("8", "attribute3", "3453", "IntegerLiteral", "9"), //doesn't match
  // //             AtomicApiPersistedStatement("1", "attribute4", "4.2", "FloatLiteral", "10"), //doesn't match
  // //         )
  // //         val expected = gson.toJson(out)

  // //         awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
  // //             client.post(port, local, "/testDataset").send(h)
  // //         }
  // //         input.forEach { statement ->
  // //             awaitResult<HttpResponse<Buffer>> { h -> //add Statement
  // //                 client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
  // //             }
  // //         }
  // //         toDelete.forEach { statement ->
  // //             awaitResult<HttpResponse<Buffer>> { h -> //add Statement
  // //                 client.delete(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
  // //             }
  // //         }
  // //         val res = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
  // //             client.get(port, local, "/testDataset").send(h)
  // //         }
  // //         JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
  // //                 JsonParser.parseString(expected).asJsonArray
  // //     }
  // // }
}
