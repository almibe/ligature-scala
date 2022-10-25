/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either
import arrow.core.getOrElse
import dev.ligature.Identifier
import dev.ligature.Statement
import dev.ligature.wander.interpreter.TestInstance
import dev.ligature.wander.model.Element

val commonLib = listOf(
  TestInstance(
    description = "create empty graph",
    script = "graph()",
    result = Either.Right(Element.Graph())
  ),
  TestInstance(
    description = "create graph with 1 Statement",
    script = "graph([[<a> <b> <c>]])",
    result = Either.Right(Element.Graph(mutableSetOf(Statement(id("a"), id("b"), id("c")))))
  ),
  TestInstance(
    description = "create graph with multiple Statements",
    script = "graph([[<a> <b> <c>][<b> <c> <d>][<c> <d> <e>]])",
    result = Either.Right(Element.Graph(mutableSetOf(
      Statement(id("a"), id("b"), id("c")),
      Statement(id("b"), id("c"), id("d")),
      Statement(id("c"), id("d"), id("e")),
    )))
  ),
)

fun id(id: String): Identifier = Identifier.create(id).getOrElse { TODO() }