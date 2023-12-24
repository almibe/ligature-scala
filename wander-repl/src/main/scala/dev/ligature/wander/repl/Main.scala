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
import dev.ligature.wander.zeromq.runServer
import dev.ligature.inmemory.LigatureInMemory
import dev.ligature.wander.ligature.ligatureEnvironment
import scala.io.Source
import scala.collection.mutable
import java.nio.file.Path

case class Command(
  val name: String,
  val description: String,
  val commands: Seq[String],
  val performAction: (arg: String) => CommandResult)

case class CommandResult(
  val newEnvironment: Option[Environment] = None,
  val continue: Boolean = true,
)

val commands = Seq(
  Command(
    "Quit", 
    "Quit the REPL.", 
    Seq(":quit", ":q"), 
    (_) => 
      println("Bye!")
      CommandResult(continue = false)),
  Command(
    "Run", 
    "Run an external script.", 
    Seq(":run", ":r"), 
    (fileName) =>
      try {
        runFile(fileName.trim())
      } catch {
        case _ => println("Could not load script.")
      }
      CommandResult()
    )
)

def runFile(fileName: String) = {
  val bufferedSource = Source.fromFile(fileName.trim())
  val sb = mutable.StringBuilder()
  for (line <- bufferedSource.getLines) {
    sb.append(line + "\n")
  }
  bufferedSource.close
  val lastResult = run(sb.toString(), common())
  println(printResult(lastResult))
}

@main def main(script: String*) =
  script.asInstanceOf[List[String]] match
    case List() => 
      val path = Path.of(s"${System.getProperty("user.home")}${System.getProperty("file.separator")}.ligature")
      runServer(4200)
      val terminal: Terminal = TerminalBuilder.builder().dumb(true).build()
      val parser: DefaultParser = new DefaultParser()
      val reader: LineReader = LineReaderBuilder
        .builder()
        .terminal(terminal)
        .parser(parser)
        .build()
      var continue = true
      var environment: Environment = ligatureEnvironment(LigatureInMemory())
      while (continue) {
        val script = reader.readLine("> ")
        val result = if script.trim().startsWith(":") then
          val command = script.split(" ").headOption
          val arg = command match
            case None => ""
            case Some(command) => script.subSequence(command.size, script.size)
          runCommand(script, arg.toString())
        else
          runScript(script, environment)
        if result.newEnvironment.isDefined then
          environment = result.newEnvironment.getOrElse(???)
        continue = result.continue
      }
      terminal.close()
    case List(fileName) => runFile(fileName)
    case _ => ???

def runCommand(userCommand: String, arg: String): CommandResult =
  commands.find(command => command.commands.contains(userCommand)) match {
    case None =>
      println(s"Could not find command - $userCommand.")
      CommandResult()
    case Some(command) =>
      command.performAction(arg)
  }

def runScript(script:String, environment: Environment): CommandResult =
  val intro = introspect(script)
  val lastResult = run(script, environment)
  println(printResult(lastResult))
  if lastResult.isRight then
    CommandResult(newEnvironment = Some(lastResult.getOrElse(???)._2))
  else 
    CommandResult()
