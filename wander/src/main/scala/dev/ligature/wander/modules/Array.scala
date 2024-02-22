/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.Environment
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.HostFunction
import dev.ligature.wander.Tag
import dev.ligature.wander.FieldPath
import dev.ligature.wander.TaggedField

val arrayModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("length") -> WanderValue.Function(
      HostFunction(
        "Get the number of elements in an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              Right((WanderValue.Int(value.length), environment))
            case _ => ???
      )
    )
  )
)
