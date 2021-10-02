/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import dev.ligature.*
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.wander.interpreter.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class WanderValueSpec : FunSpec() {
    private val ligature = InMemoryLigature()
    private val wander = Wander()

    init {
        test("Unit support") {
            val script = ""
            val res = wander.run(script)
            res shouldBe Either.Right(UnitWanderValue)
        }

        test("integer support") {
            val script = "5"
            val res = wander.run(script)
            res shouldBe Either.Right(IntegerWanderValue(IntegerLiteral(5L)))
        }

//        test("float support") {
//            val script = "5.345"
//            val res = wander.run(script)
//            res shouldBe Either.Right(FloatWanderValue(FloatLiteral(5.345)))
//        }

        test("boolean support") {
            val trueScript = "true"
            val trueRes = wander.run(trueScript)
            trueRes shouldBe Either.Right(BooleanWanderValue(true))

            val falseScript = "false"
            val falseRes = wander.run(falseScript)
            falseRes shouldBe Either.Right(BooleanWanderValue(false))
        }

        test("string support") {
            val script = "\"Hello  \\n  World! \""
            val res = wander.run(script)
            res shouldBe Either.Right(StringWanderValue(StringLiteral("Hello  \\n  World! ")))
        }

        test("entity support") {
            val script = "<hello:world>"
            val res = wander.run(script)
            res shouldBe Either.Right(IdentifierWanderValue(Identifier("hello:world").orNull()!!))
        }

        test("attribute support") {
            val script = "<hello:world:attribute>"
            val res = wander.run(script)
            res shouldBe Either.Right(IdentifierWanderValue(Identifier("hello:world:attribute").orNull()!!))
        }

        test("statement support") {
            val script = "<hello> @<hello:world:attribute> <world> <_7582447583758>"
            val res = wander.run(script)
            res shouldBe Either.Right(StatementWanderValue(
                Statement(
                    Identifier("hello").orNull()!!,
                    Identifier("hello:world:attribute").orNull()!!,
                    Identifier("world").orNull()!!,
                    Identifier("_7582447583758").orNull()!!,
                )
            ))
        }

        test("multiple values") {
            val script = "12 \"hello\" 234.2341 @<hello:world:attribute>"
            val res = wander.run(script)
            res shouldBe Either.Right(IdentifierWanderValue(Identifier("hello:world:attribute").orNull()!!))
        }
    }
}
