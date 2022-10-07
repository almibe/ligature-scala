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

fun eval(script: List<Element>, bindings: Bindings): Either<EvalError, Value> {
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
    is Element.BooleanLiteral -> Right(Value.BooleanLiteral(element.value))
    is Element.IdentifierLiteral -> Right(Value.IdentifierLiteral(element.value))
    is Element.IntegerLiteral -> Right(Value.IntegerLiteral(element.value))
    is Element.LambdaDefinition -> Right(Value.LambdaDefinition(element.parameters, element.body))
    is Element.Seq -> Right(Value.Seq(element.values))
    is Element.StringLiteral -> Right(Value.StringLiteral(element.value))
    Element.Nothing -> Right(Value.Nothing)
    is Element.Name -> bindings.read(element.name, Value::class)
    is Element.FunctionCall -> {
      TODO()
//      //TODO this should probably handle lambdas and native functions the same.
//      val wanderFunction = bindings.read(element.name, Value.LambdaDefinition::class).orNull()
//      val nativeFunction = bindings.read(element.name, Value.NativeFunction::class).orNull()
//
//      if (wanderFunction != null) {
//        bindings.addScope()
//        eval(element.)
//        //eval function
//        bindings.removeScope()
//        //return value
//        TODO()
//      } else if (nativeFunction != null) {
//        bindings.addScope()
//        eval(element.)
//        //eval function
//        bindings.removeScope()
//        //return value
//        TODO()
//      } else {
//        Left(EvalError("Function not defined ${element.name}"))
//      }
    }
    is Element.IfExpression -> {
      //eval if condition
      //if true run body and return
      //check all elsif conditions
      //if true run body and return
      //if else exists run body and return
      //return nothing if nothing matches
      TODO()
    }
    is Element.Scope -> {
      eval(element.body, bindings)
    }
    is Element.LetStatement -> {
      //eval value expression
      //bind value to name
      //return nothing
      TODO()
    }
  }
