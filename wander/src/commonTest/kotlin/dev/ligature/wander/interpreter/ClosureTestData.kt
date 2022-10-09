/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.Either.Right
import dev.ligature.Identifier
import dev.ligature.wander.model.Element

val closureTestData = listOf(
  TestInstance(
    description = "function0 def",
    script = """let f = { -> 5 }
               |f()""".trimMargin(),
    result = Right(Element.IntegerLiteral(5))
  ),
  TestInstance(
    description = "function0 def with closing over variable",
    script = """let x = 5
               |let f = { -> x }
               |f()""".trimMargin(),
    result = Right(Element.IntegerLiteral(5))
  ),
  TestInstance(
    description = "function1 def",
    script = """let identity = { identifier ->
               |  identifier
               |}
               |identity(<testEntity>)""".trimMargin(),
    result = Right(
      Element.IdentifierLiteral(Identifier.create("testEntity")
        .getOrElse { throw Error("Unexpected value.")}))
  ),
  TestInstance(
    description = "function2 def",
    script = """let second = { value1 value2 ->
               |  value2
               |}
               |second(<testEntity> "hello")""".trimMargin(),
    result = Right(Element.StringLiteral("hello"))
  ),
  TestInstance(
    description = "function3 def",
    script = """let middle = { value1 value2 value3 ->
               |  value2
               |}
               |middle(<testEntity> "hello" 24601)""".trimMargin(),
    result = Right(Element.StringLiteral("hello"))
  ),
  TestInstance(
    description = """allow "method call" syntax 1 arg""",
    script = """let id = {x -> x}
               |24601.id()""".trimMargin(),
    result = Right(Element.IntegerLiteral(24601))
  ),
  TestInstance(
    description = """allow "method call" syntax 2 args""",
    script = """let second = {x y -> y}
               |24601.second(42)""".trimMargin(),
    result = Right(Element.IntegerLiteral(42))
  ),
  TestInstance(
    description = """allow "method call" syntax 3 args""",
    script = """let middle = {x y z -> y}
               |24601.middle("hello" <world>)""".trimMargin(),
    result = Right(Element.StringLiteral("hello"))
  )
)
