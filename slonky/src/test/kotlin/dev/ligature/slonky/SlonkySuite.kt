/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import dev.ligature.PersistedStatement
import dev.ligature.inmemory.InMemoryLigature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.awaitResult

data class AtomicApiStatement(
    val entity: String?,
    val attribute: String,
    val value: String?,
    @SerializedName("value-type") val valueType: String)

data class AtomicApiPersistedStatement(
    val entity: String,
    val attribute: String,
    val value: String,
    @SerializedName("value-type") val valueType: String,
    val context: String)

class SlonkySuite: FunSpec() {
    init {
        val port = 4444
        val local = "localhost"
        lateinit var server: Server
        //this mean that server + client have different vertx instances, but this shouldn't be an issue
        val client = WebClient.create(Vertx.vertx())
        val gson = GsonBuilder().serializeNulls().create()

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

        test("Add Statements") {
            val input = listOf(
                AtomicApiStatement(null, "attribute", null, "Entity"),
                AtomicApiStatement(null, "attribute", "1", "Entity"),
                AtomicApiStatement(null, "attribute2", "Hello", "StringLiteral"),
                AtomicApiStatement(null, "attribute3", "3453", "IntegerLiteral"),
                AtomicApiStatement("1", "attribute4", "4.2", "FloatLiteral"),
            )

            val out = listOf(
                AtomicApiPersistedStatement("1", "attribute", "2", "Entity", "3"),
                AtomicApiPersistedStatement("4", "attribute", "1", "Entity", "5"),
                AtomicApiPersistedStatement("6", "attribute2", "Hello", "StringLiteral", "7"),
                AtomicApiPersistedStatement("8", "attribute3", "3453", "IntegerLiteral", "9"),
                AtomicApiPersistedStatement("1", "attribute4", "4.2", "FloatLiteral", "10"),
            )

            val expected = gson.toJson(out)

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            input.forEach { statement ->
                val res = awaitResult<HttpResponse<Buffer>> { h -> //add Statement
                    client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
                }
            }
            val res = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset").send(h)
            }
            JsonParser.parseString(res.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString(expected).asJsonArray
        }

        test("Match Statements") {
            val input = listOf(
                AtomicApiStatement(null, "attribute", null, "Entity"),
                AtomicApiStatement(null, "attribute", "1", "Entity"),
                AtomicApiStatement(null, "attribute2", "Hello", "StringLiteral"),
                AtomicApiStatement(null, "attribute3", "3453", "IntegerLiteral"),
                AtomicApiStatement("1", "attribute4", "4.2", "FloatLiteral"),
            )

            val expected1 = gson.toJson(listOf(
                AtomicApiPersistedStatement("1", "attribute", "2", "Entity", "3"),
                AtomicApiPersistedStatement("1", "attribute4", "4.2", "FloatLiteral", "10"),
            ))
            val expected2 = gson.toJson(listOf(
                AtomicApiPersistedStatement("1", "attribute", "2", "Entity", "3"),
                AtomicApiPersistedStatement("4", "attribute", "1", "Entity", "5"),
            ))
            val expected3 = gson.toJson(listOf(
                AtomicApiPersistedStatement("8", "attribute3", "3453", "IntegerLiteral", "9"),
            ))

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            input.forEach { statement ->
                awaitResult<HttpResponse<Buffer>> { h -> //add Statement
                    client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
                }
            }

            val res1 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?entity=1").send(h)
            }
            val res2 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?attribute=attribute").send(h)
            }
            val res3 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?entity=8&value=3453&value-type=IntegerLiteral&context=9").send(h)
            }
            JsonParser.parseString(res1.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString(expected1).asJsonArray
            JsonParser.parseString(res2.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString(expected2).asJsonArray
            JsonParser.parseString(res3.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString(expected3).asJsonArray
        }

        test("Match Statements with ranges") {
            val input = listOf(
                AtomicApiStatement(null, "attribute", "1", "IntegerLiteral"),
                AtomicApiStatement(null, "attribute", "2", "IntegerLiteral"),
                AtomicApiStatement(null, "attribute", "3", "IntegerLiteral"),
                AtomicApiStatement(null, "attribute", "4.2", "FloatLiteral"),
                AtomicApiStatement(null, "attribute", "4.3", "FloatLiteral"),
            )

            val expected1 = gson.toJson(listOf(
                AtomicApiStatement(null, "attribute", "1", "IntegerLiteral"),
                AtomicApiStatement(null, "attribute", "2", "IntegerLiteral"),
            ))
            val expected2 = gson.toJson(listOf(
                AtomicApiStatement(null, "attribute", "4.2", "FloatLiteral"),
            ))

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            input.forEach { statement ->
                awaitResult<HttpResponse<Buffer>> { h -> //add Statement
                    client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(gson.toJson(statement)), h)
                }
            }

            val res1 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?value-start=1&value-end=3&value-type=IntegerLiteral").send(h)
            }
            val res2 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?value-start=4.1&value-end=4.3&value-type=FloatLiteral").send(h)
            }
           JsonParser.parseString(res1.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString(expected1).asJsonArray
            JsonParser.parseString(res2.bodyAsString()).asJsonArray shouldBe
                    JsonParser.parseString(expected2).asJsonArray
        }

//        test("Delete Statements") {
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get(port, local, "").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
    }
}
