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

def run(script: String): Either[ScriptError, ScriptResult] = {
  for {
    tokens <- tokenize(script).left.map { (e: TokenizeError) =>
      ScriptError(e.message)
    }
    script <- parse(tokens).left.map(ScriptError(_))
    result <- interpret(script)
  } yield result
}

def interpret(script: Script): Either[ScriptError, ScriptResult] = {
  val bindings = common()
  var result: Either[ScriptError, ScriptResult] = Right(ScriptResult(Nothing))
  script.eval(bindings)
}
