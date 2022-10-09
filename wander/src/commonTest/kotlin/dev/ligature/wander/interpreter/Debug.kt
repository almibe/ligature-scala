/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.getOrElse
import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.parser.parse

fun main() {
  val input = """let identity = (value:Value) -> Value {
                |  value
                |}
                |identity(<testEntity>)""".trimMargin()
  println("Input")
  println(input)
  println("---")
  val tokens = tokenize(input)
  println("Tokens")
  println(tokens.getOrElse { throw Error("Unexpected error.") } )
  println("---")
  val ast = parse(tokens.getOrElse { throw Error("Unexpected error.") } )
  println("AST")
  println(ast.getOrElse { throw Error("Unexpected error.") } )
  println("---")
  //val result = run(input)//, Dataset.create("test").getOrElse { throw Error("Unexpected error.") })
  //println("Result")
  //println(result.getOrElse { throw Error("Unexpected error.") } )
}
