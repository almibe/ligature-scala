/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import scala.annotation.unused
import dev.ligature.wander.libraries.std

/** Represents a Value in the Wander language.
  */
enum WanderValue:
  case Nothing
  case Int(value: Long)
  case Bool(value: Boolean)
  case String(value: java.lang.String)
  case Array(values: Seq[WanderValue])
  case Record(values: Map[Name, WanderValue])
  case Function(function: dev.ligature.wander.Function)
  case QuestionMark

trait Function:
  def call(args: Seq[WanderValue], environment: Environment): Either[WanderError, WanderValue]

case class Lambda(val lambda: Expression.Lambda) extends Function {
  override def call(
      args: Seq[WanderValue],
      environment: Environment
  ): Either[WanderError, WanderValue] = ???
}
case class PartialFunction(args: Seq[WanderValue], function: dev.ligature.wander.Function)
    extends Function {
  override def call(
      args: Seq[WanderValue],
      environment: Environment
  ): Either[WanderError, WanderValue] = ???
}

case class HostFunction(
    name: Name,
    docString: String,
    parameters: Seq[TaggedName],
    resultTag: Tag,
    fn: (
        arguments: Seq[WanderValue],
        environment: Environment
    ) => Either[WanderError, (WanderValue, Environment)]
) extends Function {
  override def call(
      args: Seq[WanderValue],
      environment: Environment
  ): Either[WanderError, WanderValue] = ???
}

case class HostProperty(
    name: String,
    docString: String,
    resultTag: Tag,
    read: (
        environment: Environment
    ) => Either[WanderError, (WanderValue, Environment)]
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
    case Right(value) => environment.eval(value)

case class Introspect(
    tokens: Either[WanderError, Seq[Token]],
    terms: Either[WanderError, Seq[Term]],
    expression: Either[WanderError, Seq[Expression]]
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

def printWanderValue(value: WanderValue, interpolation: Boolean = false): String =
  value match {
    case WanderValue.QuestionMark => "?"
    case WanderValue.Bool(value)  => value.toString()
    case WanderValue.Int(value)   => value.toString()
    case WanderValue.String(value) =>
      if interpolation then value else s"\"$value\"" // TODO escape correctly
    case WanderValue.Nothing            => "nothing"
    case WanderValue.Function(function) => "[Function]"
    case WanderValue.Array(values) =>
      "[" + values.map(value => printWanderValue(value, interpolation)).mkString(", ") + "]"
    case WanderValue.Record(values) =>
      "{" + values
        .map((name, value) => name.name + " = " + printWanderValue(value, interpolation))
        .mkString(", ") + "}"
  }
