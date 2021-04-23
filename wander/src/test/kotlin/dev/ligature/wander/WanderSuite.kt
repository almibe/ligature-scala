/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class WanderSuite : FunSpec() {
    private val wander = Wander()

    fun readFile(name: String): String {
        TODO()
    }

    init {
//        test("integer literal") {
//            val text = readFile("intLiteral.wander")
//            val res = wander.run(text)
//            val exp = readFile("")
//            res shouldBe 5
//        }
//
//        test("let error") {
//            val text = readFile("let-err.wander")
//        }
    }
    //TODO read in .wander program
    //TODO run it
    //TODO read in .result file
    //TODO assert results are the same
}
