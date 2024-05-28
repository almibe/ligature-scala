/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.modules.std
import dev.ligature.wander.*

class LambdaSuite extends munit.FunSuite {
  // val testFunction = HostFunction(
  //   UName("test"),
  //   "",
  //   Seq(
  //     TaggedField(UName("a"), Tag.Untagged),
  //     TaggedField(UName("b"), Tag.Untagged),
  //     TaggedField(UName("c"), Tag.Untagged),
  //     TaggedField(UName("d"), Tag.Untagged)
  //   ),
  //   Tag.Untagged,
  //   (args, environment) => Right((WanderValue.Int(5), environment))
  // )

  val environment = std() // .addHostFunctions(Seq(testFunction))

  def check(script: String, expected: WanderValue): Unit =
    assertEquals(
      run(script, environment).getOrElse(???)._1,
      expected
    )

  // test("partially apply a host function") {
  //   val script = "test 1"
  //   val expected = WanderValue.Function(
  //     dev.ligature.wander.PartialFunction(Seq(WanderValue.Int(1)), testFunction)
  //   )
  //   check(script, expected)
  // }

  // test("partially apply a host function multiple times".only) {
  //   val script = "test = test 1, test = test 2, test = test 3, test 4"
  //   val expected = WanderValue.Int(5)
  //   check(script, expected)
  // }
}
