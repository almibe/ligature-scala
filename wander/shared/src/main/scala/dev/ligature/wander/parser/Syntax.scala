/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.Bindings
import cats.effect.IO
import dev.ligature.{Statement, Value}
import fs2.Stream
import scala.util.Success

/** Represents the union of Statements and Expressions
  */
sealed trait Element {
  def eval(bindings: Bindings): Either[ScriptError, EvalResult]
}

/** An element of a Wander program that can be evaluated for a value.
  */
sealed trait Expression extends Element

/** Represents a Value in the Wander language.
  */
sealed trait WanderValue extends Expression

case class ScriptError(message: String)
case class ScriptResult(result: WanderValue)
case class EvalResult(bindings: Bindings, result: WanderValue)

/** Represents a Name in the Wander language.
  */
final case class Name(name: String) extends Expression {
  override def eval(bindings: Bindings) = {
    bindings.read(this) match {
      case Left(err)    => Left(err)
      case Right(value) => Right(EvalResult(bindings, value))
    }
  }
}

sealed trait FunctionDefinition(val parameters: List[Parameter])
    extends WanderValue

case class LigatureValue(value: Value) extends WanderValue {
  override def eval(bindings: Bindings) = Right(
    EvalResult(bindings, LigatureValue(value))
  )
}

case class BooleanValue(value: Boolean) extends WanderValue {
  override def eval(bindings: Bindings) = Right(
    EvalResult(bindings, BooleanValue(value))
  )
}

//TODO is this needed?
case class StatementValue(value: Statement) extends WanderValue {
  override def eval(binding: Bindings) = ???
}

object Nothing extends WanderValue {
  override def eval(bindings: Bindings) = Right(EvalResult(bindings, Nothing))
}

case class ResultStream(stream: Stream[IO, WanderValue]) extends WanderValue {
  override def eval(binding: Bindings) = ???
}

object EqualSign extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval equal sign.")
  )
}

object OpenBrace extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval open brace.")
  )
}

object CloseBrace extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval close brace.")
  )
}

object OpenParen extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval open paren.")
  )
}

object CloseParen extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval close paren.")
  )
}

object Arrow extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval arrow.")
  )
}

object Colon extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval colon.")
  )
}

object LetKeyword extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval let keyword.")
  )
}

case class LetStatement(name: Name, expression: Expression) extends Element {
  override def eval(bindings: Bindings) = {
    val result = this.expression.eval(bindings)
    result match {
      case Left(_) => return result
      case Right(value) => {
        bindings.bindVariable(this.name, value.result) match {
          case Left(err)          => Left(err)
          case Right(newBindings) => Right(EvalResult(newBindings, Nothing))
        }
      }
    }
  }
}

/** Holds a reference to a function defined in Scala that can be called from
  * Wander.
  */
case class NativeFunction(
    override val parameters: List[Parameter],
    body: (bindings: Bindings) => Either[ScriptError, WanderValue],
    output: WanderType = null
) extends FunctionDefinition(parameters) { // TODO eventually remove the default null value
  override def eval(binding: Bindings) = {
    // body(binding) match {
    //   case Left(err) => Left(err)
    //   case Right(res) => Right(EvalResult(binding, res))
    // }
    Right(EvalResult(binding, this))
  }
}

/** Represents a full script that can be eval'd.
  */
case class Script(val elements: Seq[Element]) {
  def eval(bindings: Bindings): Either[ScriptError, ScriptResult] = {
    var result: WanderValue = Nothing
    var currentBindings: Bindings = bindings
    elements.foreach { element =>
      element.eval(currentBindings) match {
        case Left(err) => return Left(err)
        case Right(res) => {
          result = res.result
          currentBindings = res.bindings
        }
      }
    }
    Right(ScriptResult(result))
  }
}

/** Represents a scope in Wander that can be eval'd and can contain it's own
  * bindings.
  */
case class Scope(val elements: List[Element]) extends Expression {
  def eval(bindings: Bindings) = {
    var currentBindings = bindings.newScope()
    var result: WanderValue = Nothing
    elements.foreach { element =>
      element.eval(currentBindings) match {
        case Left(err) => return Left(err)
        case Right(res) => {
          result = res.result
          currentBindings = res.bindings
        }
      }
    }
    Right(EvalResult(bindings, result))
  }
}

enum WanderType {
  case Value
  case Identifier
  case Boolean
  case String
  case Integer
  case Function(parameters: List[Parameter], output: WanderType)
}

case class Parameter(
    name: Name,
    parameterType: WanderType
)

/** Holds a reference to a function defined in Wander.
  */
case class WanderFunction(
    override val parameters: List[Parameter],
    output: WanderType,
    body: Scope
) extends FunctionDefinition(parameters) {
  override def eval(bindings: Bindings) = {
    Right(EvalResult(bindings, this))
  }
}

case class FunctionCall(val name: Name, val parameters: List[Expression])
    extends Expression {
  def eval(bindings: Bindings) = {
    val func = bindings.read(name)
    func match {
      case Right(wf: WanderFunction) => {
        val functionCallBindings =
          updateFunctionCallBindings(bindings, wf.parameters)
        wf.body.eval(functionCallBindings) match {
          case left: Left[ScriptError, EvalResult] => left
          case Right(value) => Right(EvalResult(bindings, value.result))
        }
      }
      case Right(nf: NativeFunction) => {
        val functionCallBindings =
          updateFunctionCallBindings(bindings, nf.parameters)
        val res = nf.body(functionCallBindings)
        res.map(EvalResult(bindings, _))
      }
      case _ => Left(ScriptError(s"${name.name} is not a function."))
    }
  }

  // TODO: this function should probably return an Either instead of throwing an exception
  def updateFunctionCallBindings(
      bindings: Bindings,
      args: List[Parameter]
  ): Bindings = {
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
}

//TODO is this needed?
case class ReferenceExpression(val name: Name) extends Expression {
  def eval(bindings: Bindings) = {
    ???
  }
}

case class IfExpression(
    val condition: Expression,
    body: Expression,
    elseIfs: List[ElseIf] = List(),
    `else`: Option[Else] = None
) extends Expression {
  def eval(bindings: Bindings) = {
    val res = condition.eval(bindings) match {
      case Right(EvalResult(_, BooleanValue(res))) => res
      case Left(err)                               => return Left(err)
      case _ =>
        return Left(
          ScriptError("Conditions in if expression must return BooleanValue.")
        )
    }
    if (res) {
      body.eval(bindings)
    } else {
      for (elseIf <- elseIfs) {
        val res = elseIf.condition.eval(bindings) match {
          case Right(EvalResult(_, BooleanValue(res))) => res
          case Left(err)                               => return Left(err)
          case _ =>
            return Left(
              ScriptError(
                "Conditions in if expression must return BooleanValue."
              )
            )
        }
        if (res) {
          return elseIf.body.eval(bindings)
        }
      }
      `else` match {
        case Some(e) => e.body.eval(bindings)
        case None    => Right(EvalResult(bindings, Nothing))
      }
    }
  }
}

case class ElseIf(val condition: Expression, val body: Expression) {
  def eval(bindings: Bindings) = { // TODO is this needed?
    ???
  }
}

case class Else(val body: Expression) {
  def eval(bindings: Bindings) = { // TODO is this needed?
    ???
  }
}
