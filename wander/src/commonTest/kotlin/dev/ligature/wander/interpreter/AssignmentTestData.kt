/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import dev.ligature.wander.model.Element

val assignmentTestData = listOf(
  TestInstance(
    description = "basic let",
    script = "let x = 5",
    result = Right(Element.Nothing)
  ),
  TestInstance(
    description = "make sure keyword parser is greedy",
    script = "let trued = true",
    result = Right(Element.Nothing)
  ),
  TestInstance(
    description = "let with result",
    script = "let hello = 5\nhello",
    result = Right(Element.IntegerLiteral(5))
  ),
  TestInstance(
    description = "basic scope",
    script = """{
               |  let x = 7
               |  x
               |}""".trimMargin(),
    result = Right(Element.IntegerLiteral(7))
  ),
  TestInstance(
    description = "scope shadowing",
    script = """let x = 5
               |{
               |  let x = 7
               |  x
               |}""".trimMargin(),
    result = Right(Element.IntegerLiteral(7))
  )
)
