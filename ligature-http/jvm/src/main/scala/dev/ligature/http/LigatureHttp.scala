/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http

import cats.effect._, org.http4s._, org.http4s.dsl.io._
import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import scala.concurrent.duration._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.Ligature

object LigatureHttp extends IOApp {
  // NOTE: for now while developing I'm just using a hard coded in-memory instance of Ligature.
  // Eventually I'll probably flip this dependency and each implementation of Ligature will call into Ligature-HTTP.
  val ligature = InMemoryLigature()

  def run(args: List[String]): IO[ExitCode] = {
    if (args.length == 1 && args(0) == "--local") { // currently only supports --local mode
      startLocal()
    } else {
      IO {
        println("Could not start application.")
        println("A single mode argument is required.")
        println("Supported modes:")
        println("  --local")
        ExitCode.Error
      }
    }
  }

  private def startLocal(): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(testApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

  val testApp = HttpApp((req: Request[IO]) => 
     for {
       res <- req.body.through(fs2.text.uft8Decode)
       res2 <- res.compile.drain
     } yield Response[IO](res2))
//    IO {
//    println("test")
//    Response[IO]()
  })

  // val helloWorldService = HttpRoutes
  //   .of[IO] { 
  //     case res @ GET -> Root / "hello" / name => {
  //       for {
  //         body <- res.bodyText
  //       } yield Ok(body)
  //     }
  //     case GET -> Root / "datasets" => Ok("[]")
  //     case POST -> Root / "datasets" / datasetName => ???
  //     case DELETE -> Root / "datasets" / datasetName => ???
  //     case GET -> Root / "datasets" / datasetName / "statements" => ???
  //     case POST -> Root / "datasets" / datasetName / "statements" => ???
  //     case DELETE -> Root / "datasets" / datasetName / "statements" => ???
  //     case POST -> Root / "datasets" / datasetName / "wander" => ???
  //   }
  //   .orNotFound

  // val datasetsRoute = HttpRoutes
  //   .of[IO] {
  //   }
  //   .orNotFound

  // val routes = helloWorldService <+> datasetsRoute
}

//     //TODO get all Datasets (with optional prefix)
//     router.route(HttpMethod.GET, "/datasets/").handler(ctx => {
//       val response = ctx.response()
//       response.putHeader("content-type", "application/json")
//       response.end(JsonArray().toString)
//     })

//     //TODO create Dataset
//     router.route(HttpMethod.POST, "/datasets/:datasetName").handler(ctx => {
//       ???
//     })

//     //TODO delete Dataset
//     router.route(HttpMethod.DELETE, "/datasets/:datasetName").handler(ctx => {
//       ???
//     })

//     //TODO add Statements to Dataset
//     router.route(HttpMethod.POST, "/datasets/:datasetName/statements").handler(ctx => {
//       ???
//     })

//     //TODO delete Statements from Dataset
//     router.route(HttpMethod.DELETE, "/datasets/:datasetName/statements").handler(ctx => {
//       ???
//     })

//     //TODO query Dataset
//     router.route(HttpMethod.POST, "/datasets/:datasetName/wander").handler(ctx => {
//       ???
//     })

//     server.requestHandler(router).listen(8080)
//   }
// }
