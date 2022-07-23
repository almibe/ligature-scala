/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.parser.parse
import dev.ligature.wander.parser.Nothing
import dev.ligature.wander.parser.Script
import dev.ligature.wander.parser.ScriptError
import dev.ligature.wander.parser.ScriptResult
import dev.ligature.wander.lexer.TokenizeError
import dev.ligature.Dataset
import dev.ligature.Ligature

import arrow.core.Either

fun run(
    script: String,
    dataset: Dataset
): Either<ScriptError, ScriptResult> = TODO()
//  for {
//    tokens <- tokenize(script).left.map { (e: TokenizeError) =>
//      ScriptError(e.message)
//    }
//    script <- parse(tokens).left.map(ScriptError(_))
//    result <- interpret(script, dataset)
//  } yield result

fun interpret(
    script: Script,
    dataset: Dataset
): Either<ScriptError, ScriptResult> {
    TODO()
//  val bindings = createStandardBindings(dataset)
//  script.eval(bindings)
}
