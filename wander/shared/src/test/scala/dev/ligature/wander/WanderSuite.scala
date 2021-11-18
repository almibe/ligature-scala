/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.parse
import dev.ligature.wander.lexer.tokenize
import munit.FunSuite
import java.io.File
import scala.io.Source

class WanderSuite extends FunSuite {
    // test("Wander Token test") {
    //     // testData.foreach { instance =>
    //     //     val tokens = tokenize(instance.script)
    //     //     assertEquals(tokens, instance.tokens, s"tokens are not the same for ${instance.description}")
    //     // }
    // }

    test("Wander AST test") {
        testData.foreach { instance =>
            val ast = parse(instance.script)
            assertEquals(ast, instance.ast, s"AST values are not the same for ${instance.description}")
        }
    }

    // test("Wander Result test") {
    //     // testData.foreach { instance =>
    //     //     val result = run(instance.script)
    //     //     assertEquals(result, instance.result, s"results are not the same for ${instance.description}")
    //     // }
    // }
}
