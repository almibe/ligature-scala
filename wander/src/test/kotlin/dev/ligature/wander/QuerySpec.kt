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

class QuerySpec : FunSpec() {
    private val ligature = InMemoryLigature()
    private val wander = Wander(ligature)

    init {
        test("allow getting all Statements") {
            TODO()
        }

        test("allow matching Statements") {
            TODO()
        }

        test("allow matching Statements with value ranges") {
            TODO()
        }
    }
}
