/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either
import dev.ligature.*
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.model.Element
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

suspend fun datasetQueryBindings(
    queryTx: QueryTx,
    dataset: Dataset,
    bindings: Bindings = common()
): Bindings {
  bindings.bindVariable(
      "find",
      Element.NativeFunction(listOf()) { arguments, bindings ->
        val res =
            queryTx
                .allStatements()
                .map {
                  Element.Seq(
                      listOf(
                          Element.IdentifierLiteral(it.entity),
                          Element.IdentifierLiteral(it.attribute),
                          it.value.toElement()))
                }
                .toList()
        Either.Right(Element.Seq(res))
      })
  return bindings
}

fun Value.toElement(): Element.Value =
    when (this) {
      is Identifier -> Element.IdentifierLiteral(this)
      is BytesLiteral -> TODO()
      is IntegerLiteral -> Element.IntegerLiteral(this.value)
      is StringLiteral -> Element.StringLiteral(this.value)
    }
