/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.wander.WanderError
import dev.ligature.wander.parser.Element

data class EvalError(override val message: String): WanderError

fun interpret(script: List<Element>, bindings: Bindings): Either<EvalError, Value> {
  var lastResult: Value = Value.Nothing
  script.forEach {
    when (val evalResult = eval(it, bindings)) {
      is Right -> lastResult = evalResult.value
      is Left -> return evalResult
    }
  }
  return Right(lastResult)
}

fun eval(element: Element, bindings: Bindings): Either<EvalError, Value> =
  when (element) {
    is Element.Name -> when(val res = bindings.read(element.name)) {
      is Right -> Right(res.value)
      is Left -> res
    }
    else -> TODO()
  }
