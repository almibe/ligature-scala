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

sealed interface ReplResult {
  object NoResult: ReplResult
  data class ExitMode(val message: String): ReplResult
  data class ExitRepl(val message: String): ReplResult
  data class Text(val message: String): ReplResult
  data class ReplError(val message: String): ReplResult
}

sealed interface ModeSwitchResult {
  data class Success(val message: String): ModeSwitchResult
  data class Failure(val message: String): ModeSwitchResult
}

sealed interface Command {
  val name: String
  val description: String
}

/**
 * A Task in Ligature REPL is a function that accepts arguments,
 * runs once, and can print a result to the REPL.
 */
data class Task(
  /**
   * The name used to invoke this task.
   */
  override val name: String,
  /**
   * Description used for help message.
   */
  override val description: String,
  /**
   * Lambda that is run when task is invoked.
   */
  val run: (args: List<String>) -> ReplResult): Command

/**
 * A Mode represents a Task that is run against user input that is not a Command.
 * Only a single Mode is enabled at once.
 */
data class Mode(
  override val name: String,
  val displayPrefix: String,
  override val description: String,
  val init: (args: List<String>) -> ModeSwitchResult,
  val exec: (input: String) -> ReplResult): Command

val defaultMode = Mode(
  "default",
  "",
  "The default mode used to run tasks or enter other modes.",
  { ModeSwitchResult.Success("Entering default mode.") },
  { ReplResult.Text("Enter a valid Command, type :help to see all Commands.") }
)

fun main() {
  var currentMode = defaultMode
  val commands = mutableListOf<Command>()
  val ligatureInstance = InMemoryLigature()
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

  commands.add(defaultMode)
  commands.add(exitTask)
  commands.add(createHelpTask(commands))
  commands.add(createKtsMode())

  fun matchAndExecute(line: String): ReplResult =
    if (line.startsWith(":")) {
      val args = line.split(" ")
      when(val command = commands.find { ":${it.name}" == args.first() }) {
        is Task -> command.run(args.drop(1))
        is Mode -> {
          when (val res = command.init(args.drop(1))) {
            is ModeSwitchResult.Success -> {
              currentMode = command
              ReplResult.Text(res.message)
            }
            is ModeSwitchResult.Failure -> {
              ReplResult.ReplError(res.message)
            }
          }
        }
        null -> ReplResult.ReplError("Command ${args.first()} not found")
      }
    } else {
      currentMode.exec(line)
    }

  var `continue` = true
  while (`continue`) {
    try {
      val line = reader.readLine("${currentMode.displayPrefix}$prompt")
      when (val res = matchAndExecute(line)) {
        is ReplResult.NoResult -> { /* do nothing */ }
        is ReplResult.ExitMode -> currentMode = defaultMode
        is ReplResult.ExitRepl -> {
          `continue` = false
          println(res.message)
        }
        is ReplResult.Text -> println(res.message)
        is ReplResult.ReplError -> println("Error: ${res.message}")
      }
    } catch (e: UserInterruptException) {
      `continue` = false
    } catch (e: EndOfFileException) {
      `continue` = false
    }
  }
}
