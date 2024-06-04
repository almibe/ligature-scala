/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.HostFunction
import dev.ligature.wander.Tag
import dev.ligature.wander.TaggedField

val networkModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("length") -> WanderValue.Function(
      HostFunction(
        "Get the number of elements in a Network.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Network(value)) =>
              Right((WanderValue.Int(value.size), environment))
            case _ => ???
      )
    ),
    Field("merge") -> WanderValue.Function(
      HostFunction(
        "Merge two Networks.",
        Seq(TaggedField(Field("array"), Tag.Untagged), TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Network(lvalue), WanderValue.Network(rvalue)) =>
              Right((WanderValue.Network(lvalue ++ rvalue), environment))
            case _ => ???
      )
    )
  )
)
