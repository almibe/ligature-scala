/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.parser.{parse, Nothing, Script, ScriptError, ScriptResult}
import dev.ligature.wander.lexer.TokenizeError
import dev.ligature.{Dataset, Ligature}
import dev.ligature.lig.writeValue
import dev.ligature.wander.parser.WanderValue
import dev.ligature.wander.parser.BooleanValue
import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.NativeFunction
import dev.ligature.wander.parser.ResultStream
import dev.ligature.wander.parser.StatementValue
import dev.ligature.wander.parser.WanderFunction

def run(
    script: String,
    dataset: Dataset
): Either[ScriptError, ScriptResult] =
  for {
    tokens <- tokenize(script).left.map { (e: TokenizeError) =>
      ScriptError(e.message)
    }
    script <- parse(tokens).left.map(ScriptError(_))
    result <- interpret(script, dataset)
  } yield result

def interpret(
    script: Script,
    dataset: Dataset
): Either[ScriptError, ScriptResult] = {
  val bindings = createStandardBindings(dataset)
  script.eval(bindings)
}

def printResult(result: Either[ScriptError, ScriptResult]): String =
  result match {
    case Left(value)                => value.message
    case Right(ScriptResult(value)) => printWanderValue(value)
  }

def printWanderValue(value: WanderValue): String =
  value match {
    case BooleanValue(value)                      => value.toString()
    case LigatureValue(value)                     => writeValue(value)
    case NativeFunction(parameters, body, output) => "[NativeFunction]"
    case Nothing                                  => "nothing"
    case ResultStream(stream)                     => "[ResultStream]"
    case StatementValue(value)                    => "[StatementValue]"
    case WanderFunction(parameters, output, body) => "[WanderFunction]"
  }
