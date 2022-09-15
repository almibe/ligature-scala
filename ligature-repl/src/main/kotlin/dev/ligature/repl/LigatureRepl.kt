/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import dev.ligature.inmemory.InMemoryLigature
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReader.Option
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder
import javax.script.ScriptContext
import javax.script.ScriptEngineManager

sealed interface ReplResult {
  object NoResult: ReplResult
  object ExitMode: ReplResult
  data class Text(val content: String): ReplResult
}

sealed interface Command {
  val name: String
}

/**
 * A Task in Ligature REPL is a function that accepts arguments,
 * runs once, and can print a result to the REPL.
 */
data class Task(
  override val name: String,
  val description: String,
  val run: (args: List<String>) -> ReplResult): Command

data class Mode(
  override val name: String,
  val description: String,
  val init: (args: List<String>) -> ReplResult,
  val exec: (input: String) -> ReplResult): Command

val defaultMode = Mode(
  "default",
  "The default mode used to run tasks or enter other modes.",
  { ReplResult.NoResult },
  { ReplResult.Text("Enter a valid Command.") } //TODO maybe output all commands
)

fun main() {
  var currentMode = defaultMode
  val commands = mutableListOf<Command>()
  val inMemoryligature = InMemoryLigature()
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

  commands += (Task("exit", "Exit REPL") { args ->
    if (args.isEmpty()) {
      `continue` = false
      ReplResult.NoResult
    } else {
      ReplResult.Text(":exit takes no arguments.")
    }
  })

  fun matchAndExecute(line: String) {
    if (line.startsWith(":")) {
      val args = line.split(" ")
      when(val command = commands.find { it.name == ":${args.first()}" }) {
        is Task -> TODO()
        is Mode -> TODO()
        null -> TODO()
      }
      TODO("Call init and switch modes")
    } else {
      currentMode.exec(line)
    }
  }

  while (`continue`) {
    var line: String? = null
    try {
      line = reader.readLine(prompt)
      matchAndExecute(line)
    } catch (e: UserInterruptException) {
      `continue` = false
      // Ignore
    } catch (e: EndOfFileException) {
      println("end of line exception $e")
      return;
    }
  }
}

fun run(input: String) {
//  println(ScriptEngineManager().engineFactories)
//
//  val engine = ScriptEngineManager().getEngineByExtension("kts")!!
//
//
//  val bindings = engine.createBindings()
//  bindings["test"] = { 4 }
//
//  engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE)
//
//  print("> ")
////  System.`in`.reader().forEachLine {
//    val res = engine.eval(input)
//    println(res)
////  }

  val engine = ScriptEngineManager().getEngineByExtension("kts")!!
  engine.put("util", LigatureUtil)
  println(engine.eval("kotlinx.coroutines.runBlocking { util.twice(2) }")) //prints 4
}

object LigatureUtil {
  suspend fun twice(i: Int): Int = i * 2
}
