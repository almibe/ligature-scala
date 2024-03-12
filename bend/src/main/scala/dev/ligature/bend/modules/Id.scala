/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.HostFunction
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Field
import dev.ligature.bend.Tag
import dev.ligature.bend.BendValue
import io.hypersistence.tsid.TSID
import com.github.f4b6a3.ulid.UlidCreator
import dev.ligature.LigatureValue

val idModule: BendValue.Module = BendValue.Module(
  Map(
    Field("tsid") -> BendValue.Function(
      HostFunction(
        "Get next random TSID value.",
        Seq(
          TaggedField(Field("_"), Tag.Untagged),
        ),
        Tag.Untagged,
        (arguments, environment) => Right((BendValue.Identifier(LigatureValue.Identifier(TSID.Factory.getTsid().toString())), environment))
      )
    ),
    Field("ulid") -> BendValue.Function(
      HostFunction(
        "Get next random ULID value.",
        Seq(
          TaggedField(Field("_"), Tag.Untagged),
        ),
        Tag.Untagged,
        (arguments, environment) => Right((BendValue.Identifier(LigatureValue.Identifier(UlidCreator.getUlid().toLowerCase())), environment))
      )
    )
  )
)
