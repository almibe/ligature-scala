/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import dev.ligature.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import org.jline.builtins.*;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Options.HelpException;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.LineReader.Option;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.*;
import org.jline.utils.*;
import org.jline.utils.InfoCmp.Capability;

import org.jline.terminal.TerminalBuilder

sealed interface ReplResult {
  object NoResult: ReplResult
  object ExitMode: ReplResult
  data class Text(val content: String): ReplResult
}

sealed interface Command

data class Task(
  val name: String,
  val description: String,
  val run: (args: List<String>) -> ReplResult): Command

data class Mode(
  val name: String,
  val description: String,
  val exec: (input: String) -> ReplResult): Command

fun main() {
  val tasks = mutableListOf<Task>()
//  val ligature = InMemoryLigature()
//  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
//    routes(ligature)
//  }.start(wait = true)
  val terminal = TerminalBuilder.terminal()
  val reader = LineReaderBuilder.builder()
    .terminal(terminal)
//    .completer(completer)
//    .parser(parser)
    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
    .variable(LineReader.INDENTATION, 2)
    .option(Option.INSERT_BRACKET, true)
    .build()

  var `continue` = true
  val prompt = ">"

  tasks += (Task("exit", "Exit REPL") { args ->
    if (args.size == 0) {
      `continue` = false
      ReplResult.NoResult
    } else {
      ReplResult.Text(":exit takes no arguments.")
    }
  }

  while (`continue`) {
    var line: String? = null
    try {
      line = reader.readLine(prompt)
      if (line.trim() == ":exit") {
        `continue` = false
      } else {
        println(">> $line")
      }
    } catch (e: UserInterruptException) {
      `continue` = false
      // Ignore
    } catch (e: EndOfFileException) {
      println("end of line exception $e")
      return;
    }
  }
}
