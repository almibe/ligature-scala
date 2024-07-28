/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.{WanderValue, Field, HostFunction, TaggedField, Tag}
// import dev.ligature.wander.WanderError

val tripleModule = 0//: WanderValue.Module = WanderValue.Module(
//   Map(
//     Field("entity") -> WanderValue.Function(
//       HostFunction(
//         "Extract the Entity from a Triple.",
//         Seq(
//           TaggedField(Field("triple"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Triple(triple)) =>
//               Right((WanderValue.Word(triple.entity), environment))
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     ),
//     Field("attribute") -> WanderValue.Function(
//       HostFunction(
//         "Extract the Attribute from a Triple.",
//         Seq(
//           TaggedField(Field("triple"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Triple(triple)) =>
//               Right((WanderValue.Word(triple.attribute), environment))
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     ),
//     Field("value") -> WanderValue.Function(
//       HostFunction(
//         "Extract the Value from a Triple.",
//         Seq(
//           TaggedField(Field("triple"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Triple(triple)) =>
//               triple.value match {
//                 case word: LigatureValue.Word =>
//                   Right((WanderValue.Word(word), environment))
//                 case LigatureValue.StringValue(value) =>
//                   Right((WanderValue.String(value), environment))
//                 case LigatureValue.IntegerValue(value) =>
//                   Right((WanderValue.Int(value), environment))
//                 case LigatureValue.BytesValue(value) =>
//                   Right((WanderValue.Bytes(value), environment))
//                 case LigatureValue.Record(_) => ???
//               }
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     )
//   )
// )
