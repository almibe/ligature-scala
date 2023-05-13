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

@main def main() = {
  println("Welcome to Ligature's REPL!")
  val terminal: Terminal = TerminalBuilder.builder().build()
  val parser: DefaultParser = new DefaultParser()
  val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .parser(parser)
    .build();
  var continue = true
  while (continue) {
    val line: String = reader.readLine("> ")
    if (line.trim() == ":q")
      continue = false
    else
      val res = run(line, instanceMode(InMemoryLigature()))
      val res2 = res.handleError { e =>
        e match
          case LigatureError(userMessage) => WanderValue.LigatureValue(LigatureLiteral.StringLiteral(userMessage))
          case e => WanderValue.LigatureValue(LigatureLiteral.StringLiteral(e.getMessage()))
        }
      println(printResult(res2.unsafeRunSync()))
  }
  terminal.close()
}
