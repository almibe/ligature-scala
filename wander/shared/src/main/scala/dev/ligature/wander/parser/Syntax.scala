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
  override def eval(binding: Bindings) = ???
}

case class BooleanValue(value: Boolean) extends WanderValue {
  override def eval(binding: Bindings) = ???
}

case class StatementValue(value: Statement) extends WanderValue {
  override def eval(binding: Bindings) = ???
}

object Nothing extends WanderValue {
  override def eval(binding: Bindings) = ???
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

/** Represents a Name in the Wander language.
  */
case class Name(name: String)

/** Represents the result of running a script.
  */
enum WanderResult {
  case ScriptError(message: String)
  case ScriptResult(result: WanderValue)
}

/** Represents the union of Statements and Expressions
  */
sealed trait Element {
  def eval(bindings: Bindings): WanderResult
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
    body: (bindings: List[Bindings]) => WanderResult
)

/** Represents a full script that can be eval'd.
  */
case class Script(val elements: List[Element]) {
  def eval(bindings: Bindings) = {
    ???
  }
}

/** Represents a scope in Wander that can be eval'd and can contain it's own
  * bindings.
  */
case class Scope(val elements: List[Element]) extends Expression {
  def eval(bindings: Bindings) = {
    ???
  }
}

case class LetStatement(name: Name, expression: Expression) extends Element {
  def eval(bindings: Bindings) = {
    ???
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
