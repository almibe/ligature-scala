/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.getOrElse
import arrow.core.Either.Right
import dev.ligature.Dataset
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.wander.model.Element
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ExtensionsSpec : FunSpec() {
  private val ds = Dataset.create("test").getOrElse { TODO() }

  init {
    test("run basic Wander") {
      val instance = InMemoryLigature()
      val result = instance.wanderQuery(ds, "true")
      result.shouldBe(Right(Element.BooleanLiteral(true)))
    }
  }
}
