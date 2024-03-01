/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.BendValue
import dev.ligature.bend.Field
import dev.ligature.bend.HostFunction
import dev.ligature.bend.Tag
import dev.ligature.bend.TaggedField

val arrayModule: BendValue.Module = BendValue.Module(
  Map(
    Field("length") -> BendValue.Function(
      HostFunction(
        "Get the number of elements in an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              Right((BendValue.Int(value.length), environment))
            case _ => ???
      )
    )
  )
)
