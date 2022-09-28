/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.testsuite

import dev.ligature.Dataset
import dev.ligature.Statement
import dev.ligature.lig.read
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

abstract class LigatureHttpSuite: FunSpec() {
  abstract fun Application.instanceModule()

  init {
    test("Datasets should initially be empty") {
      testApplication {
        application {
          instanceModule()
        }
        val response = client.get("/datasets")
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText() shouldBe ""
      }
    }

    test("Add Datasets") {
      testApplication {
        application {
          instanceModule()
        }
        val addResponse = client.post("/datasets/new")
        addResponse.status shouldBe HttpStatusCode.OK
        val response = client.get("/datasets")
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText() shouldBe "new\n"
      }
    }

//  // test("Query Datasets w/ prefix") {
//  //   val writes = List("test/test1", "test/test2", "test3/test")
//  //     .map { ds => toIO(() => client.post(port, local, s"/$ds").send()) }
//  //     .reduce(_ >> _)
//
//  //   val res = runServer(port, createLigature()) { _ =>
//  //     for {
//  //       _ <- writes
//  //       res <- toIO(() => client.get(port, local, "/?prefix=test%2F").send())
//  //     } yield res
//  //   }.unsafeRunSync()
//  //   assertEquals(
//  //     JsonParser.parseString(res.bodyAsString()).getAsJsonArray,
//  //     JsonParser.parseString("[\"test/test1\",\"test/test2\"]").getAsJsonArray
//  //   )
//  // }
//
//  // test("Query Datasets w/ range") {
//  //   val writes = List("test", "test1/test1", "test2/test2", "test3/test")
//  //     .map { ds => toIO(() => client.post(port, local, s"/$ds").send()) }
//  //     .reduce(_ >> _)
//  //   val res = runServer(port, createLigature()) { _ =>
//  //     for {
//  //       _ <- writes
//  //       res <- toIO(() =>
//  //         client.get(port, local, "/?start=test1&end=test3").send()
//  //       )
//  //     } yield res
//  //   }.unsafeRunSync()
//  //   assertEquals(
//  //     JsonParser.parseString(res.bodyAsString()).getAsJsonArray(),
//  //     JsonParser
//  //       .parseString("[\"test1/test1\",\"test2/test2\"]")
//  //       .getAsJsonArray()
//  //   )
//  // }
//
    test("Delete Datasets") {
      testApplication {
        application {
          instanceModule()
        }
        client.post("/datasets/new2")
        client.post("/datasets/new3")
        client.delete("/datasets/new3")
        val response = client.get("/datasets")
        response.bodyAsText() shouldBe "new2\n"
      }
    }

    test("Statements in new Dataset should start empty") {
      testApplication {
        application {
          instanceModule()
        }
        client.post("/datasets/new")
        val response = client.get("/datasets/new/statements")
        response.bodyAsText() shouldBe ""
      }
    }

    test("Add a single Statement") {
      testApplication {
        application {
          instanceModule()
        }
        client.post("/datasets/new")
        val writeResponse = client.post("/datasets/new/statements") {
          setBody("<a> <b> <c>")
        }
        writeResponse.status shouldBe HttpStatusCode.OK
        val response = client.get("/datasets/new/statements")
        response.bodyAsText() shouldBe "<a> <b> <c>\n"
      }
    }

    test("Add multiple Statements") {
      testApplication {
        application {
          instanceModule()
        }
        client.post("/datasets/new")
        val statements =
          """<1> <attribute> <2>
            |<3> <attribute> <1>""".trimMargin()
        client.get("/datasets/new")
        val writeResponse = client.post("/datasets/new/statements") {
          setBody(statements)
        }
        writeResponse.status shouldBe HttpStatusCode.OK
        val response = client.get("/datasets/new/statements")
        ligToSet(response.bodyAsText()) shouldBe ligToSet(statements)
      }
    }

    test("Delete Statements") {
      testApplication {
        application {
          instanceModule()
        }
        val addStatements =
          """
        |<1> <attribute> <2>
        |<3> <attribute> <1>
        |<4> <attribute2> "Hello"
        |<5> <attribute3> 3453
        |<1> <attribute3> <1>
        |""".trimMargin()

        val deleteStatements = // includes a dupe and a statement that doesn't exist
          """
        |<1> <attribute> <2>
        |<6> <attribute3> 3453
        |<1> <attribute3> <1>
        |<1> <attribute> <2>
        |""".trimMargin()

        val resultStatements =
          """
        |<3> <attribute> <1>
        |<4> <attribute2> "Hello"
        |<5> <attribute3> 3453
        |""".trimMargin()

        client.post("/datasets/new")

        client.post("/datasets/new/statements") {
          setBody(addStatements)
        }

        val deleteResponse = client.delete("/datasets/new/statements") {
          setBody(deleteStatements)
        }
        deleteResponse.status shouldBe HttpStatusCode.OK

        val response = client.get("/datasets/new/statements")
        ligToSet(response.bodyAsText()) shouldBe ligToSet(resultStatements)
      }
    }

    test("Run Wander") {
      testApplication {
        application {
          instanceModule()
        }
        client.post("/datasets/new")
        val wanderResponse = client.post("/datasets/new/wander") {
          setBody("true")
        }
        wanderResponse.status shouldBe HttpStatusCode.OK
        wanderResponse.bodyAsText() shouldBe "true"
      }
    }
  }
  private fun ligToSet(input: String): Set<Statement> =
    read(input).fold( { throw Error("Could not convert $input") }, { it.toSet() } )
}
