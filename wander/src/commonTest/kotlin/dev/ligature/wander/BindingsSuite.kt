/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.flatMap
import arrow.core.getOrElse

import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.Name
import dev.ligature.Identifier
import dev.ligature.StringLiteral
import dev.ligature.wander.parser.WanderValue

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BindingsSuite: FunSpec() {
  private val identifier = Name("test")
  private val identifier2 = Name("test2")

  private val value1 = LigatureValue(StringLiteral("this is a test"))
  private val value2 = LigatureValue(StringLiteral("this is a test2"))
  private val value3 = LigatureValue(StringLiteral("this is a test3"))

  init {
    test("add single value and read") {
      val binding = Bindings()
      val binding2 = binding.bindVariable(identifier, value1)
      val res = binding.read(identifier)
      val res2 = binding2.flatMap { it.read(identifier) }

      res.isLeft() shouldBe true
      binding.read(identifier2).isLeft() shouldBe true
      res2 shouldBe Right(value1)
      binding2.map { it.read(identifier2).isLeft() shouldBe true }
    }

    test("test scoping") {
      val bindings = Bindings()
      val bindings2 = bindings.bindVariable(identifier, value1).getOrElse { throw Error("Unexpected result.") }
      bindings2.read(identifier) shouldBe Right(value1)

      val bindings3 = bindings2.newScope()
      bindings3.read(identifier) shouldBe Right(value1)

      val bindings4 = bindings3.bindVariable(identifier, value2).getOrElse { throw Error("Unexpected result.") }
      val bindings5 = bindings4.bindVariable(identifier2, value3).getOrElse { throw Error("Unexpected result.") }
      bindings5.read(identifier) shouldBe Right(value2)
      bindings5.read(identifier2) shouldBe Right(value3)
    }
  }
}
