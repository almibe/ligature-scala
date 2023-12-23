/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.Token
import dev.ligature.wander.preludes.common

class IfExpressionSuite extends munit.FunSuite {
  // def check(script: String) = run(script, common()).getOrElse(???)

  // test("if true") {
  //   val result = check("if true 7 else 6")
  //   val expected = WanderValue.IntValue(7)
  //   assertEquals(result, expected)
  // }
//   test("if false w/ function call") {
//     val result = check("if and(false true) 24601 else -1")
//     val expected = WanderValue.IntValue(24601)
//     assertEquals(result, expected)
//   }
//   test("elsif") {
//     val script = """let x = true
//                    |let y = false
//                    |if y {
//                    |    1
//                    |} else if x {
//                    |    2
//                    |} else if false {
//                    |    3
//                    |} else {
//                    |    4
//                    |}""".stripMargin
//     val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(2))
//     check(script, result)
//   }
//   test("conditional w/ variables and functions") {
//     val script = """let x = true
//                |let y = false
//                |if y {
//                |    1
//                |} else if not(x) {
//                |    2
//                |} else {
//                |    3
//                |}""".stripMargin
//     val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(3))
//     check(script, result)
//   }
}
