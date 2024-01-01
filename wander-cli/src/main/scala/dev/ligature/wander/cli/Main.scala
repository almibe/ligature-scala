/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.cli

import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import dev.ligature.wander.*
import dev.ligature.wander.libraries.*
import java.nio.file.Path
import scala.collection.mutable.*
import com.github.freva.asciitable.*
import io.AnsiColor._
import scala.jdk.CollectionConverters.IteratorHasAsScala

object WanderCli {
  import scala.util.CommandLineParser
  def main(args: Array[String]): Unit =
    try
      val filename = CommandLineParser.parseArgument[String](args, 0)
      val isTest = filename.endsWith(".test.wander")
      if filename == "allTests" then runAllTests()
      else if Files.exists(Paths.get(filename)) then
        val content = scala.io.Source.fromFile(filename).mkString
        runScript(content, isTest)
      else if sys.env.contains("WANDER_LIBS") then
        val libsDir = sys.env("WANDER_LIBS")
        val libsFilename = libsDir + File.separator + filename
        if Files.exists(Paths.get(libsFilename)) then
          val file = scala.io.Source.fromFile(libsFilename)
          val content = file.mkString
          runScript(content, isTest)
        else println(s"Error: $libsFilename doesn't exist.")
      else println(s"Error: File $filename doesn't exist.")
    catch {
      case ex => ex.printStackTrace()
    }
}

def runScript(script: String, isTest: Boolean) =
  val environment = if sys.env.contains("WANDER_LIBs") then
    val libsDir = sys.env("WANDER_LIBS")
    loadFromPath(Path.of(libsDir), std()) match
      case Left(_)            => ???
      case Right(environment) => environment
  else std()
  run(script, environment) match
    case Left(value) => println(s"Error: ${value.userMessage}")
    case Right((value, _)) =>
      if isTest then runTest(value)
      else println(printWanderValue(value))

def runAllTests() = {
  val path = Path.of(sys.env("WANDER_TEST_SUITE"))
  Files
    .walk(path)
    .iterator()
    .asScala
    .filter(Files.isRegularFile(_))
    .filter(_.getFileName().toString().endsWith(".test.wander"))
    .foreach { file =>
      val content = scala.io.Source.fromFile(file.toFile).mkString
      runScript(content, true)
    }
}

def runTest(value: WanderValue) = {
  val headers: Array[String] = Array("Name", "Result", "Cause")
  val data: ArrayBuffer[Array[Object]] = ArrayBuffer()
  val colors: ListBuffer[String] = ListBuffer()
  value match
    case WanderValue.Nothing => ()
    case WanderValue.Array(values) =>
      values.foreach { testValue =>
        testValue match
          case WanderValue.Record(testRecord) =>
            val name = testRecord(Name("name"))
            val test = testRecord(Name("test"))
            val expected = testRecord(Name("expect"))
            val passed = if test == expected then "pass" else "fail"
            val color = if passed == "pass" then GREEN else RED
            colors += color
            data += Array(printWanderValue(name), passed, "--")
          case _ => ???
      }
      println(
        AsciiTable
          .builder()
          .border(AsciiTable.NO_BORDERS)
          .header("Name", "Result", "Cause")
          .data(data.toArray)
          .styler(makeStyler(colors.toList))
          .asString()
      )
    case e =>
      throw RuntimeException(s"Not expecting input: $e")
}

def makeStyler(colors: List[String]): Styler =
  new Styler {
    override def styleCell(
        column: com.github.freva.asciitable.Column,
        row: Int,
        col: Int,
        data: java.util.List[String]
    ): java.util.List[String] = {
      data.set(0, s"${colors(row)}${data.get(0)}$WHITE")
      data
    }
  }

def processTestResults(results: WanderValue.Record) =
  ???
