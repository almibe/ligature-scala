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

class CommandSpec : FunSpec() {
    private val ligature = InMemoryLigature()
    private val wander = Wander(ligature)

    init {
        test("allow adding Statements") {
//            val interpreter = Interpreter(ligature)
//            val script = "x"
//            val scope = Scope()
//            scope.addSymbol("x", IntegerWanderValue(IntegerLiteral(5L)))
//            val res = interpreter.run(script, scope)
//            res shouldBe Either.Right(IntegerWanderValue(IntegerLiteral(5L)))
            TODO()
        }

        test("allow adding Statements with generated Entities") {
            TODO()
        }

        test("allow removing Statements") {
            TODO()
        }

        test("allow canceling scripts") {
            TODO()
        }
    }
}
