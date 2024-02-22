/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.HostFunction
import dev.ligature.wander.Environment
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Field
import dev.ligature.wander.FieldPath
import dev.ligature.wander.Tag
import dev.ligature.wander.WanderValue
import dev.ligature.wander.WanderError

val testingModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("assertEq") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Test"), Field("assertEq"))),
        "Check if two values are equal and fail if they are not.",
        Seq(
          TaggedField(Field("description"), Tag.Untagged),
          TaggedField(Field("left"), Tag.Untagged),
          TaggedField(Field("right"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          arguments match {
            case Seq(description: WanderValue.String, left: WanderValue, right: WanderValue) =>
              if left != right then
                Left(
                  WanderError(s"$description failed $left != $right")
                ) // TODO print value correctly
              else Right(WanderValue.Module(Map()), environment)
            case _ => Left(WanderError(s"Test.assertEq failed: $arguments"))
          }
      )
    )
  )
)
