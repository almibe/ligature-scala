/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.zeromq

import munit.*
import cats.implicits._
import cats.effect.IO
import dev.ligature.inmemory.createInMemoryLigature
import org.zeromq.{ZMQ, SocketType, ZContext}

class LigatureZeroMQSuite extends CatsEffectSuite {
  val port = 4200

  def runTest(request: String, expected: String) =
    zeromqResource.use { zContext =>
      createInMemoryLigature().use { instance =>
        val server: IO[String] = runServer(zContext, instance, port).map(_ => "")

        val runRequest: IO[String] = IO {
          val socket = zContext.createSocket(SocketType.REQ)
          socket.connect(s"tcp://localhost:$port")
          socket.send(request.getBytes(ZMQ.CHARSET), 0)
          val result = String(socket.recv(0), ZMQ.CHARSET)
          zContext.close()
          result
        }

        val result = List(server, runRequest).parSequence.map(_(1))
        assertIO(result, expected)
      }
    }

  test("eval literals") {
    val request = "true"
    val expected = "true"
    runTest(request, expected)
  }
  test("basic function call") {
    val request = "not(false)"
    val expected = "true"
    runTest(request, expected)
  }
  test("datasets call") {
    val request = """addDataset("hello") datasets()"""
    val expected = """["hello"]"""
    runTest(request, expected)
  }
}
