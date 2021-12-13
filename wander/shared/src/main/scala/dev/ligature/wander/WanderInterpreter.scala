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
import dev.ligature.Dataset

/**
 * This enum contains all of the modes that a Wander script can be ran in
 */
enum ExecutionMode:
  /**
   * In StandAloneMode you have no references to a Ligature instance are available.
   * You only have access to the core standard library for Wander.
   */
  case StandAloneMode
  /**
   * In Instance mode you have access to an entire Ligature instance.
   * You can add Datasets, remove Datasets, run transactions against specific Datasets, etc.
   */
  case InstanceMode
  /**
   * In Dataset mode you have complete control over a single Dataset.
   * This allows you to run queries and write transactions against a single Dataset.
   */
  case DatasetMode(val dataset: Dataset)
  /**
   * ReadMode allows you to perform read actions with a single Dataset.
   */
  case ReadMode(val dataset: Dataset)
  /**
   * WriteMode allows you to perform write actions with a single Dataset.
   */
  case WriteMode(val dataset: Dataset)

def run(script: String, executionMode: ExecutionMode): Either[ScriptError, ScriptResult] = {
  for {
    tokens <- tokenize(script).left.map { (e: TokenizeError) =>
      ScriptError(e.message)
    }
    script <- parse(tokens).left.map(ScriptError(_))
    result <- interpret(script, executionMode)
  } yield result
}

def interpret(script: Script, executionMode: ExecutionMode): Either[ScriptError, ScriptResult] = {
  val bindings = createStandardBindings(executionMode)//common()
  var result: Either[ScriptError, ScriptResult] = Right(ScriptResult(Nothing))
  script.eval(bindings)
}
