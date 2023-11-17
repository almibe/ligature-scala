/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.io.File
import scala.io.Source
import dev.ligature.wander.preludes.common

class ScriptSuite extends munit.FunSuite {
    // val dir = sys.env("WANDER_TEST_SUITE")
    // val files = File(dir).listFiles.filter(_.isFile)
    //     .filter(_.getName.endsWith(".test.wander"))
    //     .map(_.getPath).toList
    // files.foreach(f => {
    //     test(f) {
    //         val script = Source.fromFile(f).mkString
    //         run(script, common()) match {
    //             case Left(value) => fail(value.toString())
    //             case Right(value) => ()
    //         }
    //     }
    // })
}
