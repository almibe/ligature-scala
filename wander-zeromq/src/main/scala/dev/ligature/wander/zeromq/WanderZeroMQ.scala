/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.zeromq

import org.zeromq.{ZMQ, ZContext, SocketType}

import dev.ligature.Ligature
import dev.ligature.wander.run as runWander
import dev.ligature.wander.WanderValue
import dev.ligature.wander.printWanderValue
import dev.ligature.wander.preludes.common
import dev.ligature.wander.printResult

private class WanderZServer(val port: Int) extends Runnable {
  override def run(): Unit =
    val zContext = ZContext()
    val socket = zContext.createSocket(SocketType.REP)
    socket.bind(s"tcp://localhost:$port")
    var continue = true
    while (continue)
      try
        val query = String(socket.recv(0), ZMQ.CHARSET)
        val res = runWander(query, common())
        socket.send(printResult(res).getBytes(ZMQ.CHARSET), 0)
      catch
        case e => continue = false
}

def runServer(port: Int): AutoCloseable = {
  val thread = Thread(WanderZServer(port))
  thread.start()
  new AutoCloseable {
    def close(): Unit = ()
  }
}
