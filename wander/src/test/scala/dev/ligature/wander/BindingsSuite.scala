/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class EnvironmentSuite extends FunSuite {
  private val identifier = Name("test")
  private val identifier2 = Name("test2")

  private val value1 = WanderValue.String("this is a test")
  private val value2 = WanderValue.String("this is a test2")
  private val value3 = WanderValue.String("this is a test3")

  test("add single value and read") {
    val environment = Environment(List())
    val environment2 = environment.bindVariable(identifier, value1)
    val res = environment.read(identifier)
    val res2 = environment2.read(identifier)

    assert(res.isLeft)
    assert(environment.read(identifier2).isLeft)
    assertEquals(res2, Right(value1))
    assert(environment2.read(identifier2).isLeft)
  }

  test("test scoping") {
    val environment = Environment(List())
    val environment2 = environment.bindVariable(identifier, value1)
    assertEquals(environment2.read(identifier), Right(value1))

    val environment3 = environment2.newScope()
    assertEquals(environment3.read(identifier), Right(value1))

    val environment4 = environment3.bindVariable(identifier, value2)
    val environment5 = environment4.bindVariable(identifier2, value3)
    assertEquals(environment5.read(identifier), Right(value2))
    assertEquals(environment5.read(identifier2), Right(value3))
  }
}
