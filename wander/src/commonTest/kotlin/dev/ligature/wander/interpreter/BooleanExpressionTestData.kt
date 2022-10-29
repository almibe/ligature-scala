/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either.Right
import dev.ligature.wander.model.Element

val booleanExpression =
    listOf(
        TestInstance(
            description = "not function",
            script = "not(true)",
            result = Right(Element.BooleanLiteral(false))),
        TestInstance(
            description = "boolean1 test",
            script = "or(true and(false false))",
            result = Right(Element.BooleanLiteral(true))),
        TestInstance(
            description = "boolean2 test",
            script = "and(or(true false) false)",
            result = Right(Element.BooleanLiteral(false))),
        TestInstance(
            description = "boolean3 test with variables",
            script =
                """let t = not(or(false false))
               |let f = false
               |let res = or(t and(f false))
               |res"""
                    .trimMargin(),
            result = Right(Element.BooleanLiteral(true))))
