/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.parse
import dev.ligature.wander.lexer.tokenize
import munit.FunSuite

class WanderSuite extends FunSuite {
  primitivesTestData.foreach { instance =>
    test(s"Lexing -- ${instance.description}") {
      val tokens = tokenize(instance.script)
      assertEquals(
        tokens,
        Right(instance.tokens),
        s"tokens are not the same for ${instance.description}"
      )
    }

    test(s"Parsing -- ${instance.description}") {
      val tokens = tokenize(instance.script)
      tokens match {
        case Left(err) => fail(s"tokenizer failed - ${err.message}")
        case Right(tokens) => {
          val ast = parse(tokens)
          assertEquals(
            ast,
            instance.ast,
            s"AST values are not the same for ${instance.description}"
          )
        }
      }
    }
  }

  // test("Wander Result test") {
  //     // testData.foreach { instance =>
  //     //     val result = run(instance.script)
  //     //     assertEquals(result, instance.result, s"results are not the same for ${instance.description}")
  //     // }
  // }
}
