/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Tag
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.FieldPath
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.ArrayByteIterable

val stringModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("toBytes") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("String"), Field("toBytes"))),
        "Get a String encoded as Bytes.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.String(value)) =>
              Right(
                (
                  WanderValue.Bytes(StringBinding.stringToEntry(value).getBytesUnsafe().toSeq),
                  environment
                )
              )
      )
    ),
    Field("fromBytes") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("String"), Field("fromBytes"))),
        "Decode Bytes to a String.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Bytes(value)) =>
              Right(
                (
                  WanderValue.String(StringBinding.entryToString(ArrayByteIterable(value.toArray))),
                  environment
                )
              )
      )
    )
  )
)
