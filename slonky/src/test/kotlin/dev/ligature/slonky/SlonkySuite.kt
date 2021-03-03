/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import dev.ligature.inmemory.InMemoryLigature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.awaitResult

class SlonkySuite: FunSpec() {
    init {
        val port = 4444

        test("Datasets should initially be empty") {
            val server = Server(port, InMemoryLigature()) //TODO should be in before
            val vertx = Vertx.vertx()
            val client = WebClient.create(vertx)
            val res = awaitResult<HttpResponse<Buffer>> { h ->
                client.get(port, "localhost", "").send(h)
            }
            res.bodyAsString() shouldBe null
            server.shutDown()
//            res.bodyAsString().lines().isEmpty())
        }

//        test("Add Datasets") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Query Datasets w/ prefix") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Query Datasets w/ range") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Delete Datasets") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Statements in new Dataset should start empty") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Add Statements") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Match Statements") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Match Statements with ranges") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
//
//        test("Delete Statements") {
//            main() //TODO should be in before
//            val vertx = Vertx.vertx()
//            val client = WebClient.create(vertx)
//
//            //TODO insert POSTs to add Datasets
//
//            val res = client.get("$root/").send().result()
//            assert(res.bodyAsString().lines().isEmpty()) //TODO replace with shouldBe w/ values
//            TODO()
//        }
    }
}
