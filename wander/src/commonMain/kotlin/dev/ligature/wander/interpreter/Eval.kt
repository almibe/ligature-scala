/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.wander.WanderError
import dev.ligature.wander.model.Element

data class EvalError(override val userMessage: String) : WanderError

suspend fun eval(script: List<Element>, bindings: Bindings): Either<EvalError, Element> {
  var lastResult: Element = Element.Nothing
  script.forEach {
    when (val evalResult = eval(it, bindings)) {
      is Right -> lastResult = evalResult.value
      is Left -> return evalResult
    }
  }
  return Right(lastResult)
}

suspend fun eval(element: Element, bindings: Bindings): Either<EvalError, Element> =
    when (element) {
      is Element.BooleanLiteral -> Right(element)
      is Element.IdentifierLiteral -> Right(element)
      is Element.IntegerLiteral -> Right(element)
      is Element.LambdaDefinition -> Right(element)
      is Element.Seq -> Right(element)
      is Element.Graph -> Right(element)
      is Element.StringLiteral -> Right(element)
      Element.Nothing -> Right(element)
      is Element.Name -> bindings.read(element.name, Element::class)
      is Element.FunctionCall -> eval(element, bindings)
      is Element.IfExpression -> eval(element, bindings)
      is Element.Scope -> {
        bindings.addScope()
        val res = eval(element.body, bindings)
        bindings.removeScope()
        res
      }
      is Element.LetStatement -> {
        when (val value = eval(element.value, bindings)) {
          is Right -> {
            when (val bindingRes = bindings.bindVariable(element.name, value.value)) {
              is Right -> Right(Element.Nothing)
              is Left -> bindingRes
            }
          }
          is Left -> value
        }
      }
      is Element.NativeFunction -> Right(element)
    }

suspend fun eval(element: Element.FunctionCall, bindings: Bindings): Either<EvalError, Element> {
  val fn = bindings.read(element.name, Element.Function::class).orNull()

  return if (fn != null) {
    val arguments = mutableListOf<Element.Value>()
    //    if (fn.parameters.size == element.arguments.size) {
    element.arguments.forEach { argument ->
      when (val argRes = eval(argument, bindings)) {
        is Right -> {
          arguments.add(argRes.value as Element.Value)
        }
        is Left -> return argRes
      }
    }
    val res = fn.call(arguments, bindings)
    res
    //    } else {
    //      Left(EvalError(
    //        "Function `${element.name}` expected ${fn.parameters.size} arguments only received
    // ${element.arguments.size}"
    //      ))
    //    }
  } else {
    Left(EvalError("Function `${element.name}` not defined."))
  }
}

suspend fun eval(element: Element.IfExpression, bindings: Bindings): Either<EvalError, Element> {
  return when (val ifCondRes = eval(element.ifConditional.condition, bindings)) {
    is Right -> {
      if (ifCondRes.value is Element.BooleanLiteral) {
        if ((ifCondRes.value as Element.BooleanLiteral).value) {
          return eval(element.ifConditional.body, bindings)
        } else {
          element.elsifConditional.forEach {
            when (val elsifCondRes = eval(it.condition, bindings)) {
              is Right -> {
                if (elsifCondRes.value is Element.BooleanLiteral) {
                  if ((elsifCondRes.value as Element.BooleanLiteral).value) {
                    return eval(element.ifConditional.body, bindings)
                  } // else do nothing
                } else {
                  return Left(EvalError("Elsif condition must result in a Boolean value."))
                }
              }
              is Left -> return elsifCondRes
            }
          }
        }
      } else {
        Left(EvalError("If condition must result in a Boolean value."))
      }
      if (element.elseBody != null) {
        eval(element.elseBody, bindings)
      } else {
        Right(Element.Nothing)
      }
    }
    is Left -> ifCondRes
  }
}
