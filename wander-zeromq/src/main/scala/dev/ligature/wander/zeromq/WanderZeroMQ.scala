/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.zeromq

import org.zeromq.{ZMQ, ZContext, SocketType}

import dev.ligature.Ligature
import dev.ligature.wander.run as runWander
import dev.ligature.wander.WanderValue
import dev.ligature.wander.printWanderValue
import dev.ligature.wander.libraries.std
import dev.ligature.wander.printResult

private class WanderZServer(val port: Int) extends Runnable with AutoCloseable {
  private val zContext = ZContext()

  override def run(): Unit =
    val socket = zContext.createSocket(SocketType.REP)
    socket.bind(s"tcp://localhost:$port")
    var continue = true
    while (!Thread.currentThread().isInterrupted() && continue)
      try
        val query = String(socket.recv(0), ZMQ.CHARSET) // blocks waiting for a request
        val res = runWander(query, std())
        socket.send(printResult(res).getBytes(ZMQ.CHARSET), 0)
      catch case e => continue = false

  override def close(): Unit = zContext.close()
}

def runServer(port: Int): AutoCloseable = {
  val server = WanderZServer(port)
  val thread = Thread(server)
  thread.start()
  new AutoCloseable {
    def close(): Unit = server.close()
  }
}

@main def main =
  val server = WanderZServer(4200)
  server.run()
