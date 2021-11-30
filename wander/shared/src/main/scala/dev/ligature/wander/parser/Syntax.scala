/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.Bindings
import cats.effect.IO
import dev.ligature.{Statement, Value}
import fs2.Stream

/** Represents a Value in the Wander language.
  */
sealed trait WanderValue extends Expression

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

case class FunctionDefinitionValue(value: FunctionDefinition)
    extends WanderValue {
  override def eval(binding: Bindings) = ???
}

case class NativeFunctionValue(value: NativeFunction) extends WanderValue {
  override def eval(binding: Bindings) = ???
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

object LetKeyword extends Element {
  override def eval(binding: Bindings) = Left(
    ScriptError("Cannot eval ley keyword.")
  )
}

/** Represents a Name in the Wander language.
  */
final case class Name(name: String) extends Expression {
  override def eval(binding: Bindings) = Right(binding.read(this))
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
        bindings.bind(this.name, value)
        return Right(Nothing)
      }
    }
  }
}

/** An element of a Wander program that can be evaluated for a value.
  */
sealed trait Expression extends Element

/** Holds a reference to a function defined in Wander.
  */
case class FunctionDefinition(parameters: List[String], body: List[Element])

/** Holds a reference to a function defined in Scala that can be called from
  * Wander.
  */
case class NativeFunction(
    parameters: List[String],
    body: (bindings: List[Bindings]) => Either[ScriptError, ScriptResult]
)

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
    elseIfs: List[ElseIf],
    `else`: Option[Else]
) extends Expression {
  def eval(bindings: Bindings) = {
    ???
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

case class FunctionCall(val name: Name, val parameters: List[Expression])
    extends Expression {
  def eval(bindings: Bindings) = {
    ???
  }
}
