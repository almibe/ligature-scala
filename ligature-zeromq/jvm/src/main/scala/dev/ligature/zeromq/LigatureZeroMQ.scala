/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.zeromq

import cats.effect._
import cats.implicits._
import org.zeromq.{ZMQ, ZContext, SocketType}

val zeromqResource = Resource.make(
  IO(ZContext())
)( context => IO(context.close()))

object Main extends IOApp.Simple {
  val run = {
    zeromqResource.use { zContext =>
      val test = IO {
        println("test")
      }
      val rep = IO {
        val socket = zContext.createSocket(SocketType.REP)
        socket.bind("tcp://localhost:5555")
        val bytes = socket.recv(0)
        println(String(bytes, ZMQ.CHARSET))
        socket.send("World".getBytes(ZMQ.CHARSET), 0)
      }
      val res = IO {
        val socket = zContext.createSocket(SocketType.REQ)
        socket.connect("tcp://localhost:5555")
        socket.send("Hello".getBytes(ZMQ.CHARSET), 0)
        val res = socket.recv(0)
        println(String(res, ZMQ.CHARSET))
      }
      List(test, rep, res).parSequence.flatMap { _ => IO.pure(()) }
    }
  }
}
