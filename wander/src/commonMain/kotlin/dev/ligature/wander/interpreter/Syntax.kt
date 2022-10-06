/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

//data class LetStatement(val name: Name, val expression: Expression): Element {
//
//  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
//    return when(val result = this.expression.eval(bindings)) {
//      is Either.Left  -> result
//      is Either.Right -> {
//        when(val res = bindings.bindVariable(this.name, result.value.result)) {
//          is Either.Left  -> res
//          is Either.Right -> Either.Right(EvalResult(Nothing))
//        }
//      }
//    }
//  }
//}
//
///** Holds a reference to a function defined in Kotlin that can be called from
//  * Wander.
//  */
//data class NativeFunction(
//  val nativeParameters: List<Parameter>,
//  val body: (bindings: Bindings) -> Either<ScriptError, WanderValue>,
////    val output: WanderType? = null
//): FunctionDefinition(nativeParameters) { // TODO eventually remove the default null value
//  override fun eval(binding: Bindings) =
//    // body(binding) match {
//    //   case Left(err) => Left(err)
//    //   case Right(res) => Right(EvalResult(res))
//    // }
//    Either.Right(EvalResult(this))
//}
//
//
///** Represents a scope in Wander that can be eval'd and can contain its own
//  * bindings.
//  */
//data class Scope(val elements: List<Element>): Expression {
//  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
//    bindings.addScope()//.newScope() TODO fix
//    var result: WanderValue = Nothing
//    elements.forEach { element ->
//      when (val res = element.eval(bindings)) {
//        is Either.Left  -> return res
//        is Either.Right -> {
//          result = res.value.result
//        }
//      }
//    }
//    bindings.removeScope()
//    return Either.Right(EvalResult(result))
//  }
//}
//
///** Holds a reference to a function defined in Wander.
//  */
//data class WanderFunction(
//    val wanderParameters: List<Parameter>,
////    val output: WanderType,
//    val body: Scope
//): FunctionDefinition(wanderParameters) {
//  override fun eval(bindings: Bindings) =
//    Either.Right(EvalResult(this))
//}
//
//data class FunctionCall(val name: Name, val parameters: List<Expression>): Expression {
//  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> =
//    bindings.read(name).flatMap { value: WanderValue ->
//      when(value) {
//        is WanderFunction -> {
//          updateFunctionCallBindings(bindings, value.parameters)
//          val res = value.body.eval(bindings)
//          bindings.removeScope()
//          res
//        }
//        is NativeFunction -> {
//          updateFunctionCallBindings(bindings, value.parameters)
//          val res = value.body(bindings)
//          bindings.removeScope()
//          res.flatMap { Right(EvalResult(it)) }
//        }
//        else -> Either.Left(ScriptError("${name.name} is not a function."))
//      }
//    }
//
//  // TODO: this function should probably return an Either instead of throwing an exception
//  fun updateFunctionCallBindings(
//    bindings: Bindings,
//    args: List<Parameter>
//  ): Either<ScriptError, Unit> =
//    if (args.size == parameters.size) {
//      bindings.addScope()
//      for (i in args.indices) {
//        val arg = args[i]
//        val param = parameters[i]
//        val paramRes = param.eval(bindings)//.getOrElse(TODO())
//        if (paramRes.isNotEmpty()) {
//          when(val res = bindings.bindVariable(arg.name, (paramRes.orNull()!!).result)) {
//            is Either.Left  -> TODO()//return res
//            else -> {}
//          }
//        } else {
//          TODO()
//        }
//      }
//      Either.Right(Unit)
//    } else {
//      throw RuntimeException(
//        "Argument number ${args.size} != Parameter number ${parameters.size}"
//      )
//    }
//}
//
//data class IfExpression(
//    val condition: Expression,
//    val body: Expression,
//    val elsifs: List<Elsif> = listOf(),
//    val `else`: Else? = null
//): Expression {
//  override fun eval(bindings: Bindings): Either<ScriptError, EvalResult> {
//    fun evalCondition(expression: Expression): Either<ScriptError, Boolean> =
//      when (val res = expression.eval(bindings)) {
//        is Left -> res
//        is Right -> {
//          when (val value = res.value.result) {
//            is BooleanValue -> Right(value.value)
//            else -> Left(ScriptError("Conditions in if expression must return BooleanValue."))
//          }
//        }
//      }
//
//    return if (evalCondition(condition).orNull() == true) {
//      body.eval(bindings)
//    } else {
//      for (elsif in elsifs) {
//        if (evalCondition(elsif.condition).orNull() == true) {
//          return elsif.body.eval(bindings)
//        }
//      }
//      return `else`?.body?.eval(bindings) ?: Either.Right(EvalResult(Nothing))
//    }
//  }
//}
//
