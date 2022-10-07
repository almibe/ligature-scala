/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import arrow.core.getOrElse
import dev.ligature.Identifier

val primitivesTestData = listOf(
  TestInstance(
    description = "true boolean primitive",
    script = "true",
    result = Right(Value.BooleanLiteral(true))
  ),
  TestInstance(
    description = "false boolean primitive",
    script = "false",
    result = Right(Value.BooleanLiteral(false))
  ),
  TestInstance(
    description = "true boolean primitive with trailing whitespace",
    script = "true   ",
    result = Right(Value.BooleanLiteral(true))
  ),
  TestInstance(
    description = "identifier",
    script = "<test>",
    result = Right(
      Value.IdentifierLiteral(Identifier.create("test")
        .getOrElse { throw Error("Unexpected error.")})
    )
  ),
  TestInstance(
    description = "integer",
    script = "24601",
    result = Right(Value.IntegerLiteral(24601))
  ),
  TestInstance(
    description = "negative integer",
    script = "-111",
    result = Right(Value.IntegerLiteral(-111))
  ),
  TestInstance(
    description = "comment + nothing test",
    script = "--nothing   $newLine",
    result = Right(Value.Nothing)
  ),
  TestInstance(
    description = "string",
    script = "\"hello world\" ",
    result = Right(Value.StringLiteral("hello world"))
  )
)
