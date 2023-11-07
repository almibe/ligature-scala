/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.zeromq

import cats.effect.unsafe.implicits.global
import cats.effect._
import cats.implicits._

import org.zeromq.{ZMQ, ZContext, SocketType}

import dev.ligature.inmemory.createInMemoryLigature
import dev.ligature.Ligature
import dev.ligature.wander.run
import dev.ligature.wander.preludes.instancePrelude
import dev.ligature.wander.WanderValue
import dev.ligature.wander.printWanderValue

val zeromqResource = Resource.make(IO {
  ZContext()
})(context => IO(context.close()))

def runServer(zContext: ZContext, ligature: Ligature, port: Int) =
  IO {
    val socket = zContext.createSocket(SocketType.REP)
    socket.bind(s"tcp://localhost:$port")
    var continue = true
    while (continue)
      try
        val query = String(socket.recv(0), ZMQ.CHARSET)
        val res = run(query, instancePrelude(ligature)).unsafeRunSync()
        socket.send(printWanderValue(res).getBytes(ZMQ.CHARSET), 0)
      catch case e => continue = false
    ()
  }

object Main extends IOApp.Simple {
  val run =
    zeromqResource.use { zContext =>
      createInMemoryLigature().use { instance =>
        runServer(zContext, instance, 4200)
      }
    }
}
