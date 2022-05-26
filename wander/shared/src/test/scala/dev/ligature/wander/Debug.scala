/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.Dataset
import dev.ligature.wander.parser.parse
import dev.ligature.wander.lexer.tokenize

@main def hello() = {
  val input = """let hello = () -> Integer { 5 }
                |hello()""".stripMargin
  val tokens = tokenize(input)
  val ast = parse(tokens.getOrElse(???))
  val result = run(input, Dataset.fromString("test").getOrElse(???))

  println("Input")
  println(input)
  println("---")
  println("Tokens")
  println(tokens.getOrElse(???))
  println("---")
  println("AST")
  println(ast.getOrElse(???))
  println("---")
  println("Result")
  println(result.getOrElse(???))
}
