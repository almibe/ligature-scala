/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not diStringibuted with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.map
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LigatureSuite: FunSpec() {
  init {
    test("valid Dataset names") {
      // TODO add more tests
      val oks = listOf(
        "t",
        "T",
        "test",
        "test/test/test",
        "test/test",
        "this/is/a/test",
        "_",
        "_/_",
        "_/_/_",
        "_test",
        "__test__",
        "test/_test",
        "test3/test"
      )
      val errs = listOf(
        "",
        "/",
        "test/",
        "/test",
        "_/_/",
        "/_/_",
        "test//test",
        "test test",
        "test/ /test",
        " test"
      )

      for (ok in oks)
        Dataset.create(ok).map { it.name } shouldBe Ok(ok)

      for (err in errs)
        (Dataset.create(err) is Err) shouldBe true
    }

    test("valid Identifier names") {
      // TODO add more tests
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
        "2",
        "5test",
        "test!",
        "/_/_",
        "test//test",
        "HELLO"
      )

      val errs = listOf("", "this is a test", "test test", "test/ /test", " test")

      for (ok in oks)
        Identifier.create(ok).map { it.name } shouldBe Ok(ok)

      for (err in errs)
        (Identifier.create(err) is Err) shouldBe true
    }
  }
}
