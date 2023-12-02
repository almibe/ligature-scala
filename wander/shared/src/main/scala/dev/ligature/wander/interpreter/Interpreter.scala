/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break
import dev.ligature.wander.*

trait Interpreter:
  def eval(
      expression: Expression,
      bindings: Environment
  ): Either[WanderError, (WanderValue, Environment)]

enum Expression:
  case NameExpression(value: Name)
  case IdentifierValue(value: Identifier)
  case IntegerValue(value: Long)
  case StringValue(value: String)
  case BooleanValue(value: Boolean)
  case Nothing
  case Array(value: Seq[Expression])
  case Set(value: Seq[Expression])
  case LetExpression(name: Name, value: Expression)
  case Application(name: Name, arguments: Seq[Expression])
  case Lambda(parameters: Seq[Name], body: Expression)
  case WhenExpression(conditionals: Seq[(Expression, Expression)])
  case Grouping(expressions: Seq[Expression])
  case Triple(entity: Expression, attribute: Expression, value: Expression)
  case Quad(entity: Expression, attribute: Expression, value: Expression, graph: Expression)
  case QuestionMark
