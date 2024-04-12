/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.HostFunction
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Tag
import dev.ligature.bend.BendValue
import dev.ligature.bend.Field

val stringModule: BendValue.Module = BendValue.Module(
  Map(
    Field("replace") -> BendValue.Function(
      HostFunction(
        "Replace all character sequences with another and create a new String.",
        Seq(
          TaggedField(Field("toMatch"), Tag.Untagged),
          TaggedField(Field("replacement"), Tag.Untagged),
          TaggedField(Field("data"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(
                  BendValue.String(toMatch),
                  BendValue.String(replacement),
                  BendValue.String(data)
                ) =>
              Right(
                (
                  BendValue.String(data.replace(toMatch, replacement)),
                  environment
                )
              )
      )
    ),
    Field("toBytes") -> BendValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("String"), Field("toBytes"))),
        "Get a String encoded as Bytes.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.String(value)) =>
              Right(
                (
                  ???,//BendValue.Bytes(StringBinding.stringToEntry(value).getBytesUnsafe().toSeq),
                  environment
                )
              )
      )
    ),
    Field("fromBytes") -> BendValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("String"), Field("fromBytes"))),
        "Decode Bytes to a String.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Bytes(value)) =>
              Right(
                (
                  ???,//BendValue.String(StringBinding.entryToString(ArrayByteIterable(value.toArray))),
                  environment
                )
              )
      )
    )
  )
)
