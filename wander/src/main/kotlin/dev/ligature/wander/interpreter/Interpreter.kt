/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.*
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

    fun runCommand(script: String): Either<InterpreterError, WanderValue> {
        return run(script, createCommandScope())
    }

    fun runQuery(script: String): Either<InterpreterError, WanderValue> {
        return run(script, createQueryScope())
    }

    fun run(script: String, topScope: Scope): Either<InterpreterError, WanderValue> {
        val inputStream = CharStreams.fromString(script)
        val lexer = WanderLexer(inputStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = WanderParser(tokenStream)
        val scriptVisitor = ScriptVisitor(topScope)
        return scriptVisitor.visitScript(parser.script())
    }
}

val ligParser = LigParser()

class ScriptVisitor(private val topScope: Scope): WanderBaseVisitor<Either<InterpreterError, WanderValue>>() {
    override fun visitScript(ctx: WanderParser.ScriptContext): Either<InterpreterError, WanderValue> {
        if (ctx.expression().size == 0) {
            return Either.Right(UnitWanderValue)
        }
        val expressionVisitor = ExpressionVisitor(topScope)
        return expressionVisitor.visitExpression(ctx.expression(0))
    }
}

class ExpressionVisitor(scope: Scope): WanderBaseVisitor<Either<InterpreterError, WanderValue>>() {
    override fun visitExpression(ctx: WanderParser.ExpressionContext): Either<InterpreterError, WanderValue> {
        val wanderValueVisitor = WanderValueVisitor()
        return wanderValueVisitor.visitWanderValue(ctx.wanderValue())
    }
}

class WanderValueVisitor: WanderBaseVisitor<Either<InterpreterError, WanderValue>>() {
    override fun visitWanderValue(ctx: WanderParser.WanderValueContext): Either<InterpreterError, WanderValue> {
        return when {
            ctx.statement() != null -> {
                val statementVisitor = StatementVisitor()
                statementVisitor.visitStatement(ctx.statement())
            }
            ctx.ATTRIBUTE() != null -> {
                handleAttribute(ctx.ATTRIBUTE())
            }
            ctx.ligatureValue() != null -> {
                val valueVisitor = LigatureValueVisitor()
                wrapValue(valueVisitor.visitLigatureValue(ctx.ligatureValue()))
            }
            ctx.BOOLEAN() != null -> {
                val text = ctx.BOOLEAN().text
                Either.Right(BooleanWanderValue(text.toBoolean()))
            }
            else -> Either.Left(ParserError("Unknown primitive.", -1)) //TODO fix error
        }
    }
}

fun wrapValue(value: Either<InterpreterError, Value>): Either<InterpreterError, WanderValue> = //TODO this can be cleaned up
    when (value) {
        is Either.Left -> value
        is Either.Right -> when (value.value) {
            is IntegerLiteral -> value.map { IntegerWanderValue(it as IntegerLiteral) }
            is Entity -> value.map { EntityWanderValue(it as Entity) }
            is FloatLiteral -> value.map { FloatWanderValue(it as FloatLiteral) }
            is StringLiteral -> value.map { StringWanderValue(it as StringLiteral) }
        }
    }

class StatementVisitor(): WanderBaseVisitor<Either<InterpreterError, StatementWanderValue>>() {
    override fun visitStatement(ctx: WanderParser.StatementContext): Either<InterpreterError, StatementWanderValue> {
        val entity = handleEntity(ctx.ENTITY(0))
        val attribute = handleAttribute(ctx.ATTRIBUTE())
        val valueVisitor = LigatureValueVisitor()
        val value = valueVisitor.visitLigatureValue(ctx.ligatureValue())
        val context = handleEntity(ctx.ENTITY(1))
        return when {
            entity.isLeft() -> TODO()
            attribute.isLeft() -> TODO()
            value.isLeft() -> TODO()
            context.isLeft() -> TODO()
            else -> {
                val statement = Statement(entity.orNull()!!, attribute.orNull()!!.value, value.orNull()!!, context.orNull()!!)
                Either.Right(StatementWanderValue(statement))
            }
        }
    }
}

fun handleEntity(ctx: TerminalNode): Either<InterpreterError, Entity> {
    return ligParser.parseEntity(Rakkoon(ctx.text))
        .mapLeft { ParserError("Could not parse Entity.", -1) } //TODO fix error
}

fun handleAttribute(ctx: TerminalNode): Either<InterpreterError, AttributeWanderValue> {
    return ligParser.parseAttribute(Rakkoon(ctx.text))
        .mapLeft { ParserError("Could not parse Attribute.", -1) }.map { AttributeWanderValue(it) } //TODO fix error
}

class LigatureValueVisitor: WanderBaseVisitor<Either<InterpreterError, Value>>() {
    override fun visitLigatureValue(ctx: WanderParser.LigatureValueContext): Either<InterpreterError, Value> {
        return when {
            ctx.ENTITY() != null -> {
                handleEntity(ctx.ENTITY())
            }
            ctx.INTEGER_LITERAL() != null -> {
                ligParser.parseIntegerLiteral(Rakkoon(ctx.INTEGER_LITERAL()!!.text))
                    .mapLeft { ParserError("Could not parse value.", -1) } //TODO fix error
            }
            ctx.FLOAT_LITERAL() != null -> {
                ligParser.parseFloatLiteral(Rakkoon(ctx.FLOAT_LITERAL()!!.text))
                    .mapLeft { ParserError("Could not parse value.", -1) } //TODO fix error
            }
            ctx.STRING_LITERAL() != null -> {
                ligParser.parseStringLiteral(Rakkoon(ctx.STRING_LITERAL()!!.text))
                    .mapLeft { ParserError("Could not parse value.", -1) } //TODO fix error
            }
            else -> TODO()
        }
    }
}
