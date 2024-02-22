/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import munit.FunSuite

class EnvironmentSuite extends FunSuite {
  private val identifier = TaggedField(Field("test"), Tag.Untagged)
  private val identifier2 = TaggedField(Field("test2"), Tag.Untagged)

  private val value1 = WanderValue.String("this is a test")
  private val value2 = WanderValue.String("this is a test2")
  private val value3 = WanderValue.String("this is a test3")

  test("add single value and read") {
    val environment = Environment()
    val environment2 = environment.bindVariable(identifier, value1).getOrElse(???)
    val res = environment.read(identifier.field)
    val res2 = environment2.read(identifier.field)

    assertEquals(res, Right(None))
    assertEquals(environment.read(identifier2.field), Right(None))
    assertEquals(res2, Right(Some(value1)))
    assertEquals(environment2.read(identifier2.field), Right(None))
  }

  test("test scoping") {
    val environment = Environment()
    val environment2 = environment.bindVariable(identifier, value1).getOrElse(???)
    assertEquals(environment2.read(identifier.field), Right(Some(value1)))

    val environment3 = environment2.newScope()
    assertEquals(environment3.read(identifier.field), Right(Some(value1)))

    val environment4 = environment3.bindVariable(identifier, value2).getOrElse(???)
    val environment5 = environment4.bindVariable(identifier2, value3).getOrElse(???)
    assertEquals(environment5.read(identifier.field), Right(Some(value2)))
    assertEquals(environment5.read(identifier2.field), Right(Some(value3)))
  }
}
