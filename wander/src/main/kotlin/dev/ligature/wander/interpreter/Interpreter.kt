/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.Ligature
import dev.ligature.wander.error.InterpreterError
import dev.ligature.wander.parser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Interpreter(private val ligature: Ligature) {
    private val scriptVisitor = ScriptVisitor()

    fun createCommandScope(): Scope {
        val scope = Scope(null)
        //TODO add default functions
        return scope
    }

    fun createQueryScope(): Scope {
        val scope = Scope(null)
        //TODO add default functions
        return scope
    }

    fun runCommand(script: String): Either<InterpreterError, Primitive> {
        return run(script, createCommandScope())
    }

    fun runQuery(script: String): Either<InterpreterError, Primitive> {
        return run(script, createQueryScope())
    }

    fun run(script: String, topScope: Scope): Either<InterpreterError, Primitive> {
        val inputStream = CharStreams.fromString(script)
        val lexer = WanderLexer(inputStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = WanderParser(tokenStream)
        return scriptVisitor.visitScript(parser.script())
    }
}

class ScriptVisitor: WanderBaseVisitor<Either<InterpreterError, Primitive>>() {
    override fun visitScript(ctx: WanderParser.ScriptContext): Either<InterpreterError, Primitive> {
        TODO()
    }
}
