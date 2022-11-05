/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import arrow.core.flatMap
import dev.ligature.LigatureError
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.interpreter.eval
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.library.common
import dev.ligature.wander.model.Element
import dev.ligature.wander.parser.parse

interface WanderError : LigatureError

suspend fun run(script: String, bindings: Bindings = common()): Either<WanderError, Element> =
    tokenize(script).flatMap { tokens: List<Token> -> parse(tokens) }.flatMap { eval(it, bindings) }
