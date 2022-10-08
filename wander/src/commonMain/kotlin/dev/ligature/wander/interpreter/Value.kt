/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.Identifier
import dev.ligature.wander.parser.Element

/**
 * A Value represents a result from eval-ing an Element or a value that can be bound.
 */
sealed interface Value {
  data class BooleanLiteral(val value: Boolean): Value
  data class StringLiteral(val value: String): Value
  data class IntegerLiteral(val value: Long): Value
  data class IdentifierLiteral(val value: Identifier): Value
  sealed interface Function: Value {
    val parameters: List<String>
    fun call(bindings: Bindings): Either<EvalError, Value>
  }
  data class LambdaDefinition(override val parameters: List<String>,
                              val body: List<Element>): Function {
    override fun call(bindings: Bindings): Either<EvalError, Value> =
      eval(body, bindings)
  }
  data class NativeFunction(override val parameters: List<String>,
                            val body: (Bindings) -> Either<EvalError, Value>): Function {
    override fun call(bindings: Bindings): Either<EvalError, Value> =
      body(bindings)
  }
  data class Seq(val values: List<Element.Expression>): Value
  object Nothing: Value
}

fun write(value: Value): String =
  when (value) {
    is Value.BooleanLiteral -> value.value.toString()
    is Value.IdentifierLiteral -> value.value.toString() //TODO probably not correct
    is Value.IntegerLiteral -> value.value.toString()
    is Value.LambdaDefinition -> "Lambda"
    is Value.NativeFunction -> "Native Function"
    Value.Nothing -> "nothing"
    is Value.Seq -> {
      val sb = StringBuilder("[")
      value.values.forEach { _ ->
        sb.append(".")
      }
      sb.append("]")
      sb.toString()
    }
    is Value.StringLiteral -> value.value
  }
