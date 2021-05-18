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

class PrimitiveSpec : FunSpec() {
    private val ligature = InMemoryLigature()
    private val wander = Wander(ligature)

    init {
        test("Unit support") {
            val script = ""
            val res = wander.run(script)
            res shouldBe Either.Right(UnitPrimitive)
        }

        test("integer support") {
            val script = "5"
            val res = wander.run(script)
            res shouldBe Either.Right(IntegerPrimitive(IntegerLiteral(5L)))
        }

        test("float support") {
            val script = "5.345"
            val res = wander.run(script)
            res shouldBe Either.Right(FloatPrimitive(FloatLiteral(5.345)))
        }

        test("boolean support") {
            val trueScript = "true"
            val trueRes = wander.run(trueScript)
            trueRes shouldBe Either.Right(BooleanPrimitive(true))

            val falseScript = "false"
            val falseRes = wander.run(falseScript)
            falseRes shouldBe Either.Right(BooleanPrimitive(false))
        }

        test("string support") {
            val script = "\"Hello  \\n  World! \""
            val res = wander.run(script)
            res shouldBe Either.Right(StringPrimitive(StringLiteral("Hello  \\n  World! ")))
        }

        test("entity support") {
            val script = "<hello:world>"
            val res = wander.run(script)
            res shouldBe Either.Right(EntityPrimitive(Entity.from("hello:world").orNull()!!))
        }

        test("attribute support") {
            val script = "@<hello:world:attribute>"
            val res = wander.run(script)
            res shouldBe Either.Right(AttributePrimitive(Attribute.from("hello:world:attribute").orNull()!!))
        }
    }
}
