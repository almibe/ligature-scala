/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not diStringibuted with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature

//import munit.FunSuite
//
//class LigatureSpec extends FunSuite {
//    test("valid Dataset names") {
//        //TODO add more tests
//        val oks = List(
//                "t",
//                "T",
//                "test",
//                "test/test/test",
//                "test/test",
//                "this/is/a/test",
//                "_",
//                "_/_",
//                "_/_/_",
//                "_test",
//                "__test__",
//                "test/_test",
//                "test3/test")
//        val errs = List(
//                "",
//                "/",
//                "test/",
//                "/test",
//                "_/_/",
//                "/_/_",
//                "test//test",
//                "test test",
//                "test/ /test",
//                " test")
//
//        for(ok <- oks) {
//        assertEquals(Dataset.fromString(ok).get.name, ok)
//    }
//
//        for(err <- errs) {
//        assertEquals(Dataset.fromString(err), None)
//    }
//    }
//
//    test("valid Attribute names") {
//        //TODO add more tests
//        val oks = List(
//                "test",
//                "test_test_test",
//                "test_test",
//                "this1_is2_a_test",
//                "_",
//                "_test",
//                "__test__",
//                "testTest",
//                "G",
//                "HELLO")
//
//        val errs = List(
//                "",
//                "2",
//                "5test",
//                "test!",
//                "this is a test",
//                "/_/_",
//                "test//test",
//                "test test",
//                "test/ /test",
//                " test")
//
//        for(ok <- oks) {
//        assertEquals(Attribute.fromString(ok).get.name, ok)
//    }
//
//        for(err <- errs) {
//        assertEquals(Attribute.fromString(err), None)
//    }
//    }
//}
