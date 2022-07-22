/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.Bindings
import dev.ligature.Statement
import dev.ligature.Value
import arrow.core.Either
import arrow.core.Option
import arrow.core.Some
import arrow.core.None

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

data class ScriptError(val message: String)
data class ScriptResult(val result: WanderValue)
data class EvalResult(val bindings: Bindings, val result: WanderValue)

/** Represents a Name in the Wander language.
  */
final data class Name(val name: String): Expression {
  override fun eval(bindings: Bindings) =
    when(val res = bindings.read(this)) {
      is Either.Left  -> res
      is Either.Right -> Either.Right(EvalResult(bindings, res.value))
    }
}

sealed interface FunctionDefinition(val parameters: List<Parameter>): WanderValue

data class LigatureValue(val value: Value): WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(
    EvalResult(bindings, LigatureValue(value))
  )
}

data class BooleanValue(value: Boolean): WanderValue {
  override fun eval(bindings: Bindings) = Either.Right(
    EvalResult(bindings, BooleanValue(value))
  )
}

//TODO is this needed?
data class StatementValue(value: Statement): WanderValue {
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
  override fun eval(bindings: Bindings) {
    return when(val result = this.expression.eval(bindings)) {
      is Left  -> result
      is Right -> {
        when(val res = bindings.bindVariable(this.name, result.value.result)) {
          is Left  -> res
          is Right -> Right(EvalResult(res.value, Nothing))
        }
      }
    }
  }
}

/** Holds a reference to a function defined in Scala that can be called from
  * Wander.
  */
data class NativeFunction(
    override val parameters: List<Parameter>,
    body: (bindings: Bindings) -> Either<ScriptError, WanderValue>,
    output: WanderType = null
): FunctionDefinition(parameters) { // TODO eventually remove the default null value
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

/** Represents a scope in Wander that can be eval'd and can contain it's own
  * bindings.
  */
data class Scope(val elements: List<Element>): Expression {
  override fun eval(bindings: Bindings): Either<ScriptError, ScriptResult> {
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
    Either.Right(EvalResult(bindings, result))
  }
}

sealed interface WanderType
object WTValue: WanderType
object WTIdentifier: WanderType
object WTBoolean: WanderType
object WTString: WanderType
object WTInteger: WanderType
data class WTFunction(val parameters: List<Parameter>, val output: WanderType): WanderType

data class Parameter(
    val name: Name,
    val parameterType: WanderType
)

/** Holds a reference to a function defined in Wander.
  */
data class WanderFunction(
    override val parameters: List<Parameter>,
    output: WanderType,
    body: Scope
): FunctionDefinition(parameters) {
  override fun eval(bindings: Bindings) =
    Either.Right(EvalResult(bindings, this))
}

data class FunctionCall(val name: Name, val parameters: List<Expression>): Expression {
  fun eval(bindings: Bindings) {
    val func = bindings.read(name)
    when(func) {
      is Right(wf: WanderFunction) -> {
        val functionCallBindings =
          updateFunctionCallBindings(bindings, wf.parameters)
        wf.body.eval(functionCallBindings) match {
          case left: Left[ScriptError, EvalResult] => left
          case Right(value)                        => Right(EvalResult(bindings, value.result))
        }
      }
      is Right(nf: NativeFunction) -> {
        val functionCallBindings =
          updateFunctionCallBindings(bindings, nf.parameters)
        val res = nf.body(functionCallBindings)
        res.map(EvalResult(bindings, _))
      }
      else -> Either.Left(ScriptError("${name.name} is not a function."))
    }
  }

  // TODO: this function should probably return an Either instead of throwing an exception
  fun updateFunctionCallBindings(
      bindings: Bindings,
      args: List[Parameter]
  ): Bindings =
    if (args.length == parameters.length) {
      var currentBindings = bindings.newScope()
      for (i <- args.indices) {
        val arg = args(i)
        val param = parameters(i)
        val paramRes = param.eval(currentBindings).getOrElse(???)
        currentBindings.bindVariable(arg.name, paramRes.result) match {
          case Left(err)    => Left(err)
          case Right(value) => currentBindings = value
        }
      }
      currentBindings
    } else {
      throw RuntimeException(
        s"Argument number ${args.length} != Parameter number ${parameters.length}"
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
    if (evalCondition(condition)) {
      body.eval(bindings)
    } else {
      for (elseIf in elseIfs) {
        if (evalCondition(elseIf.condition)) {
          return elseIf.body.eval(bindings)
        }
      }
      return when(`else`) {
        is Some -> `else`.value.body.eval(bindings)
        is None -> Either.Right(EvalResult(bindings, Nothing))
      }
    }
  }
}

data class ElseIf(val condition: Expression, val body: Expression): Expression {
  override fun eval(bindings: Bindings) = TODO("is this needed?")
}

data class Else(val body: Expression): Expression {
  override fun eval(bindings: Bindings) = TODO("is this needed?")
}
