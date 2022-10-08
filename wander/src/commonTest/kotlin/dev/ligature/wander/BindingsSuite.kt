/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either.Right
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.model.Element
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BindingsSuite: FunSpec() {
  private val name1 = "test"
  private val name2 = "test2"

  private val value1 = Element.StringLiteral("this is a test")
  private val value2 = Element.StringLiteral("this is a test2")
  private val value3 = Element.StringLiteral("this is a test3")

  init {
    test("add single value and read") {
      val binding = Bindings()
      binding.bindVariable(name1, value1)
      binding.read(name1, Element::class) shouldBe Right(value1)
      binding.read(name2, Element::class).isLeft() shouldBe true
    }

    test("test scoping") {
      val bindings = Bindings()
      bindings.bindVariable(name1, value1)
      bindings.read(name1, Element::class) shouldBe Right(value1)

      bindings.addScope()
      bindings.read(name1, Element::class) shouldBe Right(value1)

      bindings.bindVariable(name1, value2)
      bindings.bindVariable(name2, value3)
      bindings.read(name1, Element::class) shouldBe Right(value2)
      bindings.read(name2, Element::class) shouldBe Right(value3)
    }
  }
}
