/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import dev.ligature.wander.tokenize
//import dev.ligature.wander.preludes.common

@main def wanderDebug() = {
  val input = """let identity = (value:Value) -> Value {
                |  value
                |}
                |identity(<testEntity>)""".stripMargin
  println("Input")
  println(input)
  println("---")
  val tokens = tokenize(input)
  println("Tokens")
  println(tokens.getOrElse(???))
  println("---")
  val ast = parse(tokens.getOrElse(???))
  println("AST")
  println(ast.getOrElse(???))
  println("---")
//  val result = run(input, common())
//  println("Result")
  // println(result.getOrElse(???))
}
