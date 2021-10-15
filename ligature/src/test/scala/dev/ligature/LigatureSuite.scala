/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not diStringibuted with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature

import munit.FunSuite

class LigatureSuite extends FunSuite {
  test("valid Dataset names") {
    //TODO add more tests
    val oks = List(
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
      "test3/test")
    val errs = List(
      "",
      "/",
      "test/",
      "/test",
      "_/_/",
      "/_/_",
      "test//test",
      "test test",
      "test/ /test",
      " test")

    for(ok <- oks) {
      assertEquals(Dataset.fromString(ok).getOrElse(throw RuntimeException(s"Invalid Dataset $ok")).name, ok)
    }

    for(err <- errs) {
      assertEquals(Dataset.fromString(err).isLeft, true)
    }
  }

  test("valid Identifier names") {
    //TODO add more tests
    val oks = List(
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
      "HELLO")

    val errs = List(
      "",
      "this is a test",
      "test test",
      "test/ /test",
      " test")

    for(ok <- oks) {
      assertEquals(Identifier.fromString(ok).getOrElse(throw RuntimeException(s"Invalid Identifier $ok")).name, ok)
    }

    for(err <- errs) {
      assertEquals(Identifier.fromString(err).isLeft, true)
    }
  }
}
