/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.Environment
import dev.ligature.bend.WanderValue
import dev.ligature.bend.Field
import dev.ligature.bend.HostFunction
import dev.ligature.bend.Tag
import dev.ligature.bend.FieldPath
import dev.ligature.bend.TaggedField

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
