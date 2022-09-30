/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import arrow.core.flatMap
import dev.ligature.Dataset
import dev.ligature.wander.interpreter.common
import dev.ligature.wander.interpreter.createStandardBindings
import dev.ligature.wander.interpreter.interpret
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.ScriptResult
import dev.ligature.wander.parser.parse

interface WanderError {
  val message: String
}

fun run(script: String, bindings: Bindings = common()): Either<WanderError, ScriptResult> =
  tokenize(script)
    .flatMap { tokens: List<Token> -> parse(tokens) }
    .flatMap { interpret(it, bindings) }

fun run(script: String, dataset: Dataset, bindings: Bindings = createStandardBindings(dataset)): Either<WanderError, ScriptResult> =
  tokenize(script)
    .flatMap { parse(it) }
    .flatMap { interpret(it, dataset, bindings) }
//  for {
//    tokens <- tokenize(script).left.map { (e: TokenizeError) =>
//      ScriptError(e.message)
//    }
//    script <- parse(tokens).left.map(ScriptError(_))
//    result <- interpret(script, dataset)
//  } yield result
