/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class EnvironmentSuite extends FunSuite {
  private val identifier = TaggedName(Name("test"), Tag.Untagged)
  private val identifier2 = TaggedName(Name("test2"), Tag.Untagged)

  private val value1 = WanderValue.String("this is a test")
  private val value2 = WanderValue.String("this is a test2")
  private val value3 = WanderValue.String("this is a test3")

  test("add single value and read") {
    val environment = Environment(List())
    val environment2 = environment.bindVariable(identifier, value1).getOrElse(???)
    val res = environment.read(identifier.name)
    val res2 = environment2.read(identifier.name)

    assert(res.isLeft)
    assert(environment.read(identifier2.name).isLeft)
    assertEquals(res2, Right(value1))
    assert(environment2.read(identifier2.name).isLeft)
  }

  test("test scoping") {
    val environment = Environment(List())
    val environment2 = environment.bindVariable(identifier, value1).getOrElse(???)
    assertEquals(environment2.read(identifier.name), Right(value1))

    val environment3 = environment2.newScope()
    assertEquals(environment3.read(identifier.name), Right(value1))

    val environment4 = environment3.bindVariable(identifier, value2).getOrElse(???)
    val environment5 = environment4.bindVariable(identifier2, value3).getOrElse(???)
    assertEquals(environment5.read(identifier.name), Right(value2))
    assertEquals(environment5.read(identifier2.name), Right(value3))
  }
}
