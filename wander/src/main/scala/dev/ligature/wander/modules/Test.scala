/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Field
import dev.ligature.wander.Tag
import dev.ligature.wander.BendValue
import dev.ligature.wander.BendError

val testingModule: BendValue.Module = BendValue.Module(
  Map(
    Field("assertEq") -> BendValue.Function(
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
            case Seq(description: BendValue.String, left: BendValue, right: BendValue) =>
              if left != right then
                Left(
                  BendError(s"$description failed $left != $right")
                ) // TODO print value correctly
              else Right(BendValue.Module(Map()), environment)
            case _ => Left(BendError(s"Test.assertEq failed: $arguments"))
          }
      )
    )
  )
)
