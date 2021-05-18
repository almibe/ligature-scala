/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.Ligature
import dev.ligature.lig.LigParser
import dev.ligature.rakkoon.Rakkoon
import dev.ligature.wander.error.InterpreterError
import dev.ligature.wander.error.ParserError
import dev.ligature.wander.parser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.TerminalNode

class Interpreter(private val ligature: Ligature) {
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
        val scriptVisitor = ScriptVisitor(topScope)
        return scriptVisitor.visitScript(parser.script())
    }
}

val ligParser = LigParser()

class ScriptVisitor(private val topScope: Scope): WanderBaseVisitor<Either<InterpreterError, Primitive>>() {
    override fun visitScript(ctx: WanderParser.ScriptContext): Either<InterpreterError, Primitive> {
        if (ctx.expression().size == 0) {
            return Either.Right(UnitPrimitive)
        }
        val expressionVisitor = ExpressionVisitor(topScope)
        return expressionVisitor.visitExpression(ctx.expression(0))
    }
}

class ExpressionVisitor(scope: Scope): WanderBaseVisitor<Either<InterpreterError, Primitive>>() {
    override fun visitExpression(ctx: WanderParser.ExpressionContext): Either<InterpreterError, Primitive> {
        val primitiveVisitor = PrimitiveVisitor()
        return primitiveVisitor.visitPrimative(ctx.primative())
    }
}

class PrimitiveVisitor: WanderBaseVisitor<Either<InterpreterError, Primitive>>() {
    override fun visitPrimative(ctx: WanderParser.PrimativeContext): Either<InterpreterError, Primitive> {
        return when {
            ctx.ENTITY() != null -> {
                handleEntity(ctx.ENTITY())
            }
            ctx.ATTRIBUTE() != null -> {
                handleAttribute(ctx.ATTRIBUTE())
            }
            ctx.value() != null -> {
                val valueVisitor = ValueVisitor()
                valueVisitor.visitValue(ctx.value())
            }
            ctx.BOOLEAN() != null -> {
                val text = ctx.BOOLEAN().text
                Either.Right(BooleanPrimitive(text.toBoolean()))
            }
            else -> Either.Left(ParserError("Unknown primitive.", -1)) //TODO fix error
        }
    }
}

fun handleEntity(ctx: TerminalNode): Either<InterpreterError, EntityPrimitive> {
    return ligParser.parseEntity(Rakkoon(ctx.text))
        .mapLeft { ParserError("Could not parse Entity.", -1) }.map { EntityPrimitive(it) } //TODO fix error
}

fun handleAttribute(ctx: TerminalNode): Either<InterpreterError, AttributePrimitive> {
    return ligParser.parseAttribute(Rakkoon(ctx.text))
        .mapLeft { ParserError("Could not parse Attribute.", -1) }.map { AttributePrimitive(it) } //TODO fix error
}

class ValueVisitor: WanderBaseVisitor<Either<InterpreterError, Primitive>>() {
    override fun visitValue(ctx: WanderParser.ValueContext): Either<InterpreterError, Primitive> {
        return when {
            ctx.INTEGER_LITERAL() != null -> {
                ligParser.parseIntegerLiteral(Rakkoon(ctx.INTEGER_LITERAL()!!.text))
                    .mapLeft { ParserError("Could not parse value.", -1) }.map { IntegerPrimitive(it) } //TODO fix error
            }
            ctx.FLOAT_LITERAL() != null -> {
                ligParser.parseFloatLiteral(Rakkoon(ctx.FLOAT_LITERAL()!!.text))
                    .mapLeft { ParserError("Could not parse value.", -1) }.map { FloatPrimitive(it) } //TODO fix error
            }
            ctx.STRING_LITERAL() != null -> {
                ligParser.parseStringLiteral(Rakkoon(ctx.STRING_LITERAL()!!.text))
                    .mapLeft { ParserError("Could not parse value.", -1) }.map { StringPrimitive(it) } //TODO fix error
            }
            else -> TODO()
        }
    }
}
