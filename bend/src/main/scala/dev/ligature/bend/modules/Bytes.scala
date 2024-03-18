/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.HostFunction
import dev.ligature.bend.BendValue
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Tag
import dev.ligature.bend.Field
import io.fury.Fury

val fury = Fury.builder()
  .withScalaOptimizationEnabled(true)
  .requireClassRegistration(false)
  .withRefTracking(true)
  .build()

val bytesModule: BendValue.Module = BendValue.Module(Map(
    Field("encode") -> BendValue.Function(HostFunction(
        "",
        Seq(TaggedField(Field("value"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
            args match
                case Seq(value: BendValue) => Right((BendValue.Bytes(encodeBendValue(value)), env))
                case _ => ???
    )),
    Field("decode") -> BendValue.Function(HostFunction(
        "",
        Seq(TaggedField(Field("value"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
            args match
                case Seq(BendValue.Bytes(value)) => Right((decodeBendValue(value), env))
                case _ => ???
    )),
))

def encodeBendValue(value: BendValue): Seq[Byte] =
  fury.serialize(value).toIndexedSeq

def decodeBendValue(value: Seq[Byte]): BendValue =
 fury.deserialize(value.toArray).asInstanceOf[BendValue]
