/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not diStringibuted with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature

import arrow.core.None
import arrow.core.getOrElse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LigatureSpec : FunSpec() {
    init {
        test("Dataset name validation") {
            //TODO add more tests
            val oks = listOf(
                "t",
                "T",
                "test",
                "_",
                "_test",
                "test23480928492",
                "__test__",)
            val errs = listOf(
                "",
                "3",
                "3test",
                "test/test/test",
                "test/test",
                "this/is/a/test",
                "/",
                "_/_",
                "_/_/_",
                "test/",
                "/test",
                "_/_/",
                "/_/_",
                "test//test",
                "test test",
                "test/ /test",
                "test/_test",
                "test3/test",
                " test")
            for(ok in oks) {
                Dataset.from(ok).getOrElse { TODO("Invalid name $ok") }.name shouldBe ok
            }

            for(err in errs) {
                Dataset.from(err) shouldBe None
            }
        }

        test("valid Identifier names") {
            //TODO add more tests
            val oks = listOf(
                    "test",
                    "test_test_test",
                    "test_test",
                    "this1_is2_a_test",
                    "_",
                    "_test",
                    "__test__",
                    "testTest",
                    "G",
                    "test!",
                    "test//test",
                    "HELLO")

            val errs = listOf(
                    "",
                    "2",
                    "5test",
                    "this is a test",
                    "/_/_",
                    "test test",
                    "test/ /test",
                    " test")

            for(ok in oks) {
                Identifier(ok).getOrElse { TODO() }.id shouldBe ok
            }

            for(err in errs) {
                Identifier(err) shouldBe None
            }
        }
    }
}
