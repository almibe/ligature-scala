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

class FunctionSpec : FunSpec() {
    private val ligature = InMemoryLigature()
    private val wander = Wander(ligature)

    init {
        test("call a function from scope with zero params") {
            val interpreter = Interpreter(ligature)
            val script = "x()"
            val scope = Scope()
            scope.addSymbol("x", WanderFunction(listOf()) { Either.Right(IntegerWanderValue(IntegerLiteral(5L))) })
            val res = interpreter.run(script, scope)
            res shouldBe Either.Right(IntegerWanderValue(IntegerLiteral(5L)))
        }

        test("call a function from scope with one param") {
            val interpreter = Interpreter(ligature)
            val script = "plusOne(41)"
            val scope = Scope()
            scope.addSymbol("plusOne", WanderFunction(listOf(IntegerWanderValue::class)) { args ->
                when (val toInc = args[0]) {
                    is IntegerWanderValue -> Either.Right(IntegerWanderValue(IntegerLiteral(toInc.value.value + 1)))
                    else -> TODO()
                }
            })
            val res = interpreter.run(script, scope)
            res shouldBe Either.Right(IntegerWanderValue(IntegerLiteral(5L)))
        }

        test("expect an error when calling a function with incorrect params") {
            TODO()
        }

        test("define and call a function with zero params") {
            TODO()
        }

        test("define and call a function with one param") {
            TODO()
        }
    }
}
