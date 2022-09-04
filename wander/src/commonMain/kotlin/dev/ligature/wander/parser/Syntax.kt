/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.Bindings
import dev.ligature.wander.interpreter.ScriptError
import dev.ligature.Statement
import dev.ligature.Value
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap
import arrow.core.Option

/** Represents the union of Statements and Expressions
  */
sealed interface Element {
  fun eval(bindings: Bindings): Either<ScriptError, EvalResult>
}

/** An element of a Wander program that can be evaluated for a value.
  */
sealed interface Expression: Element

/** Represents a Value in the Wander language.
  */
sealed interface WanderValue: Expression

data class ScriptResult(val result: WanderValue)
data class EvalResult(val bindings: Bindings, val result: WanderValue)

/** Represents a Name in the Wander language.
  */
data class Name(val name: String): Expression {
  override fun eval(bindings: Bindings) =
    when(val res = bindings.read(this)) {
      is Either.Left  -> res
      is Either.Right -> Either.Right(EvalResult(bindings, res.value))
    }
}

sealed class FunctionDefinition(val parameters: List<Parameter>): WanderValue

data class LigatureValue(val value: Value): WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(
    EvalResult(bindings, LigatureValue(value))
  )
}

data class BooleanValue(val value: Boolean): WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(
    EvalResult(bindings, BooleanValue(value))
  )
}

//TODO is this needed?
data class StatementValue(val value: Statement): WanderValue {
  override fun eval(binding: Bindings) = TODO()
}

object Nothing: WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(EvalResult(bindings, Nothing))
}

//TODO add back
//data class ResultStream(stream: Stream[IO, WanderValue]) extends WanderValue {
//  override def eval(binding: Bindings) = ???
//}

object EqualSign: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval equal sign.")
  )
}

object OpenBrace: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval open brace.")
  )
}

object CloseBrace: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval close brace.")
  )
}

object OpenParen: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval open paren.")
  )
}

object CloseParen: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval close paren.")
  )
}

object Arrow: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval arrow.")
  )
}

object Colon: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval colon.")
  )
}

object LetKeyword: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval let keyword.")
  )
}

data class LetStatement(val name: Name, val expression: Expression): Element {

  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
    return when(val result = this.expression.eval(bindings)) {
      is Either.Left  -> result
      is Either.Right -> {
        when(val res = bindings.bindVariable(this.name, result.value.result)) {
          is Either.Left  -> res
          is Either.Right -> Either.Right(EvalResult(res.value, Nothing))
        }
      }
    }
  }
}

/** Holds a reference to a function defined in Kotlin that can be called from
  * Wander.
  */
data class NativeFunction(
    val nativeParameters: List<Parameter>,
    val body: (bindings: Bindings) -> Either<ScriptError, WanderValue>,
//    val output: WanderType? = null
): FunctionDefinition(nativeParameters) { // TODO eventually remove the default null value
  override fun eval(binding: Bindings) =
    // body(binding) match {
    //   case Left(err) => Left(err)
    //   case Right(res) => Right(EvalResult(binding, res))
    // }
    Either.Right(EvalResult(binding, this))
}

/** Represents a full script that can be eval'd.
  */
data class Script(val elements: List<Element>) {
  fun eval(bindings: Bindings): Either<ScriptError, ScriptResult> {
    var result: WanderValue = Nothing
    var currentBindings: Bindings = bindings
    elements.forEach { element ->
      when(val res = element.eval(currentBindings)) {
        is Either.Left  -> return res
        is Either.Right -> {
          result = res.value.result
          currentBindings = res.value.bindings
        }
      }
    }
    return Either.Right(ScriptResult(result))
  }
}

/** Represents a scope in Wander that can be eval'd and can contain its own
  * bindings.
  */
data class Scope(val elements: List<Element>): Expression {
  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
    var currentBindings = bindings.newScope()
    var result: WanderValue = Nothing
    elements.forEach { element ->
      when (val res = element.eval(currentBindings)) {
        is Either.Left  -> return res
        is Either.Right -> {
          result = res.value.result
          currentBindings = res.value.bindings
        }
      }
    }
    return Either.Right(EvalResult(bindings, result))
  }
}

//sealed interface WanderType
//
//enum class SimpleType: WanderType {
//  Value,
//  Identifier,
//  Boolean,
//  String,
//  Integer
//}
//data class Function(val parameters: List<Parameter>, val output: WanderType): WanderType

data class Parameter(
    val name: Name,
//    val parameterType: WanderType
): Expression {
  override fun eval(bindings: Bindings) = TODO()
}

/** Holds a reference to a function defined in Wander.
  */
data class WanderFunction(
    val wanderParameters: List<Parameter>,
//    val output: WanderType,
    val body: Expression
): FunctionDefinition(wanderParameters) {
  override fun eval(bindings: Bindings) =
    Either.Right(EvalResult(bindings, this))
}

data class FunctionCall(val name: Name, val parameters: List<Expression>): Expression {
  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> =
    bindings.read(name).flatMap { value: WanderValue ->
      when(value) {
        is WanderFunction -> {
          val functionCallBindings = updateFunctionCallBindings(bindings, value.parameters)
          value.body.eval(functionCallBindings)
        }
        is NativeFunction -> {
          val functionCallBindings = updateFunctionCallBindings(bindings, value.parameters)
          val res = value.body(functionCallBindings)
          res.flatMap { Right(EvalResult(bindings, it)) }
        }
        else -> Either.Left(ScriptError("${name.name} is not a function."))
      }
    }

  // TODO: this function should probably return an Either instead of throwing an exception
  fun updateFunctionCallBindings(
      bindings: Bindings,
      args: List<Parameter>
  ): Bindings =
    if (args.size == parameters.size) {
      var currentBindings = bindings.newScope()
      for (i in args.indices) {
        val arg = args[i]
        val param = parameters[i]
        val paramRes = param.eval(currentBindings)//.getOrElse(TODO())
        if (paramRes.isNotEmpty()) {
          when(val res = currentBindings.bindVariable(arg.name, (paramRes.orNull()!!).result)) {
            is Either.Left  -> TODO()//return res
            is Either.Right -> currentBindings = res.value
          }
        } else {
          TODO()
        }
      }
      currentBindings
    } else {
      throw RuntimeException(
        "Argument number ${args.size} != Parameter number ${parameters.size}"
      )
    }
}

//TODO is this needed?
data class ReferenceExpression(val name: Name): Expression {
  override fun eval(bindings: Bindings) =
    TODO()
}

data class IfExpression(
    val condition: Expression,
    val body: Expression,
    val elseIfs: List<ElseIf> = listOf(),
    val `else`: Option<Else> = none()
): Expression {
  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
    fun evalCondition(expression: Expression): Either<ScriptError, Boolean> {
      TODO()
//      when(val res = condition.eval(bindings)) {
//        is Either.Right(EvalResult(_, BooleanValue(res))) -> res
//        is Either.Left -> return res
//        case _ =>
//          return Either.Left(
//          ScriptError("Conditions in if expression must return BooleanValue.")
//        )
//      }

//      elseIf.condition.eval(bindings) match {
//        case Right(EvalResult(_, BooleanValue(res))) => res
//        case Left(err)                               => return Left(err)
//        case _ =>
//        return Left(
//          ScriptError(
//            "Conditions in if expression must return BooleanValue."
//          )
//        )
//      }
    }
    if (evalCondition(condition).orNull() == true) {
      body.eval(bindings)
    } else {
      for (elseIf in elseIfs) {
        if (evalCondition(elseIf.condition).orNull() == true) {
          return elseIf.body.eval(bindings)
        }
      }
      return when(`else`) {
        is Some -> `else`.value.body.eval(bindings)
        is None -> Either.Right(EvalResult(bindings, Nothing))
      }
    }
    return Either.Right(EvalResult(bindings, Nothing))
  }
}

data class ElseIf(val condition: Expression, val body: Expression): Expression {
  override fun eval(bindings: Bindings) = TODO("is this needed?")
}

data class Else(val body: Expression): Expression {
  override fun eval(bindings: Bindings) = TODO("is this needed?")
}
