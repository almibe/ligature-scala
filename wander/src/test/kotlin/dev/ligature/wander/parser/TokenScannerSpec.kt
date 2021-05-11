/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.lexer.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@kotlin.ExperimentalUnsignedTypes
class TokenScannerSpec : FunSpec() {
    init {
        test("empty token list") {
            val tokens = listOf<WanderToken>()
            val ts = TokenScanner(tokens)
            ts.isComplete() shouldBe true
            ts.peek() shouldBe null
            ts.peek(100U) shouldBe null
            ts.skip(100U)
            ts.isComplete() shouldBe true
            ts.peek() shouldBe null
            ts.peek(100U) shouldBe null
        }

        test("single token list") {
            val tokens = listOf<WanderToken>(WanderToken(0, LetKeyword))
            val ts = TokenScanner(tokens)
            ts.isComplete() shouldBe false
            ts.peek() shouldBe WanderToken(0, LetKeyword)
            ts.peek(1U) shouldBe null
            ts.skip(1U)
            ts.isComplete() shouldBe true
            ts.peek() shouldBe null
            ts.peek(100U) shouldBe null
        }

        test("multiple token list") {
            val tokens = listOf<WanderToken>(
                WanderToken(0, LetKeyword),
                WanderToken(10, Identifier("x")),
                WanderToken(20, AssignmentOperator),
                WanderToken(30, EndOfScriptToken)
            )
            val ts = TokenScanner(tokens)
            ts.isComplete() shouldBe false
            ts.peek() shouldBe WanderToken(0, LetKeyword)
            ts.peek(1U) shouldBe WanderToken(10, Identifier("x"))
            ts.skip(1U)
            ts.peek() shouldBe WanderToken(10, Identifier("x"))
            ts.isComplete() shouldBe false
            ts.skip(2U)
            ts.isComplete() shouldBe false
            ts.peek() shouldBe WanderToken(30, EndOfScriptToken)
            ts.peek(1U) shouldBe null
            ts.peek(100U) shouldBe null
            ts.skip(1U)
            ts.isComplete() shouldBe true
        }
    }
}
