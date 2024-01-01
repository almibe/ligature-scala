/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.HostFunction
import dev.ligature.wander.Environment
import dev.ligature.wander.*

val testingLibrary: Seq[HostFunction] = Seq(
  HostFunction(
    Name("Test.assertEq"),
    "Check if two values are equal and fail if they are not.",
    Seq(
      TaggedName(Name("description"), Tag.Single(Name("Core.String"))),
      TaggedName(Name("left"), Tag.Single(Name("Core.Any"))),
      TaggedName(Name("right"), Tag.Single(Name("Core.Any")))
    ),
    Tag.Single(Name("Core.Nothing")),
    (arguments, environment) =>
      arguments match {
        case Seq(description: WanderValue.String, left: WanderValue, right: WanderValue) =>
          if left != right then
            Left(WanderError(s"$description failed $left != $right")) // TODO print value correctly
          else Right(WanderValue.Nothing, environment)
        case _ => Left(WanderError(s"Test.assertEq failed: $arguments"))
      }
  )
)
