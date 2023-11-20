/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.io.File
import scala.io.Source
import dev.ligature.wander.preludes.common

class ScriptSuite extends munit.FunSuite {
    sys.env.get("WANDER_TEST_SUITE") match {
        case Some(dir) => {
            val files = File(dir).listFiles.filter(_.isFile)
                .filter(_.getName.endsWith(".test.wander"))
                .map(_.getPath).toList
            files.foreach(f => {
                test(f) {
                    val script = Source.fromFile(f).mkString
                    run(script, common()) match {
                        case Right(WanderValue.Array(values)) => {
                            runTests(f, values)
                        }
                        case Left(value) => fail(value.toString())
                        case _ => fail(s"File ${f} is not a valid test file.")
                    }
                }
            })
        }
        case None => ()
    }

    def runTests(fileName: String, values: Seq[WanderValue]) = {
        values.foreach(_ match {
            case record: WanderValue.Record => runTest(record)
            case _ => fail(s"File ${fileName} is not a valid test file.")
        })
    }

    def runTest(record: WanderValue.Record) = {
        val name = readEntry("name", record)
        val result = readEntry("test", record)
        val expected = readEntry("expected", record)
        assertEquals(result, expected, s"${name.toString()} failed.")
    }

    def readEntry(entryName: String, record: WanderValue.Record): WanderValue = {
        record.entires.find((name, value) => {
            name == Name(entryName)
        }) match {
            case None => fail(s"Could not read entry ${entryName}")
            case Some(value) => value._2
        }
    }
}
