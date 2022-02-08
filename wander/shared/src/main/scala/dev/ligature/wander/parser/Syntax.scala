/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.Bindings
import cats.effect.IO
import dev.ligature.{Statement, Value}
import fs2.Stream
import scala.util.Success

/** Represents a Value in the Wander language.
  */
sealed trait WanderValue extends Expression

sealed trait FunctionDefinition extends WanderValue

case class LigatureValue(value: Value) extends WanderValue {
  override def eval(binding: Bindings) = Right(
    LigatureValue(value)
  )
}

case class BooleanValue(value: Boolean) extends WanderValue {
  override def eval(binding: Bindings) = Right(
    BooleanValue(value)
  )
}

case class StatementValue(value: Statement) extends WanderValue {
  override def eval(binding: Bindings) = ???
}

object Nothing extends WanderValue {
  override def eval(binding: Bindings) = Right(Nothing)
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

object LetKeyword extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval let keyword.")
  )
}

/** Represents a Name in the Wander language.
  */
final case class Name(name: String) extends Expression {
  override def eval(binding: Bindings) = binding.read(this)
}

case class ScriptError(message: String)
case class ScriptResult(result: WanderValue)

/** Represents the union of Statements and Expressions
  */
sealed trait Element {
  def eval(bindings: Bindings): Either[ScriptError, WanderValue]
}

case class LetStatement(name: Name, expression: Expression) extends Element {
  override def eval(bindings: Bindings) = {
    val result = this.expression.eval(bindings)
    result match {
      case Left(_) => return result
      case Right(value) => {
        bindings.bindVariable(this.name, value)
        return Right(Nothing)
      }
    }
  }
}

/** An element of a Wander program that can be evaluated for a value.
  */
sealed trait Expression extends Element

/** Holds a reference to a function defined in Scala that can be called from
  * Wander.
  */
case class NativeFunction(
    parameters: List[Parameter],
    body: (bindings: Bindings) => Either[ScriptError, WanderValue],
    output: WanderType = null
) extends FunctionDefinition { // TODO eventually remove the default null value
  override def eval(binding: Bindings): Either[ScriptError, WanderValue] = {
    body(binding)
  }
}

/** Represents a full script that can be eval'd.
  */
case class Script(val elements: Seq[Element]) {
  def eval(bindings: Bindings): Either[ScriptError, ScriptResult] = {
    var result: Either[ScriptError, WanderValue] = Right(Nothing)
    elements.foreach { element =>
      result = element.eval(bindings)
    }
    result.map(ScriptResult(_))
  }
}

/** Represents a scope in Wander that can be eval'd and can contain it's own
  * bindings.
  */
case class Scope(val elements: List[Element]) extends Expression {
  def eval(bindings: Bindings) = {
    bindings.addScope()
    var result: Either[ScriptError, WanderValue] = Right(Nothing)
    elements.foreach { element =>
      result = element.eval(bindings)
    }
    bindings.removeScope()
    result
  }
}

enum WanderType {
  case Boolean
  case String
  case Integer
  case Function(parameters: List[Parameter], output: WanderType)
}

case class Parameter(
    val name: Name,
    val parameterType: WanderType = null
) //TODO eventually remove the default null value

/** Holds a reference to a function defined in Wander.
  */
case class WanderFunction(
    parameters: List[Parameter],
    body: Scope,
    output: WanderType = null
) //TODO eventually remove the default null value
    extends FunctionDefinition {
  override def eval(binding: Bindings) = {
    Right(this)
  }
}

case class FunctionCall(val name: Name, val parameters: List[Expression])
    extends Expression {
  def eval(bindings: Bindings) = {
    val func = bindings.read(name)
    func match {
      case Right(wf: WanderFunction) => {
        updateFunctionCallBindings(bindings, wf.parameters)
        val res = wf.body.eval(bindings)
        bindings.removeScope()
        res
      }
      case Right(nf: NativeFunction) => {
        updateFunctionCallBindings(bindings, nf.parameters)
        val res = nf.body(bindings)
        bindings.removeScope()
        res
      }
      case _ => Left(ScriptError(s"${name.name} is not a function."))
    }
  }

  def updateFunctionCallBindings(binding: Bindings, args: List[Parameter]) = {
    if (args.length == parameters.length) {
      binding.addScope()
      for (i <- args.indices) {
        val arg = args(i)
        val param = parameters(i)
        binding.bindVariable(arg.name, param.eval(binding).getOrElse(???))
      }
    } else {
      throw RuntimeException(
        s"Argument number ${args.length} != Parameter number ${parameters.length}"
      )
    }
  }
}

case class ValueExpression(val value: WanderValue) extends Expression {
  def eval(bindings: Bindings) = {
    ???
  }
}

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
      case Right(BooleanValue(res)) => res
      case Left(err)                => return Left(err)
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
          case Right(BooleanValue(res)) => res
          case Left(err)                => return Left(err)
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
        case None    => Right(Nothing)
      }
    }
  }
}

case class ElseIf(val condition: Expression, val body: Expression) {
  def eval(bindings: Bindings) = {
    ???
  }
}

case class Else(val body: Expression) {
  def eval(bindings: Bindings) = {
    ???
  }
}
