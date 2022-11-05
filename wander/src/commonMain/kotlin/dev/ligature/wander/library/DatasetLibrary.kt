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
        if (arguments.size <= 3) {
          val entity: Identifier? = arguments.getOrNull(0).let {
            when(it) {
              is Element.IdentifierLiteral -> it.value
              is Element.Nothing -> null
              null -> null
              else -> TODO("Return error")
            }
          }
          val attribute: Identifier? = arguments.getOrNull(1).let {
            when(it) {
              is Element.IdentifierLiteral -> it.value
              is Element.Nothing -> null
              null -> null
              else -> TODO("Return error")
            }
          }
          val value: Value? = arguments.getOrNull(2).let {
            when (it) {
              is Element.Nothing -> null
              null -> null
              is Element.IdentifierLiteral -> it.value
              is Element.IntegerLiteral -> IntegerLiteral(it.value)
              is Element.StringLiteral -> StringLiteral(it.value)
              is Element.Seq -> TODO("error")
              is Element.BooleanLiteral -> TODO("error")
              is Element.LambdaDefinition -> TODO("error")
              is Element.NativeFunction -> TODO("error")
              is Element.Graph -> TODO("error")
            }
          }
          val res =
              queryTx
                  .matchStatements(entity, attribute, value)
                  .map {
                    Element.Seq(
                        listOf(
                            Element.IdentifierLiteral(it.entity),
                            Element.IdentifierLiteral(it.attribute),
                            it.value.toElement()))
                  }
                  .toList()
          Either.Right(Element.Seq(res))
        } else {
          TODO()
        }
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
