/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.interpreter.*
import dev.ligature.wander.parse
import scala.annotation.unused
import dev.ligature.wander.preludes.common

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
  case HostFunction(hostFunction: dev.ligature.wander.HostFunction)
  case Triple(
      entity: dev.ligature.wander.Identifier,
      attribute: dev.ligature.wander.Identifier,
      value: dev.ligature.wander.WanderValue
  )
  case Quad(
      entity: dev.ligature.wander.Identifier,
      attribute: dev.ligature.wander.Identifier,
      value: WanderValue,
      graph: dev.ligature.wander.Identifier
  )
  case QuestionMark

case class HostFunction(
    name: String,
    fn: (
        arguments: Seq[Expression],
        bindings: Environment
    ) => Either[WanderError, (WanderValue, Environment)]
)

case class HostProperty(
    name: String,
    read: (
        bindings: Environment
    ) => Either[WanderError, (WanderValue, Environment)]
)

case class Parameter(
    name: Name,
    parameterType: Option[WanderValue]
)

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
    expression <- process(terms)
  } yield expression
  expression match
    case Left(value)  => Left(value)
    case Right(value) => environment.interpreter.eval(value, environment)

case class Introspect(
    tokens: Either[WanderError, Seq[Token]],
    terms: Either[WanderError, Term],
    expression: Either[WanderError, Expression]
)

def introspect(script: String): Introspect = {
  val tokens = tokenize(script)

  val terms = if (tokens.isRight) {
    parse(tokens.getOrElse(???))
  } else {
    Left(WanderError("Previous error."))
  }

  val expression = if (terms.isRight) {
    process(terms.getOrElse(???))
  } else {
    Left(WanderError("Previous error."))
  }

  Introspect(tokens, terms, expression)
}

def printResult(value: Either[WanderError, (WanderValue, Environment)]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printWanderValue(value._1)
  }

def printWanderValue(value: WanderValue): String =
  value match {
    case WanderValue.QuestionMark => "?"
    case WanderValue.Triple(entity, attribute, value) =>
      s"<${entity.name}> <${attribute.name}> ${printWanderValue(value)},"
    case WanderValue.Quad(entity, attribute, value, graph) =>
      s"<${entity.name}> <${attribute.name}> ${printWanderValue(value)} <${graph.name}>,"
    case WanderValue.BooleanValue(value) => value.toString()
    case WanderValue.IntValue(value)     => value.toString()
    case WanderValue.StringValue(value)  => value
    case WanderValue.Identifier(value)   => s"<${value.name}>"
    case WanderValue.HostFunction(body)  => "[HostFunction]"
    case WanderValue.Nothing             => "nothing"
    case WanderValue.Lambda(lambda)      => "[Lambda]"
    case WanderValue.Array(values) =>
      "[" + values.map(value => printWanderValue(value)).mkString(" ") + "]"
    case WanderValue.Set(values) =>
      "#[" + values.map(value => printWanderValue(value)).mkString(" ") + "]"
  }

final case class Identifier private (name: String) {
  @unused
  private def copy(): Unit = ()
}

object Identifier {
  private val pattern = "^[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]+$".r

  def fromString(name: String): Either[WanderError, Identifier] =
    if (pattern.matches(name)) {
      Right(Identifier(name))
    } else {
      Left(WanderError(s"Invalid Identifier $name"))
    }
}
