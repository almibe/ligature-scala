/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.{WanderValue, ScriptError, Script, ScriptResult}
import dev.ligature.wander.parse
import dev.ligature.{Dataset, Ligature}
import dev.ligature.lig.writeValue

def run(
    script: String,
    bindings: Bindings
): Either[ScriptError, ScriptResult] =
  for {
    tokens <- tokenize(script)
    script <- parse(tokens).left.map(ScriptError(_))
    result <- interpret(script, bindings)
  } yield result

def interpret(
    script: Script,
    bindings: Bindings
): Either[ScriptError, ScriptResult] = {
  script.eval(bindings)
}

def printResult(result: Either[ScriptError, ScriptResult]): String = {
  result match {
    case Left(value) => value.message
    case Right(ScriptResult(value)) => printWanderValue(value)
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
