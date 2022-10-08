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
    is Element.FunctionCall -> eval(element, bindings)
    is Element.IfExpression -> eval(element, bindings)
    is Element.Scope -> {
      bindings.addScope()
      val res = eval(element.body, bindings)
      bindings.removeScope()
      res
    }
    is Element.LetStatement -> {
      when(val value = eval(element.value, bindings)) {
        is Right -> {
          when(val bindingRes = bindings.bindVariable(element.name, value.value)) {
            is Right -> Right(Value.Nothing)
            is Left -> bindingRes
          }
        }
        is Left -> value
      }
    }
  }

fun eval(element: Element.FunctionCall, bindings: Bindings): Either<EvalError, Value> {
  val fn = bindings.read(element.name, Value.Function::class).orNull()

  return if (fn != null) {
    bindings.addScope()
    if (fn.parameters.size == element.arguments.size) {
      fn.parameters.forEachIndexed { index, param ->
        when (val argRes = eval(element.arguments[index], bindings)) {
          is Right -> {
            bindings.bindVariable(param, argRes.value)
          }
          is Left -> return argRes
        }
      }
      val res = fn.call(bindings)
      bindings.removeScope()
      res
    } else {
      Left(EvalError(
        "Function `${element.name}` expected ${fn.parameters.size} arguments only received ${element.arguments.size}"
      ))
    }
  } else {
    Left(EvalError("Function `${element.name}` not defined."))
  }
}

fun eval(element: Element.IfExpression, bindings: Bindings): Either<EvalError, Value> {
  return when (val ifCondRes = eval(element.ifConditional.condition, bindings)) {
    is Right -> {
      if (ifCondRes.value is Value.BooleanLiteral) {
        if ((ifCondRes.value as Value.BooleanLiteral).value) {
          return eval(element.ifConditional.body, bindings)
        } else {
          element.elsifConditional.forEach {
            when (val elsifCondRes = eval(it.condition, bindings)) {
              is Right -> {
                if (elsifCondRes.value is Value.BooleanLiteral) {
                  if ((elsifCondRes.value as Value.BooleanLiteral).value) {
                    return eval(element.ifConditional.body, bindings)
                  } //else do nothing
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
        Right(Value.Nothing)
      }
    }
    is Left -> ifCondRes
  }
}
