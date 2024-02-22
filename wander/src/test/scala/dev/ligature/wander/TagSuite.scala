/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.modules.std
import munit.FunSuite

class TagSuite extends FunSuite {
  // def check(script: String, expected: WanderValue) =
  //   assertEquals(
  //     run(script, std()) match {
  //       case Left(err)         => throw err
  //       case Right((value, _)) => value
  //     },
  //     expected
  //   )

  // def checkFail(script: String, messageContains: String) =
  //   val res = run(script, std())
  //   assert(res.isLeft)
  //   assert(res.left.getOrElse(???).userMessage.contains(messageContains))

  // test("run passing tag assignment") {
  //   val script = "x: Core.Int = 5"
  //   val result = WanderValue.Int(5)
  //   check(script, result)
  // }

  // test("run failing tag assignment") {
  //   val script = "x: Core.Bool = 5"
  //   checkFail(script, "Tag Function")
  // }

  // test("run failing tag with Host Function") {
  //   val script = "Bool.not 5"
  //   checkFail(script, "Tag Function")
  // }

  // test("define and use passing Tag in lambda") {
  //   val script = "five = \\i -> Core.eq i 5, x: five = 5"
  //   val result = WanderValue.Int(5)
  //   check(script, result)
  // }

  // test("define and use failing Tag in lambda") {
  //   val script = "five = \\i -> Core.eq i 5, x: five = 4"
  //   checkFail(script, "Tag Function")
  // }

  // test("bind lambda with function tag that passes") {
  //   val script = "increment: Core.Int -> Core.Int = \\i -> Int.add i 1, nothing"
  //   val result = WanderValue.Nothing
  //   check(script, result)
  // }

  // test("bind lambda with function tag that fails".ignore) {
  //   val script = "increment: Core.Int -> Core.Int -> Core.Bool = \\i -> Int.add i 1"
  //   checkFail(script, "Tag Function")
  // }

  // test("run failing tag used with lambda".ignore) {
  //   val script = "increment: Core.Int -> Core.Int = \\i -> Int.add i 1, increment false"
  //   checkFail(script, "Tag Function")
  // }

  // test("run failing tag used with lambda in return".ignore) {
  //   val script = "increment: Core.Int -> Core.Int = \\i -> true, increment 1"
  //   checkFail(script, "")
  // }
}
