/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.wander.error.InterpreterError
import dev.ligature.wander.parser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

class Interpreter {
    fun runCommand(script: String): Either<InterpreterError, Primitive> {
        return run(script, createCommandScope())
    }

    fun runQuery(script: String): Either<InterpreterError, Primitive> {
        return run(script, createQueryScope())
    }

    private fun run(script: String, topScope: Scope): Either<InterpreterError, Primitive> {
        TODO()
    }

    private fun parse(input: String) {
        val inputStream = CharStreams.fromString(input)
        val lexer = WanderLexer(inputStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = WanderParser(tokenStream)
        val tree = parser.script()
        val walker = ParseTreeWalker()
        walker.walk(WanderListener(), tree)
    }
}

private class WanderListener(): WanderBaseListener() {

}
