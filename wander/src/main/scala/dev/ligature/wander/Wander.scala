/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.{WanderValue, ScriptResult}
import dev.ligature.wander.parse
import scala.annotation.unused

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    bindings: Bindings
): Either[WanderError, ScriptResult] =
  val terms = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
  } yield terms
  terms match
    case Left(value) => Left(value)
    case Right(value) => eval(value, bindings).map(_.result)

def evalString(
  script: String,
  bindings: Bindings
): Either[WanderError, EvalResult] =
  val terms = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
  } yield terms
  terms match
    case Left(value) => Left(value)
    case Right(value) => eval(value, bindings)

def printResult(value: ScriptResult): String = {
  printWanderValue(value)
}

def printWanderValue(value: WanderValue): String = {
  value match {
    case WanderValue.BooleanValue(value) => value.toString()
//    case WanderValue.LigatureValue(value) => writeValue(value)
    case WanderValue.NativeFunction(body) => "[NativeFunction]"
    case WanderValue.Nothing => "nothing"
    case WanderValue.WanderFunction(parameters, body) => "[WanderFunction]"
//    case WanderValue.Itr(internal) => "[Stream]"
    case WanderValue.ListValue(values) =>
      "[" + values.map { value => printWanderValue(value) }.mkString(" ") + "]"
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
