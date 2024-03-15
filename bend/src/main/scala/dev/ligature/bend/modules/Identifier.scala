/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.HostFunction
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Field
import dev.ligature.bend.Tag
import dev.ligature.bend.BendValue
import dev.ligature.LigatureValue

val identifierModule: BendValue.Module = BendValue.Module(
  Map(
    Field("toString") -> BendValue.Function(
      HostFunction(
        "Convert an Identifier to a String.",
        Seq(
          TaggedField(Field("identifier"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          arguments match
            case Seq(BendValue.Identifier(value)) =>
              Right((BendValue.String(s"`${value.value}`"), environment))
            case _ => ???
      )
    ),
    Field("value") -> BendValue.Function(
      HostFunction(
        "Get value of Identifier as a String.",
        Seq(
          TaggedField(Field("identifier"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          arguments match
            case Seq(BendValue.Identifier(value)) =>
              Right((BendValue.String(value.value), environment))
            case _ => ???
      )
    )
  )
)
