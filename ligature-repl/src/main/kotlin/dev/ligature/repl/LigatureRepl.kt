/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import dev.ligature.Ligature
import dev.ligature.inmemory.InMemoryLigature
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReader.Option
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder

data class LigatureInstance(
  var instance: Ligature,
  var displayName: String
)

fun main() {
//  val commands = mutableListOf<Command>()
  val ligatureInstance = LigatureInstance(InMemoryLigature(), "In-Memory")
  val terminal = TerminalBuilder.terminal()
  val reader = LineReaderBuilder.builder()
    .terminal(terminal)
//    .completer(completer)
//    .parser(parser)
    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
    .variable(LineReader.INDENTATION, 2)
    .option(Option.INSERT_BRACKET, true)
    .build()

  val prompt = ">"

  var `continue` = true
  while (`continue`) {
    try {
      val line = reader.readLine(prompt)
      val res = WanderREPL.eval(line)
      println(res)
    } catch (e: UserInterruptException) {
      `continue` = false
    } catch (e: EndOfFileException) {
      `continue` = false
    }
  }
}
