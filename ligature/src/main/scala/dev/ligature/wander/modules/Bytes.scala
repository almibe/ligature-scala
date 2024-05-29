/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.HostFunction
import dev.ligature.wander.WanderValue
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Tag
import dev.ligature.wander.Field
import io.fury.Fury

val fury = Fury
  .builder()
  .withScalaOptimizationEnabled(true)
  .requireClassRegistration(false)
  .withRefTracking(true)
  .build()

val bytesModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("encode") -> WanderValue.Function(
      HostFunction(
        "",
        Seq(TaggedField(Field("value"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(value: WanderValue) =>
              Right((WanderValue.Bytes(encodeWanderValue(value)), env))
            case _ => ???
      )
    ),
    Field("decode") -> WanderValue.Function(
      HostFunction(
        "",
        Seq(TaggedField(Field("value"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(WanderValue.Bytes(value)) => Right((decodeWanderValue(value), env))
            case _                             => ???
      )
    )
  )
)

def encodeWanderValue(value: WanderValue): Seq[Byte] =
  fury.serialize(value).toIndexedSeq

def decodeWanderValue(value: Seq[Byte]): WanderValue =
  fury.deserialize(value.toArray).asInstanceOf[WanderValue]
