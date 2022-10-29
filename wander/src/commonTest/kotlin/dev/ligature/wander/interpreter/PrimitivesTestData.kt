/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import arrow.core.getOrElse
import dev.ligature.Identifier
import dev.ligature.wander.model.Element

val primitivesTestData =
    listOf(
        TestInstance(
            description = "true boolean primitive",
            script = "true",
            result = Right(Element.BooleanLiteral(true))),
        TestInstance(
            description = "false boolean primitive",
            script = "false",
            result = Right(Element.BooleanLiteral(false))),
        TestInstance(
            description = "true boolean primitive with trailing whitespace",
            script = "true   ",
            result = Right(Element.BooleanLiteral(true))),
        TestInstance(
            description = "identifier",
            script = "<test>",
            result =
                Right(
                    Element.IdentifierLiteral(
                        Identifier.create("test").getOrElse { throw Error("Unexpected error.") }))),
        TestInstance(
            description = "integer",
            script = "24601",
            result = Right(Element.IntegerLiteral(24601))),
        TestInstance(
            description = "negative integer",
            script = "-111",
            result = Right(Element.IntegerLiteral(-111))),
        TestInstance(
            description = "comment + nothing test",
            script = "--nothing   $newLine",
            result = Right(Element.Nothing)),
        TestInstance(
            description = "string",
            script = "\"hello world\" ",
            result = Right(Element.StringLiteral("hello world"))))
