/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.parse
import scala.annotation.unused
import dev.ligature.wander.preludes.common

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    bindings: Bindings
): Either[WanderError, WanderValue] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
    expression <- process(terms)
  } yield expression
  expression match
    case Left(value) => Left(value)
    case Right(value) => eval(value, bindings)

case class Introspect(
  tokens: Either[WanderError, Seq[Token]], 
  terms: Either[WanderError, Seq[Term]],
  expression: Either[WanderError, Expression])

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

def printResult(value: WanderValue): String = {
  printWanderValue(value)
}

def printWanderValue(value: WanderValue): String = {
  value match {
    case WanderValue.BooleanValue(value) => value.toString()
    case WanderValue.IntValue(value) => value.toString()
    case WanderValue.StringValue(value) => value
    case WanderValue.Record(value) => ???
    case WanderValue.Identifier(value) => s"<${value.name}>"
//    case WanderValue.LigatureValue(value) => writeValue(value)
    case WanderValue.NativeFunction(body) => "[NativeFunction]"
    case WanderValue.Nothing => "nothing"
    case WanderValue.WanderFunction(parameters, body) => "[WanderFunction]"
//    case WanderValue.Itr(internal) => "[Stream]"
    case WanderValue.Array(values) =>
      "[" + values.map { value => printWanderValue(value) }.mkString(" ") + "]"
    case WanderValue.Set(values) =>
      "#[" + values.map { value => printWanderValue(value) }.mkString(" ") + "]"
  }
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
