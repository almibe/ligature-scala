/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import javax.lang.model.`type`.ErrorType
import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Tag
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.FieldPath
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.ArrayByteIterable

val intModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("add") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Int"), Field("add"))),
        "Add two Ints.",
        Seq(
          TaggedField(Field("left"), Tag.Untagged), // Tag.Single(Name("Core.Int"))),
          TaggedField(Field("right"), Tag.Untagged) // Tag.Single(Name("Core.Int")))
        ),
        Tag.Untagged, // Tag.Single(Field("Core.Int")),
        (args, environment) =>
          args match
            case Seq(WanderValue.Int(left), WanderValue.Int(right)) =>
              Right((WanderValue.Int(left + right), environment))
            case _ => ???
      )
    ),
    Field("toBytes") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Int"), Field("toBytes"))),
        "Encode an Int as Bytes.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Int(value)) =>
              Right(
                (
                  WanderValue.Bytes(LongBinding.longToEntry(value).getBytesUnsafe().toSeq),
                  environment
                )
              )
      )
    ),
    Field("fromBytes") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Int"), Field("fromBytes"))),
        "Decode Bytes to an Int.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Bytes(value)) =>
              Right(
                (
                  WanderValue.Int(LongBinding.entryToLong(ArrayByteIterable(value.toArray))),
                  environment
                )
              )
      )
    )
  )
)
