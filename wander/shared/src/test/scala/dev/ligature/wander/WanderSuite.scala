/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.parse
import dev.ligature.wander.lexer.tokenize
import munit.FunSuite

class WanderSuite extends FunSuite {
  val testLexer = true
  val testParser = false
  val testInterpreter = false
  val testOnly: Set[String] = Set() //if set is empty all tests will run
  def runTest(description: String): Boolean =
    testOnly.isEmpty || testOnly.contains(description)

  testData.foreach { testGroup =>
    testGroup.testInstances.foreach { instance =>
      if (testLexer && instance.tokens != null && runTest(instance.description)) {
        test(s"Lexing -- ${testGroup.category} -- ${instance.description}") {
          val tokens = tokenize(instance.script)
          assertEquals(
            tokens,
            Right(instance.tokens),
            s"tokens are not the same for ${instance.description}"
          )
        }
      }

      if (testParser && instance.ast != null && runTest(instance.description)) {
        test(s"Parsing -- ${testGroup.category} -- ${instance.description}") {
          val tokens = tokenize(instance.script)
          tokens match {
            case Left(err) => fail(s"tokenizer failed - ${err.message}")
            case Right(tokens) => {
              val ast = parse(tokens)
              assertEquals(
                ast,
                Right(instance.ast),
                s"AST values are not the same for ${instance.description}"
              )
            }
          }
        }
      }

      if (testInterpreter && runTest(instance.description)) {
        test(
          s"Interpreting -- ${testGroup.category} -- ${instance.description}"
        ) {
          val result = run(instance.script, testGroup.dataset)
          assertEquals(
            result,
            instance.result,
            s"results are not the same for ${instance.description}"
          )
        }
      }
    }
  }
}
