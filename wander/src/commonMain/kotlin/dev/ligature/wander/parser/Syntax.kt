/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.interpreter.ScriptError
import dev.ligature.Statement
import dev.ligature.Value
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap

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

fun write(wanderValue: WanderValue): String =
  when (wanderValue) {
    is Seq -> {
//      val sb = StringBuilder("[")
//      wanderValue.contents.forEach {
//        //sb.append(" ${write(it)}")
//      }
//      sb.append("]")
//      sb.toString()
      "Sequence"
    }
    is BooleanValue -> wanderValue.value.toString()
    is NativeFunction -> "Native Function"
    is WanderFunction -> "Wander Function"
    is LigatureValue -> dev.ligature.lig.writeValue(wanderValue.value)
    Nothing -> "nothing"
    is StatementValue -> dev.ligature.lig.writeStatement(wanderValue.value)
  }

data class ScriptResult(val result: WanderValue) {
  fun printResult(): String {
    return write(result)
  }
}
data class EvalResult(val result: WanderValue)

/** Represents a Name in the Wander language.
  */
data class Name(val name: String): Expression {
  override fun eval(bindings: Bindings) =
    when(val res = bindings.read(this)) {
      is Either.Left  -> res
      is Either.Right -> Either.Right(EvalResult(res.value))
    }
}

//TODO probably replace type of contents with Flow<Expression>
data class Seq(val contents: List<Expression> = listOf()): WanderValue {
  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> =
    Right(EvalResult(this))
}

sealed class FunctionDefinition(val parameters: List<Parameter>): WanderValue

data class LigatureValue(val value: Value): WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(
    EvalResult(LigatureValue(value))
  )
}

data class BooleanValue(val value: Boolean): WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(
    EvalResult(BooleanValue(value))
  )
}

//TODO is this needed?
data class StatementValue(val value: Statement): WanderValue {
  override fun eval(binding: Bindings) = TODO()
}

object Nothing: WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(EvalResult(Nothing))
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

object OpenSquare: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval `[`.")
  )
}

object CloseSquare: Element {
  override fun eval(binding: Bindings) = Either.Left(
    ScriptError("Cannot eval `]`.")
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
          is Either.Right -> Either.Right(EvalResult(Nothing))
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
    //   case Right(res) => Right(EvalResult(res))
    // }
    Either.Right(EvalResult(this))
}

/** Represents a full script that can be eval'd.
  */
data class Script(val elements: List<Element>) {
  fun eval(bindings: Bindings): Either<ScriptError, ScriptResult> {
    var result: WanderValue = Nothing
    elements.forEach { element ->
      when(val res = element.eval(bindings)) {
        is Either.Left  -> return res
        is Either.Right -> {
          result = res.value.result
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
    bindings.addScope()//.newScope() TODO fix
    var result: WanderValue = Nothing
    elements.forEach { element ->
      when (val res = element.eval(bindings)) {
        is Either.Left  -> return res
        is Either.Right -> {
          result = res.value.result
        }
      }
    }
    bindings.removeScope()
    return Either.Right(EvalResult(result))
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
    val body: Scope
): FunctionDefinition(wanderParameters) {
  override fun eval(bindings: Bindings) =
    Either.Right(EvalResult(this))
}

data class FunctionCall(val name: Name, val parameters: List<Expression>): Expression {
  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> =
    bindings.read(name).flatMap { value: WanderValue ->
      when(value) {
        is WanderFunction -> {
          updateFunctionCallBindings(bindings, value.parameters)
          val res = value.body.eval(bindings)
          bindings.removeScope()
          res
        }
        is NativeFunction -> {
          updateFunctionCallBindings(bindings, value.parameters)
          val res = value.body(bindings)
          bindings.removeScope()
          res.flatMap { Right(EvalResult(it)) }
        }
        else -> Either.Left(ScriptError("${name.name} is not a function."))
      }
    }

  // TODO: this function should probably return an Either instead of throwing an exception
  fun updateFunctionCallBindings(
    bindings: Bindings,
    args: List<Parameter>
  ): Either<ScriptError, Unit> =
    if (args.size == parameters.size) {
      bindings.addScope()
      for (i in args.indices) {
        val arg = args[i]
        val param = parameters[i]
        val paramRes = param.eval(bindings)//.getOrElse(TODO())
        if (paramRes.isNotEmpty()) {
          when(val res = bindings.bindVariable(arg.name, (paramRes.orNull()!!).result)) {
            is Either.Left  -> TODO()//return res
            else -> {}
          }
        } else {
          TODO()
        }
      }
      Either.Right(Unit)
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
    val elsifs: List<Elsif> = listOf(),
    val `else`: Else? = null
): Expression {
  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
    fun evalCondition(expression: Expression): Either<ScriptError, Boolean> =
      when (val res = expression.eval(bindings)) {
        is Left -> res
        is Right -> {
          when (val value = res.value.result) {
            is BooleanValue -> Right(value.value)
            else -> Left(ScriptError("Conditions in if expression must return BooleanValue."))
          }
        }
      }

    return if (evalCondition(condition).orNull() == true) {
      body.eval(bindings)
    } else {
      for (elsif in elsifs) {
        if (evalCondition(elsif.condition).orNull() == true) {
          return elsif.body.eval(bindings)
        }
      }
      return `else`?.body?.eval(bindings) ?: Either.Right(EvalResult(Nothing))
    }
  }
}

data class Elsif(val condition: Expression, val body: Expression): Expression {
  override fun eval(bindings: Bindings) = TODO("is this needed?")
}

data class Else(val body: Expression): Expression {
  override fun eval(bindings: Bindings) = TODO("is this needed?")
}
