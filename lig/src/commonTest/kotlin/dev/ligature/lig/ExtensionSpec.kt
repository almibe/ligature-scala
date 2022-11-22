/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.getOrElse
import arrow.core.Either.Right
import dev.ligature.Dataset
import dev.ligature.inmemory.InMemoryLigature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count

class ExtensionSpec : FunSpec() {
  init {
    val ds = Dataset.create("test").getOrElse { TODO() }

    test("run empty Lig String") {
      val instance = InMemoryLigature()
      instance.createDataset(ds)
      instance.insertLig(ds, "")
      val count = instance.query(ds) {
        it.allStatements().count()
      }
      count.shouldBe(Right(0))
    }

    test("add Statements to existing Dataset") {
      val instance = InMemoryLigature()
      instance.createDataset(ds)
      instance.insertLig(ds, """
        <a> <b> <c>
        <b> <c> <d>
        <b> <c> <e>
        <b> <c> <e>
      """.trimIndent())
      val count = instance.query(ds) {
        it.allStatements().count()
      }
      count.shouldBe(Right(3))
    }

    test("remove Statements from existing Dataset") {
      val instance = InMemoryLigature()
      instance.createDataset(ds)
      instance.insertLig(ds, """
        <a> <b> <c>
        <b> <c> <d>
        <b> <c> <e>
        <b> <c> <e>
      """.trimIndent())
      instance.removeLig(ds, """
        <a> <b> <e>
        <b> <c> <d>
        <b> <c> <e>
        <b> <c> <e>
      """.trimIndent())
      val count = instance.query(ds) {
        it.allStatements().count()
      }
      count.shouldBe(Right(1))
    }
  }
}
