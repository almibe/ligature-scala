/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.{WanderValue, ScriptResult}
import dev.ligature.wander.parse
import dev.ligature.{Dataset, Ligature}
import dev.ligature.lig.writeValue
import dev.ligature.LigatureError

def run(
    script: String,
    bindings: Bindings
): Either[LigatureError, ScriptResult] =
  for {
    tokens <- tokenize(script)
    script <- parse(tokens)
    result <- eval(script, bindings)
  } yield result


def printResult(result: Either[LigatureError, ScriptResult]): String = {
  result match {
    case Left(value) => value.userMessage
    case Right(value) => printWanderValue(value)
  }
}

def printWanderValue(value: WanderValue): String = {
  value match {
    case WanderValue.BooleanValue(value) => value.toString()
    case WanderValue.LigatureValue(value) => writeValue(value)
    case WanderValue.NativeFunction(parameters, body, output) => "[NativeFunction]"
    case WanderValue.Nothing => "nothing"
    case WanderValue.WanderFunction(parameters, output, body) => "[WanderFunction]"
    case WanderValue.Name(name) => s"[Name:${name}]"
    case WanderValue.Scope(contents) => "[Scope]"
  }
}
