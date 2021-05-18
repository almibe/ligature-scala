/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.IntegerLiteral
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.wander.interpreter.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LetStatementSpec : FunSpec() {
    private val ligature = InMemoryLigature()
    private val wander = Wander(ligature)

    init {
        test("basic assignment") {
            val script = "let x = 5"
            val res = wander.run(script)
            res shouldBe Either.Right(UnitPrimitive)
        }

        test("basic assignment w/ return") {
            val script = "let x = 5\nx"
            val res = wander.run(script)
            res shouldBe Either.Right(IntegerPrimitive(IntegerLiteral(5L)))
        }

        test("basic assignment w/ return w/ weird spacing") {
            val script = "let\n x\n =\n 55\nx"
            val res = wander.run(script)
            res shouldBe Either.Right(IntegerPrimitive(IntegerLiteral(55L)))
        }
    }
}
