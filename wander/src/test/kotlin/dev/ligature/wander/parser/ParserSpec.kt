/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import arrow.core.Either
import arrow.core.getOrElse
import dev.ligature.wander.lexer.Lexer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ParserSpec : FunSpec() {
    val lexer = Lexer()
    val parser = Parser()

    init {
        test("empty script") {
            val script = ""
            val res = parser.parse(lexer.read(script).getOrElse { TODO("parser error") })
            res shouldBe Either.Right(Script(listOf()))
        }

        test("primitives support") {
            val script = "5"
            val res = parser.parse(lexer.read(script).getOrElse { TODO("parser error") })
            res shouldBe Either.Right(Script(listOf()))
        }
    }
}
