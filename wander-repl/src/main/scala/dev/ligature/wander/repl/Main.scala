/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.repl

import dev.ligature.wander.preludes.common

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import dev.ligature.wander.introspect
import dev.ligature.wander.run
import dev.ligature.wander.printResult
import dev.ligature.wander.WanderError
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Environment
import dev.ligature.wander
import dev.ligature.wander.ligature.LigatureInterpreter
import dev.ligature.inmemory.LigatureInMemory

@main def main =
//  val path = Path.of(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.ligature")
  val terminal: Terminal = TerminalBuilder.builder().dumb(true).build()
  val parser: DefaultParser = new DefaultParser()
  val reader: LineReader = LineReaderBuilder
    .builder()
    .terminal(terminal)
    .parser(parser)
    .build()
  var continue = true
  var lastResult: Either[WanderError, (WanderValue, Environment)] = null
  while (continue) {
    val script = reader.readLine("> ")
    if (script == ":q")
      continue = false
      println("Bye!")
    else
      val intro = introspect(script)
      val environment = if (lastResult == null) {
        common()
      } else {
        lastResult.getOrElse(???)._2
      }
      lastResult = run(script, environment)
      println(printResult(lastResult))
  }
  terminal.close()
