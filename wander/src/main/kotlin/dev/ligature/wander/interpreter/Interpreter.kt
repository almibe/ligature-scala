/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.*
import dev.ligature.lig.LigParser
import dev.ligature.rakkoon.Rakkoon
import dev.ligature.wander.parser.*
import kotlinx.coroutines.runBlocking
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.TerminalNode

class Interpreter {
    fun run(script: String, topScope: Scope): Either<WanderError, WanderValue> {
        val inputStream = CharStreams.fromString(script)
        val lexer = WanderLexer(inputStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = WanderParser(tokenStream)
        val scriptVisitor = ScriptVisitor(topScope)
        return scriptVisitor.visitScript(parser.script())
    }
}

val ligParser = LigParser()

class ScriptVisitor(private val topScope: Scope): WanderBaseVisitor<Either<WanderError, WanderValue>>() {
    override fun visitScript(ctx: WanderParser.ScriptContext): Either<WanderError, WanderValue> {
        if (ctx.expression().size == 0) {
            return Either.Right(UnitWanderValue)
        }
        var step = 0; //used for stepping through script
        var lastResult: Either<WanderError, WanderValue>? = null

        while (ctx.getChild(step) != null) {
            when (val current = ctx.getChild(step)) {
                is WanderParser.ExpressionContext -> {
                    val expressionVisitor = ExpressionVisitor(topScope)
                    lastResult = expressionVisitor.visitExpression(current)
                    step++
                }
                is WanderParser.LetStatementContext -> {
                    val letStatementVisitor = LetStatementVisitor(topScope)
                    lastResult = letStatementVisitor.visitLetStatement(current)
                    step++
                }
                else -> TODO()
            }
        }
        return lastResult!!
    }
}

class ExpressionVisitor(private val scope: Scope): WanderBaseVisitor<Either<WanderError, WanderValue>>() {
    override fun visitExpression(ctx: WanderParser.ExpressionContext): Either<WanderError, WanderValue> {
        return when {
            ctx.wanderValue() != null -> {
                val wanderValueVisitor = WanderValueVisitor(scope)
                wanderValueVisitor.visitWanderValue(ctx.wanderValue())
            }
            ctx.functionCall() != null -> {
                val functionCallVisitor = FunctionCallVisitor(scope)
                functionCallVisitor.visitFunctionCall(ctx.functionCall())
            }
            else -> TODO("unknown expression type")
        }
    }
}

class FunctionCallVisitor(private val scope: Scope): WanderBaseVisitor<Either<WanderError, WanderValue>>() {
    override fun visitFunctionCall(ctx: WanderParser.FunctionCallContext): Either<WanderError, WanderValue> {
        val functionName = ctx.WANDER_NAME().text
        val functionRef = scope.lookupSymbol(functionName)
        return when {
            functionRef.isLeft() -> {
                TODO("symbol not found")
            }
            functionRef.orNull()!! !is WanderFunction -> {
                TODO("symbol is not a function")
            }
            else -> {
                val function = functionRef.orNull()!! as WanderFunction
                val params = if (ctx.expression() != null) {
                    //check param types
                    val expressionVisitor = ExpressionVisitor(scope)
                    ctx.expression().map {
                        when (val expressionRes = expressionVisitor.visitExpression(it)) {
                            is Either.Left -> return expressionRes
                            is Either.Right -> expressionRes.value
                        }
                    }
                } else {
                    listOf()
                }
                runBlocking { //TODO fix this so I'm not blocking
                    function.body(params)
                }
            }
        }
    }
}

class LetStatementVisitor(private val scope: Scope): WanderBaseVisitor<Either<WanderError, UnitWanderValue>>() {
    override fun visitLetStatement(ctx: WanderParser.LetStatementContext): Either<WanderError, UnitWanderValue> {
        val name = ctx.WANDER_NAME().text
        val expressionVisitor = ExpressionVisitor(scope)
        return when (val res = expressionVisitor.visitExpression(ctx.expression())) {
            is Either.Left -> res
            is Either.Right -> {
                val value = res.value
                scope.addSymbol(name, value)
                Either.Right(UnitWanderValue)
            }
        }
    }
}

class WanderValueVisitor(private val scope: Scope): WanderBaseVisitor<Either<WanderError, WanderValue>>() {
    override fun visitWanderValue(ctx: WanderParser.WanderValueContext): Either<WanderError, WanderValue> {
        return when {
            ctx.functionDecl() != null -> {
                val functionDeclVisitor = FunctionDeclVisitor(scope)
                functionDeclVisitor.visitFunctionDecl(ctx.functionDecl())
            }
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
            ctx.WANDER_NAME() != null -> scope.lookupSymbol(ctx.WANDER_NAME().text)
            else -> Either.Left(ParserError("Unknown primitive.", -1)) //TODO fix error
        }
    }
}

fun wrapValue(value: Either<WanderError, Value>): Either<WanderError, WanderValue> = //TODO this can be cleaned up
    when (value) {
        is Either.Left -> value
        is Either.Right -> when (value.value) {
            is IntegerLiteral -> value.map { IntegerWanderValue(it as IntegerLiteral) }
            is Entity -> value.map { EntityWanderValue(it as Entity) }
            is FloatLiteral -> value.map { FloatWanderValue(it as FloatLiteral) }
            is StringLiteral -> value.map { StringWanderValue(it as StringLiteral) }
        }
    }

class FunctionDeclVisitor(private val scope: Scope): WanderBaseVisitor<Either<WanderError, WanderFunction>>() {
    override fun visitFunctionDecl(ctx: WanderParser.FunctionDeclContext?): Either<WanderError, WanderFunction> {
        //TODO hard coded for now
        return Either.Right(WanderFunction(listOf()) { Either.Right(IntegerWanderValue(IntegerLiteral(5L))) })
    }
}

class StatementVisitor: WanderBaseVisitor<Either<WanderError, StatementWanderValue>>() {
    override fun visitStatement(ctx: WanderParser.StatementContext): Either<WanderError, StatementWanderValue> {
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

fun handleEntity(ctx: TerminalNode): Either<WanderError, Entity> {
    return ligParser.parseEntity(Rakkoon(ctx.text))
        .mapLeft { ParserError("Could not parse Entity.", -1) } //TODO fix error
}

fun handleAttribute(ctx: TerminalNode): Either<WanderError, AttributeWanderValue> {
    return ligParser.parseAttribute(Rakkoon(ctx.text))
        .mapLeft { ParserError("Could not parse Attribute.", -1) }.map { AttributeWanderValue(it) } //TODO fix error
}

class LigatureValueVisitor: WanderBaseVisitor<Either<WanderError, Value>>() {
    override fun visitLigatureValue(ctx: WanderParser.LigatureValueContext): Either<WanderError, Value> {
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
