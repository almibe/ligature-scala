/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.io.File
import scala.io.Source
import dev.ligature.wander.modules.std
import java.nio.file.Path
import dev.ligature.wander.libraries.loadFromPath
import dev.ligature.wander.libraries.DirectoryLibrary

class ScriptSuite extends munit.FunSuite {
  sys.env.get("WANDER_TEST_SUITE") match {
    case Some(dir) =>
      val files = File(dir).listFiles
        .filter(_.isFile)
        .filter(_.getName.endsWith(".test.wander"))
        .map(_.getPath)
        .toList
      files.foreach { f =>
        val script = Source.fromFile(f).mkString
        val library = DirectoryLibrary(Path.of(dir))
        run(script, std(List(library))) match {
          case Left(err) => fail(f.toString() + err.toString())
          case Right((results, _)) =>
            evaluateResults(results, f)
        }
      }
    case None => ()
  }

  def evaluateResults(results: WanderValue, fileName: String) =
    results match
      case WanderValue.Array(tests) =>
        tests.foreach { currentTest =>
          currentTest match
            case WanderValue.Module(values) =>
              test(values(Field("name")).toString) {
                val test = values(Field("test"))
                val expected = values(Field("expect"))
                assertEquals(test, expected)
              }
            case _ => ???
        }
      case _ =>
        throw RuntimeException(s"In $fileName -- Expected result to be array got ${results}")
}
