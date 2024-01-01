/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.io.File
import scala.io.Source
import dev.ligature.wander.libraries.std
import dev.ligature.wander.libraries.loadFromPath
import java.nio.file.Path

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
        loadFromPath(Path.of(dir), std()) match
          case Left(value) => ???
          case Right(environment) =>
            run(script, environment) match {
              case Left(err) => fail(err.toString())
              case Right((results, _)) =>
                evaluateResults(results)
            }
      }
    case None => ()
  }

  def evaluateResults(results: WanderValue) =
    results match
      case WanderValue.Nothing => ()
      case WanderValue.Array(tests) =>
        tests.foreach { currentTest =>
          currentTest match
            case WanderValue.Record(values) =>
              test(values(Name("name")).toString) {
                val test = values(Name("test"))
                val expected = values(Name("expect"))
                assertEquals(test, expected)
              }
            case _ => ???
        }
      case _ => ???
}
