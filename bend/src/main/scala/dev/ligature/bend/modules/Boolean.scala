/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.BendValue
import dev.ligature.bend.BendError
import dev.ligature.bend.HostFunction
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Tag
import dev.ligature.bend.Field
import jetbrains.exodus.bindings.BooleanBinding
import jetbrains.exodus.ArrayByteIterable

val boolModule: BendValue.Module = BendValue.Module(
  Map(
    Field("not") -> BendValue.Function(
      HostFunction(
        "Perform a not operation on a Bool value.",
        Seq(TaggedField(Field("value"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Bool(value)) => Right((BendValue.Bool(!value), environment))
            case _                          => Left(BendError("Unexpected input " + args))
      )
    ),
    Field("and") -> BendValue.Function(
      HostFunction(
        "Perform a logical and on two Bools.",
        Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Bool(left), BendValue.Bool(right)) =>
              Right((BendValue.Bool(left && right), environment))
            case _ => ???
      )
    ),
    Field("or") -> BendValue.Function(
      HostFunction(
        "Perform a logical or on two Bools.",
        Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
        Tag.Untagged,
        (_, _) => ???
      )
    ),
    Field("toBytes") -> BendValue.Function(
      HostFunction(
        "Encod a Bool as Bytes.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Bool(value)) =>
              Right(
                (
                  BendValue.Bytes(BooleanBinding.booleanToEntry(value).getBytesUnsafe().toSeq),
                  environment
                )
              )
      )
    ),
    Field("fromBytes") -> BendValue.Function(
      HostFunction(
        "Decode Bytes to a Bool.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Bytes(value)) =>
              Right(
                (
                  BendValue.Bool(BooleanBinding.entryToBoolean(ArrayByteIterable(value.toArray))),
                  environment
                )
              )
      )
    )
  )
)
