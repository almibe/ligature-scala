/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.util.Success

/** Represents the union of Statements and Expressions
  */
// sealed trait Element {
//   def eval(bindings: Environment): Either[ScriptError, EvalResult]
// }

/** An element of a Wander program that can be evaluated for a value.
  */
//sealed trait Expression extends Element

/** Represents a Value in the Wander language.
  */
enum WanderValue:
  case Nothing
  case IntValue(value: Long)
  case BooleanValue(value: Boolean)
  case StringValue(value: String)
  case Identifier(value: dev.ligature.wander.Identifier)
  case Array(values: Seq[WanderValue])
  case Set(values: scala.collection.Set[WanderValue])
  case Lambda(lambda: Expression.Lambda)
  case HostFunction(
      body: (
          arguments: Seq[Expression],
          bindings: Environment
      ) => Either[WanderError, (WanderValue, Environment)]
  )
  case Triple(entity: dev.ligature.wander.Identifier, attribute: dev.ligature.wander.Identifier, value: dev.ligature.wander.WanderValue)
  case Quad(entity: dev.ligature.wander.Identifier, attribute: dev.ligature.wander.Identifier, value: WanderValue, graph: dev.ligature.wander.Identifier)
  case QuestionMark

//sealed trait FunctionDefinition(val parameters: List[Parameter]) extends WanderValue

// case class LigatureValue(value: Value) extends WanderValue {
//   override def eval(bindings: Environment) = Right(
//     EvalResult(LigatureValue(value), bindings)
//   )
// }

// case class BooleanValue(value: Boolean) extends WanderValue {
//   override def eval(bindings: Environment) = Right(
//     EvalResult(BooleanValue(value), bindings)
//   )
// }

// object Nothing extends WanderValue {
//   override def eval(bindings: Environment) = Right(EvalResult(Nothing, bindings))
// }

// case class ResultStream(stream: Stream[IO, WanderValue]) extends WanderValue {
//   override def eval(binding: Environment) = ???
// }

/** Holds a reference to a function defined in Scala that can be called from
  * Wander.
  */
// case class NativeFunction(
//     override val parameters: List[Parameter],
//     body: (arguments: Seq[Term], bindings: Environment) => Either[ScriptError, WanderValue],
//     output: WanderType = null
// ) extends FunctionDefinition(parameters) { // TODO eventually remove the default null value
//   override def eval(binding: Environment) =
//     // body(binding) match {
//     //   case Left(err) => Left(err)
//     //   case Right(res) => Right(EvalResult(binding, res))
//     // }
//     Right(EvalResult(this, binding))
// }

/** Represents a scope in Wander that can be eval'd and can contain it's own
  * bindings.
  */
// case class Scope(val elements: List[Term]) extends Expression {
//   def eval(bindings: Environment) = {
//     var currentEnvironment = bindings.newScope()
//     var result: WanderValue = Nothing
//     elements.foreach { element =>
//       evalTerm(element, currentEnvironment) match {
//         case Left(err) => return Left(err)
//         case Right(res) =>
//           result = res.result
//           currentEnvironment = res.bindings
//       }
//     }
//     Right(EvalResult(result, bindings))
//   }
// }

case class Parameter(
    name: Name,
    parameterType: Option[WanderValue]
)

/** Holds a reference to a function defined in Wander.
  */
// case class WanderFunction(
//     override val parameters: List[Parameter],
//     output: WanderType,
//     body: Scope
// ) extends FunctionDefinition(parameters) {
//   override def eval(bindings: Environment) =
//     Right(EvalResult(this, bindings))
// }

// case class FunctionCall(val name: Name, val parameters: List[Expression]) extends Expression {
//   def eval(bindings: Environment) = {
//     val func = bindings.read(name)
//     func match {
//       case Right(wf: WanderFunction) =>
//         val functionCallEnvironment =
//           updateFunctionCallEnvironment(bindings, wf.parameters)
//         wf.body.eval(functionCallEnvironment) match {
//           case left: Left[ScriptError, EvalResult] => left
//           case Right(value)                        => Right(EvalResult(value.result, bindings))
//         }
//       case Right(nf: NativeFunction) =>
//         val functionCallEnvironment =
//           updateFunctionCallEnvironment(bindings, nf.parameters)
//         val res = nf.body(functionCallEnvironment)
//         res.map(EvalResult(_, bindings))
//       case _ => Left(ScriptError(s"${name.name} is not a function."))
//     }
//   }

//   // TODO: this function should probably return an Either instead of throwing an exception
//   def updateFunctionCallEnvironment(
//       bindings: Environment,
//       args: List[Parameter]
//   ): Environment =
//     if (args.length == parameters.length) {
//       var currentEnvironment = bindings.newScope()
//       for (i <- args.indices) {
//         val arg = args(i)
//         val param = parameters(i)
//         val paramRes = param.eval(currentEnvironment).getOrElse(???)
//         currentEnvironment.bindVariable(arg.name, paramRes.result) match {
//           case Left(err)    => Left(err)
//           case Right(value) => currentEnvironment = value
//         }
//       }
//       currentEnvironment
//     } else {
//       throw RuntimeException(
//         s"Argument number ${args.length} != Parameter number ${parameters.length}"
//       )
//     }
// }

// case class IfExpression(
//     val condition: Expression,
//     body: Expression,
//     elseIfs: List[ElseIf] = List(),
//     `else`: Option[Else] = None
// ) extends Expression {
//   def eval(bindings: Environment) =
//     ???
//     // val res = condition.eval(bindings) match {
//     //   case Right(EvalResult(_, BooleanValue(res))) => res
//     //   case Left(err)                               => return Left(err)
//     //   case _ =>
//     //     return Left(
//     //       ScriptError("Conditions in if expression must return BooleanValue.")
//     //     )
//     // }
//     // if (res) {
//     //   body.eval(bindings)
//     // } else {
//     //   for (elseIf <- elseIfs) {
//     //     val res = elseIf.condition.eval(bindings) match {
//     //       case Right(EvalResult(_, BooleanValue(res))) => res
//     //       case Left(err)                               => return Left(err)
//     //       case _ =>
//     //         return Left(
//     //           ScriptError(
//     //             "Conditions in if expression must return BooleanValue."
//     //           )
//     //         )
//     //     }
//     //     if (res) {
//     //       return elseIf.body.eval(bindings)
//     //     }
//     //   }
//     //   `else` match {
//     //     case Some(e) => e.body.eval(bindings)
//     //     case None    => Right(EvalResult(Nothing, bindings))
//     //   }
//     // }
// }
