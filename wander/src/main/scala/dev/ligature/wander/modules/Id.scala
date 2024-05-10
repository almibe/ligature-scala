/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Field
import dev.ligature.wander.Tag
import dev.ligature.wander.WanderValue
import io.hypersistence.tsid.TSID
import com.github.f4b6a3.ulid.UlidCreator
import dev.ligature.LigatureValue

val idModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("tsid") -> WanderValue.Function(
      HostFunction(
        "Get next random TSID value.",
        Seq(
          TaggedField(Field("_"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          Right(
            (
              WanderValue.Identifier(LigatureValue.Identifier(TSID.Factory.getTsid().toString())),
              environment
            )
          )
      )
    ),
    Field("ulid") -> WanderValue.Function(
      HostFunction(
        "Get next random ULID value.",
        Seq(
          TaggedField(Field("_"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          Right(
            (
              WanderValue.Identifier(LigatureValue.Identifier(UlidCreator.getUlid().toLowerCase())),
              environment
            )
          )
      )
    )
  )
)
