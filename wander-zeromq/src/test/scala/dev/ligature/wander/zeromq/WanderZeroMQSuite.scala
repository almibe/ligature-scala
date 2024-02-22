/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.zeromq

import munit.*
import org.zeromq.{ZMQ, SocketType, ZContext}
import dev.ligature.wander.*
import dev.ligature.wander.modules.*

class LigatureZeroMQSuite extends FunSuite {
  val port = 4201

  def runTest(request: String, expected: String) = {
    val close = runServer(port)
    val zContext = ZContext()
    val result = {
      val socket = zContext.createSocket(SocketType.REQ)
      socket.connect(s"tcp://localhost:$port")
      socket.send(request.getBytes(ZMQ.CHARSET), 0)
      socket.setReceiveTimeOut(1000)
      val result = String(socket.recv(0), ZMQ.CHARSET)
      zContext.close()
      result
    }
    assertEquals(run(result, wmdn), run(expected, wmdn))
    close.close()
  }

  test("eval literals") {
    val request = """{ action = "run", script = "true" }"""
    val expected = "{ result = true }"
    runTest(request, expected)
  }
  test("basic function call") {
    val request = """{ action = "run", script = "Bool.not false" }"""
    val expected = "{ result = true }"
    runTest(request, expected)
  }
}
