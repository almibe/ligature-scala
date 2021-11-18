/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.{Script, WanderResult, BooleanValue, LigatureValue}
import dev.ligature.wander.parser.WanderResult.*
import dev.ligature.wander.lexer.TokenType
import dev.ligature.{Identifier, IntegerLiteral}

case class TestData(val description: String,
    val script: String,
    val ast: Script, 
    val result: WanderResult)

/*
 * This file holds a Map that contains the expected ASTs for Wander's test files.
 */
val testData = List(
        //primitives
        TestData(
            description = "boolean primitive", 
            script = "true",
            ast = Script(List(BooleanValue(true))), 
            result = ScriptResult(BooleanValue(true))),

        TestData(
            description = "boolean primitive", 
            script = "false",
            ast = Script(List(BooleanValue(false))), 
            result = ScriptResult(BooleanValue(false))),

        TestData(
            description = "identifier",
            script = "<test>", //TODO this is wrong
            ast = Script(List(LigatureValue(Identifier.fromString("test").getOrElse(???)))),
            result = ScriptResult(LigatureValue(Identifier.fromString("test").getOrElse(???)))),

        TestData(
            description = "integer",
            script = "24601", //TODO this is wrong
            ast = Script(List(LigatureValue(IntegerLiteral(24601)))),
            result = ScriptResult(LigatureValue(IntegerLiteral(24601)))),

        TestData(
            description = "negative integer",
            script = "-111", //TODO this is wrong
            ast = Script(List(LigatureValue(IntegerLiteral(-111)))),
            result = ScriptResult(LigatureValue(IntegerLiteral(-111)))),

    // ("integer.wander" -> Script(List(
    //         valueExpression(LongLiteral.from(24601n).unsafeCoerce())
    //     )),

    // "nothing.wander" ->
    //     Script(List()),

    // "statement.wander" ->
    //     Script(List(
    //         valueExpression(new Statement(new Entity("entity"), new Attribute("attribute"), 3.03, new Entity("context")))
    //     )),

    // "string.wander" ->
    //     Script(List(
    //         valueExpression("Hello")
    //     )),

    // //ASSIGNMENT
    // "let.wander" ->
    //     Script(List(
    //         letStatement(identifier("x"), valueExpression(5n))
    //     )),

    // "let-res.wander" ->
    //     Script(List(
    //         letStatement(identifier("hello"), valueExpression(5n)),
    //         referenceExpression(identifier("hello"))
    //     )),

    // "block.wander" ->
    //     Script(List(
    //         scope(List(
    //             letStatement(identifier("x"), valueExpression(7n)),
    //             referenceExpression(identifier('x'))
    //         ))
    //     )),

    // "block-shadow.wander" ->
    //     Script(List(
    //         letStatement(identifier("x"), valueExpression(5n)),
    //         scope(List(
    //             letStatement(identifier("x"), valueExpression(7n)),
    //             referenceExpression(identifier('x'))
    //         ))
    //     )),

    // //FUNCTIONS
    // "function0-def.wander" ->
    //     Script(List(
    //         letStatement(identifier("f"), valueExpression(functionDefinition(List(), List(valueExpression(5n))))),
    //         functionCall(identifier("f"), List())
    //     )),

    // "function1-def.wander" ->
    //     Script(List(
    //         letStatement(identifier("identity"), valueExpression(functionDefinition(List("value"), List(referenceExpression(identifier("value")))))),
    //         functionCall(identifier("identity"), List(valueExpression(new Entity("testEntity"))))
    //     )),

    // //BOOLEAN-EXPRESSION
    // "not.wander" ->
    //     Script(List(
    //         functionCall(identifier("not"), List(valueExpression(true)))
    //     ))
)
