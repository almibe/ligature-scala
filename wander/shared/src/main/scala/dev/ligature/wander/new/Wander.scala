/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

import dev.ligature.Identifier

def run(script: String): Either[WanderError, WanderValue] =
  for
    tokens <- tokenize(script)
    elements <- parse(tokens)
    expressions <- expressionize(elements)
    result <- eval(expressions)
  yield result

def runPrint(script: String): String =
  run(script) match
    case Right(value) => printWanderValue(value)
    case Left(err)    => err.message

case class WanderError(message: String)

enum WanderValue:
  case Integer(value: Long)
  case StringLiteral(value: String)
  case BooleanLiteral(value: Boolean)
  case Identifier(value: String)
  case Nothing

def printWanderValue(value: WanderValue): String =
  value match
    case WanderValue.Integer(value)        => value.toString()
    case WanderValue.StringLiteral(value)  => s"\"$value\""
    case WanderValue.Nothing               => "nothing"
    case WanderValue.BooleanLiteral(value) => value.toString()
    case WanderValue.Identifier(value)     => s"<$value>"
