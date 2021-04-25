/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.*
import dev.ligature.*
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.lig.LigParser
import dev.ligature.lig.LigWriter
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
        val ligWriter = LigWriter()
        val ligParser = LigParser()

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
            res.bodyAsString() shouldBe null //Vert.x treats an empty body as null and not empty String :(
        }

        test("Add Statements") {
            val input = listOf(
                Statement(Entity("ent1"), Attribute("attribute"), StringLiteral("Hey"), Entity("Context1")),
                Statement(Entity("ent1"), Attribute("attribute"), StringLiteral("Hey"), Entity("Context1")), //dupe
                Statement(Entity("ent1"), Attribute("attribute"), StringLiteral("Hey"), Entity("Context2")),
                Statement(Entity("ent2"), Attribute("size"), IntegerLiteral(34537463), Entity("Context3")),
                Statement(Entity("ent3"), Attribute("notPi"), FloatLiteral(3.131123), Entity("Context4")),
                Statement(Entity("ent4"), Attribute("attribute5"), Entity("Hey"), Entity("Context5")),
            )

            val expected = input.toSet() //use a set to check for case of adding repeated Statements...see above

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            val writeResponse = awaitResult<HttpResponse<Buffer>> { h -> //add statements as lig
                client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(ligWriter.write(input.iterator())), h)
            }
            writeResponse.statusCode() shouldBe 200
            val res = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements as lig
                client.get(port, local, "/testDataset").send(h)
            }.bodyAsString()
            val resStatements = ligParser.parse(res).asSequence().toSet()
            resStatements shouldBe expected
        }

        test("Match Statements") {
            val entities = (1..5).map { i -> Entity("entity$i") }.toList()
            val attributes = (1..4).map { i -> Attribute("attribute$i") }.toList()
            val values = listOf(StringLiteral("Hello"), IntegerLiteral(3453), FloatLiteral(4.2))
            val contexts = (1..5).map { i -> Entity("context$i") }.toList()

            val input = setOf(
                Statement(entities[0], attributes[0], entities[1], contexts[0]),
                Statement(entities[2], attributes[0], entities[0], contexts[1]),
                Statement(entities[3], attributes[1], values[0], contexts[2]),
                Statement(entities[4], attributes[2], values[1], contexts[3]),
                Statement(entities[0], attributes[3], values[2], contexts[4]),
            )

            val expected1 = setOf(
                Statement(entities[0], attributes[0], entities[1], contexts[0]),
                Statement(entities[0], attributes[3], values[2], contexts[4]),
            )
            val expected2 = setOf(
                Statement(entities[0], attributes[0], entities[1], contexts[0]),
                Statement(entities[2], attributes[0], entities[0], contexts[1]),
            )
            val expected3 = setOf(
                Statement(entities[4], attributes[2], values[1], contexts[3])
            )

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            val writeResponse = awaitResult<HttpResponse<Buffer>> { h -> //add statements as lig
                client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(ligWriter.write(input.iterator())), h)
            }
            writeResponse.statusCode() shouldBe 200

            val res1 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?entity=entity1").send(h)
            }.bodyAsString()
            val res2 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?attribute=attribute1").send(h)
            }.bodyAsString()
            val res3 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?entity=entity5&value=3453&value-type=IntegerLiteral&context=context4").send(h)
            }.bodyAsString()

            val resStatements1 = ligParser.parse(res1).asSequence().toSet()
            val resStatements2 = ligParser.parse(res2).asSequence().toSet()
            val resStatements3 = ligParser.parse(res3).asSequence().toSet()
            resStatements1 shouldBe expected1
            resStatements2 shouldBe expected2
            resStatements3 shouldBe expected3
        }

        test("Match Statements with ranges") {
            val entities = (1..5).map { i -> Entity("entity$i") }.toList()
            val attribute = Attribute("attribute")
            val values = listOf(
                IntegerLiteral(1),
                IntegerLiteral(2),
                IntegerLiteral(3),
                FloatLiteral(4.2),
                FloatLiteral(4.3))
            val contexts = (1..5).map { i -> Entity("context$i") }.toList()

            val input = setOf(
                Statement(entities[0], attribute, values[0], contexts[0]),
                Statement(entities[1], attribute, values[1], contexts[1]),
                Statement(entities[2], attribute, values[2], contexts[2]),
                Statement(entities[3], attribute, values[3], contexts[3]),
                Statement(entities[4], attribute, values[4], contexts[4]),
            )

            val expected1 = setOf(
                Statement(entities[0], attribute, values[0], contexts[0]),
                Statement(entities[1], attribute, values[1], contexts[1]),
            )
            val expected2 = setOf(
                Statement(entities[3], attribute, values[3], contexts[3]),
            )

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            val writeResponse = awaitResult<HttpResponse<Buffer>> { h -> //add statements as lig
                client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(ligWriter.write(input.iterator())), h)
            }
            writeResponse.statusCode() shouldBe 200

            val res1 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?value-start=1&value-end=3&value-type=IntegerLiteral").send(h)
            }.bodyAsString()
            val res2 = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset?value-start=4.1&value-end=4.3&value-type=FloatLiteral").send(h)
            }.bodyAsString()

            val resStatements1 = ligParser.parse(res1).asSequence().toSet()
            val resStatements2 = ligParser.parse(res2).asSequence().toSet()
            resStatements1 shouldBe expected1
            resStatements2 shouldBe expected2
        }

        test("Delete Statements") {
            val entities = (1..5).map { i -> Entity("entity$i") }.toList()
            val attributes = (1..4).map { i -> Attribute("attribute$i") }.toList()
            val values = listOf(
                IntegerLiteral(1),
                StringLiteral("Hello"),
                IntegerLiteral(3453),
                FloatLiteral(4.2))
            val contexts = (1..5).map { i -> Entity("context$i") }.toList()

            val input = setOf(
                Statement(entities[0], attributes[0], entities[1], contexts[0]),
                Statement(entities[2], attributes[0], values[0], contexts[1]),
                Statement(entities[3], attributes[1], values[1], contexts[2]),
                Statement(entities[4], attributes[2], values[2], contexts[3]),
                Statement(entities[0], attributes[3], values[3], contexts[4]),
            )

            val toDelete = setOf(
                Statement(entities[0], attributes[0], entities[1], contexts[0]),
                Statement(entities[2], attributes[0], values[0], contexts[1]),
                Statement(Entity("6"), Attribute("attribute2"), values[1], contexts[2]), //doesn't match
                Statement(Entity("8"), Attribute("attribute3"), values[2], contexts[3]), //doesn't match
                Statement(Entity("2"), Attribute("attribute4"), values[3], contexts[4]), //doesn't match
            )

            val expected = setOf(
                Statement(entities[3], attributes[1], values[1], contexts[2]),
                Statement(entities[4], attributes[2], values[2], contexts[3]),
                Statement(entities[0], attributes[3], values[3], contexts[4]),
            )

            awaitResult<HttpResponse<Buffer>> { h -> //create Dataset
                client.post(port, local, "/testDataset").send(h)
            }
            val writeResponse = awaitResult<HttpResponse<Buffer>> { h -> //add statements as lig
                client.post(port, local, "/testDataset").sendBuffer(Buffer.buffer(ligWriter.write(input.iterator())), h)
            }
            writeResponse.statusCode() shouldBe 200

            val deleteResponse = awaitResult<HttpResponse<Buffer>> { h ->
                client.delete(port, local, "/testDataset").sendBuffer(Buffer.buffer(ligWriter.write(toDelete.iterator())), h)
            }
            deleteResponse.statusCode() shouldBe 200

            val res = awaitResult<HttpResponse<Buffer>> { h -> //get all Statements
                client.get(port, local, "/testDataset").send(h)
            }.bodyAsString()

            val resStatements = ligParser.parse(res).asSequence().toSet()
            resStatements shouldBe expected
        }
    }
}
