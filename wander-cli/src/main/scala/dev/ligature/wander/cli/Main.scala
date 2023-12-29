/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.cli

import java.nio.file.Files
import java.nio.file.Paths
import java.io.File

object WanderCli {
  import scala.util.CommandLineParser
  def main(args: Array[String]): Unit =
    println(sys.env("WANDER_LIBS"))
    try
      val filename = CommandLineParser.parseArgument[String](args, 0)
      if Files.exists(Paths.get(filename)) then
        val file = scala.io.Source.fromFile(filename)
        val content = file.mkString
        println(content)
      else if sys.env.contains("WANDER_LIBS") then
        val libsDir = sys.env("WANDER_LIBS")
        val libsFilename = libsDir + File.separator + filename
        if Files.exists(Paths.get(libsFilename)) then
          val file = scala.io.Source.fromFile(libsFilename)
          val content = file.mkString
          println(content)
        else
          println(s"Error: $libsFilename doesn't exist.")
      else
        println(s"Error: File $filename doesn't exist.")
    catch {
      case _ => println("Requires single file name.")
    }
}
