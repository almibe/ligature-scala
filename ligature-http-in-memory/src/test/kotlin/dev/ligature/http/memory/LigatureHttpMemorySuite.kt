/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.memory

import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.http.testsuite.LigatureHttpSuite
import dev.ligature.http.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.ktor.server.application.*

class LigatureHttpMemorySuite: LigatureHttpSuite() {
  override fun Application.instanceModule() {
    routes(InMemoryLigature())
  }
}

class MyTests : StringSpec({
  "length should return size of string" {
    "hello".length shouldBe 5
  }
  "startsWith should test for a prefix" {
    "world" should startWith("wor")
  }
})
