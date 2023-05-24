/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import java.io.IOException;
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import dev.ligature.wander.run
import dev.ligature.wander.printResult
import dev.ligature.wander.instanceMode
import dev.ligature.inmemory.InMemoryLigature
import cats.effect.unsafe.implicits.global
import dev.ligature.wander.WanderValue
import dev.ligature.LigatureLiteral
import dev.ligature.LigatureError
import cats.effect.{IO, Resource}
import cats.effect.IOApp
import cats.effect.ExitCode
import dev.ligature.inmemory.createInMemoryLigature
import dev.ligature.Ligature
import dev.ligature.wander.run as runWander
import dev.ligature.wander.common

case class TerminalResources(val terminal: Terminal, val reader: LineReader)

val terminalResource: Resource[IO, TerminalResources] = 
  Resource.make { 
    val terminal: Terminal = TerminalBuilder.builder().build()
    val parser: DefaultParser = new DefaultParser()
    val reader: LineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .parser(parser)
      .build();
    IO.pure(TerminalResources(terminal, reader))
  }(terminal => IO(terminal.terminal.close()))


object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    createInMemoryLigature().use { ligature =>
      terminalResource.use { terminal =>
        for {
          _ <- IO.println("Welcome to Ligature's REPL!")
          res <- repl(ligature, terminal)
        } yield res
      }
    }

  def repl(ligature: Ligature, terminal: TerminalResources): IO[ExitCode] = {
      for {
        line <- IO.blocking { terminal.reader.readLine("> ") }
        res <- 
          if (line == ":q") { 
            IO.println("Bye!").map { _ => ExitCode.Success } 
          } else {
            for {
              res <- runWander(line, instanceMode(ligature))
              _ <- IO.println(printResult(res))
              code <- repl(ligature, terminal) 
            } yield code
          }
      } yield ExitCode.Success
  }
}

//       val res2 = res.handleError { e =>
//         e match
//           case LigatureError(userMessage) => WanderValue.LigatureValue(LigatureLiteral.StringLiteral(userMessage))
//           case e => WanderValue.LigatureValue(LigatureLiteral.StringLiteral(e.getMessage()))
//         }
