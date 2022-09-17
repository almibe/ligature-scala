/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either.Right
import arrow.core.flatMap
import arrow.core.getOrElse

import dev.ligature.wander.parser.LigatureValue
import dev.ligature.wander.parser.Name
import dev.ligature.StringLiteral

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BindingsSuite: FunSpec() {
  private val name1 = Name("test")
  private val name2 = Name("test2")

  private val value1 = LigatureValue(StringLiteral("this is a test"))
  private val value2 = LigatureValue(StringLiteral("this is a test2"))
  private val value3 = LigatureValue(StringLiteral("this is a test3"))

  init {
    test("add single value and read") {
      val binding = Bindings()
      binding.bindVariable(name1, value1)
      binding.read(name1) shouldBe Right(value1)
      binding.read(name2).isLeft() shouldBe true
    }

    test("test scoping") {
      val bindings = Bindings()
      bindings.bindVariable(name1, value1)
      bindings.read(name1) shouldBe Right(value1)

      bindings.addScope()
      bindings.read(name1) shouldBe Right(value1)

      bindings.bindVariable(name1, value2)
      bindings.bindVariable(name2, value3)
      bindings.read(name1) shouldBe Right(value2)
      bindings.read(name2) shouldBe Right(value3)
    }
  }
}
