/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Field
import dev.ligature.wander.Tag
import dev.ligature.wander.WanderValue
import dev.ligature.wander.LigatureValue
import dev.ligature.wander.WanderError

val identifierModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("toString") -> WanderValue.Function(
      HostFunction(
        "Convert an Identifier to a String.",
        Seq(
          TaggedField(Field("identifier"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          arguments match
            case Seq(WanderValue.Identifier(value)) =>
              Right((WanderValue.String(s"`${value.value}`"), environment))
            case _ => Left(WanderError("Unexpected value."))
      )
    ),
    Field("value") -> WanderValue.Function(
      HostFunction(
        "Get value of Identifier as a String.",
        Seq(
          TaggedField(Field("identifier"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          arguments match
            case Seq(WanderValue.Identifier(value)) =>
              Right((WanderValue.String(value.value), environment))
            case _ => Left(WanderError("Unexpected value."))
      )
    )
  )
)
