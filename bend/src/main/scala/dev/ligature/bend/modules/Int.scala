/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.HostFunction
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Tag
import dev.ligature.bend.BendValue
import dev.ligature.bend.Field
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.ArrayByteIterable

val intModule: BendValue.Module = BendValue.Module(
  Map(
    Field("add") -> BendValue.Function(
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
            case Seq(BendValue.Int(left), BendValue.Int(right)) =>
              Right((BendValue.Int(left + right), environment))
            case _ => ???
      )
    ),
    Field("toBytes") -> BendValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Int"), Field("toBytes"))),
        "Encode an Int as Bytes.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Int(value)) =>
              Right(
                (
                  BendValue.Bytes(LongBinding.longToEntry(value).getBytesUnsafe().toSeq),
                  environment
                )
              )
      )
    ),
    Field("fromBytes") -> BendValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Int"), Field("fromBytes"))),
        "Decode Bytes to an Int.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Bytes(value)) =>
              Right(
                (
                  BendValue.Int(LongBinding.entryToLong(ArrayByteIterable(value.toArray))),
                  environment
                )
              )
      )
    )
  )
)
