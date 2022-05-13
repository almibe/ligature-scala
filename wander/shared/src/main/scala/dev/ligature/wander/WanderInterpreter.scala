/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.parser.{
  parse,
  Nothing,
  Script,
  ScriptError,
  ScriptResult
}
import dev.ligature.wander.lexer.TokenizeError
import dev.ligature.{Dataset, Ligature}

def run(
    script: String,
    dataset: Dataset
): Either[ScriptError, ScriptResult] = {
  for {
    tokens <- tokenize(script).left.map { (e: TokenizeError) =>
      ScriptError(e.message)
    }
    script <- parse(tokens).left.map(ScriptError)
    result <- interpret(script, dataset)
  } yield result
}

def interpret(
    script: Script,
    dataset: Dataset
): Either[ScriptError, ScriptResult] = {
  val bindings = createStandardBindings(dataset)
  script.eval(bindings)
}
