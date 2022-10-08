/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import dev.ligature.wander.model.Element

val ifExpression = listOf(
  TestInstance(
    description = "if true",
    script = """if true {
               |  7
               |}""".trimMargin(),
    result = Right(Element.IntegerLiteral(7))
  ),
  TestInstance(
    description = "if false",
    script = "if and(false true) { 24601 }",
    result = Right(Element.Nothing)
  ),
  TestInstance(
    description = "if else",
    script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if x {
               |    2
               |} else if false {
               |    3    
               |} else {
               |    4
               |}""".trimMargin(),
    result = Right(Element.IntegerLiteral(2))
  ),
  TestInstance(
    description = "else",
    script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if not(x) {
               |    2
               |} else {
               |    3
               |}""".trimMargin(),
    result = Right(Element.IntegerLiteral(3))
  )
)
