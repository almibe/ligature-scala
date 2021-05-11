/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import arrow.core.Either
import arrow.core.getOrElse
import dev.ligature.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LexerSpec : FunSpec() {
    private val lexer = Lexer()
    private fun tk(offset: Int, token: WanderTokenValue) = WanderToken(offset, token)

    init {
        test("integer primitive") {
            val script = "5"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, IntegerPrimitive(IntegerLiteral(5L)))
            ))
        }

        test("float primitive") {
            val script = "5.75"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, FloatPrimitive(FloatLiteral(5.75)))
            ))
        }

        test("Entity primitive") {
            val script = "<hello___:::///////3905823989>"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, EntityPrimitive(Entity.from("hello___:::///////3905823989").getOrElse { TODO() }))
            ))
        }

        test("Attribute primitive") {
            val script = "@<test://#&%?__075029823984_testlkj>"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, AttributePrimitive(Attribute.from("test://#&%?__075029823984_testlkj").getOrElse { TODO() }))
            ))
        }

        test("boolean primitive") {
            val script = "true"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, BooleanPrimitive(true))
            ))
        }

        test("string primitive") {
            val script = "\"Hello\""
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, StringPrimitive(StringLiteral("Hello")))
            ))
        }

        test("basic assignment") {
            val script = "let x = 5"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, LetKeyword),
                tk(4, Identifier("x")),
                tk(6, AssignmentOperator),
                tk(8, IntegerPrimitive(IntegerLiteral(5L)))
            ))
        }

        test("assignment support") {
            val script = "let y = 5\n  let x =4\nx"
            val res = lexer.read(script)
            res shouldBe Either.Right(listOf(
                tk(0, LetKeyword),
                tk(4, Identifier("y")),
                tk(6, AssignmentOperator),
                tk(8, IntegerPrimitive(IntegerLiteral(5L))),
                tk(9, NewLineToken),
                tk(12, LetKeyword),
                tk(16, Identifier("x")),
                tk(18, AssignmentOperator),
                tk(19, IntegerPrimitive(IntegerLiteral(4L))),
                tk(20, NewLineToken),
                tk(21, Identifier("x")),
            ))
        }
    }
}
