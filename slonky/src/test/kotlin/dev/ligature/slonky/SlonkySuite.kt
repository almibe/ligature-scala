/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import dev.ligature.inmemory.InMemoryLigature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.awaitResult

class SlonkySuite: FunSpec() {
    init {
        val port = 4444
        val local = "localhost"
        lateinit var server: Server
        //this mean that server + client have different vertx instances, but this shouldn't be an issue
        val client = WebClient.create(Vertx.vertx())

        beforeTest {
            server = Server(port, InMemoryLigature())
        }

        afterTest {
            server.shutDown()
        }

        test("Datasets should initially be empty") {
            val res = awaitResult<HttpResponse<Buffer>> { h ->
                client.get(port, local, "").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe JsonArray()
        }

        test("Add Datasets") {
            awaitResult<HttpResponse<Buffer>> { h ->
                client.post(port, local, "/testDataset").send(h)
            }
            val res = awaitResult<HttpResponse<Buffer>> { h ->
                client.get(port, local, "").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
                JsonParser.parseString("[\"testDataset\"]").asJsonArray
        }

        test("Query Datasets w/ prefix") {
            listOf("test/test1", "test/test2", "test3/test").forEach {
                awaitResult<HttpResponse<Buffer>> { h ->
                    client.post(port, local, "/$it").send(h)
                }
            }
            val res = awaitResult<HttpResponse<Buffer>> { h ->
                client.get(port, local, "/?prefix=test%2F").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
                JsonParser.parseString("[\"test/test1\",\"test/test2\"]").asJsonArray
        }

        test("Query Datasets w/ range") {
            listOf("test", "test1/test1", "test2/test2", "test3/test").forEach {
                awaitResult<HttpResponse<Buffer>> { h ->
                    client.post(port, local, "/$it").send(h)
                }
            }
            val res = awaitResult<HttpResponse<Buffer>> { h ->
                client.get(port, local, "/?start=test1&end=test3").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString("[\"test1/test1\",\"test2/test2\"]").asJsonArray
        }

        test("Delete Datasets") {
            listOf("test", "test1/test1", "test2/test2", "test3/test").forEach {
                awaitResult<HttpResponse<Buffer>> { h ->
                    client.post(port, local, "/$it").send(h)
                }
            }
            awaitResult<HttpResponse<Buffer>> { h ->
                client.delete(port, local, "/test2/test2").send(h)
            }
            val res = awaitResult<HttpResponse<Buffer>> { h ->
                client.get(port, local, "").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString("[\"test\",\"test1/test1\",\"test3/test\"]").asJsonArray
        }

        test("Statements in new Dataset should start empty") {
            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            val res = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe JsonArray()
        }

//        test("Add Statements") {
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get(port, local, "").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Match Statements") {
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get(port, local, "").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Match Statements with ranges") {
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get(port, local, "").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Delete Statements") {
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get(port, local, "").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
    }
}
