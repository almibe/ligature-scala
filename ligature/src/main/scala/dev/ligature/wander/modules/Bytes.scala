/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.Field
import io.fury.Fury

val fury = Fury
  .builder()
  .withScalaOptimizationEnabled(true)
  .requireClassRegistration(false)
  .withRefTracking(true)
  .build()

// val bytesModule: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("encode") -> LigatureValue.Function(
//       HostFunction(
//         "",
//         Seq(TaggedField(Field("value"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(value: LigatureValue) =>
//               Right((LigatureValue.Bytes(encodeLigatureValue(value)), env))
//             case _ => ???
//       )
//     ),
//     Field("decode") -> LigatureValue.Function(
//       HostFunction(
//         "",
//         Seq(TaggedField(Field("value"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(LigatureValue.Bytes(value)) => Right((decodeLigatureValue(value), env))
//             case _                             => ???
//       )
//     )
//   )
// )

// def encodeLigatureValue(value: LigatureValue): Seq[Byte] =
//   fury.serialize(value).toIndexedSeq

// def decodeLigatureValue(value: Seq[Byte]): LigatureValue =
//   fury.deserialize(value.toArray).asInstanceOf[LigatureValue]
