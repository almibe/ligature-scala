/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.{LigatureValue, Field, HostFunction, TaggedField, Tag}
// import dev.ligature.wander.WanderError

val tripleModule = 0//: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("entity") -> LigatureValue.Function(
//       HostFunction(
//         "Extract the Entity from a Triple.",
//         Seq(
//           TaggedField(Field("triple"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Triple(triple)) =>
//               Right((LigatureValue.Word(triple.entity), environment))
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     ),
//     Field("attribute") -> LigatureValue.Function(
//       HostFunction(
//         "Extract the Attribute from a Triple.",
//         Seq(
//           TaggedField(Field("triple"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Triple(triple)) =>
//               Right((LigatureValue.Word(triple.attribute), environment))
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     ),
//     Field("value") -> LigatureValue.Function(
//       HostFunction(
//         "Extract the Value from a Triple.",
//         Seq(
//           TaggedField(Field("triple"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Triple(triple)) =>
//               triple.value match {
//                 case word: LigatureValue.Word =>
//                   Right((LigatureValue.Word(word), environment))
//                 case LigatureValue.String(value) =>
//                   Right((LigatureValue.String(value), environment))
//                 case LigatureValue.Int(value) =>
//                   Right((LigatureValue.Int(value), environment))
//                 case LigatureValue.Bytes(value) =>
//                   Right((LigatureValue.Bytes(value), environment))
//                 case LigatureValue.Record(_) => ???
//               }
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     )
//   )
// )
