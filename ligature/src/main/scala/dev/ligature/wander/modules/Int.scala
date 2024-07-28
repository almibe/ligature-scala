/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.Field

val intModule = 0//: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("add") -> LigatureValue.Function(
//       HostFunction(
//         // FieldPath(Seq(Field("Int"), Field("add"))),
//         "Add two Ints.",
//         Seq(
//           TaggedField(Field("left"), Tag.Untagged), // Tag.Single(Name("Core.Int"))),
//           TaggedField(Field("right"), Tag.Untagged) // Tag.Single(Name("Core.Int")))
//         ),
//         Tag.Untagged, // Tag.Single(Field("Core.Int")),
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Int(left), LigatureValue.Int(right)) =>
//               Right((LigatureValue.Int(left + right), environment))
//             case _ => ???
//       )
//     ),
//     Field("toBytes") -> LigatureValue.Function(
//       HostFunction(
//         // FieldPath(Seq(Field("Int"), Field("toBytes"))),
//         "Encode an Int as Bytes.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Int(value)) =>
//               Right(
//                 (
//                   ???, // LigatureValue.Bytes(LongBinding.longToEntry(value).getBytesUnsafe().toSeq),
//                   environment
//                 )
//               )
//       )
//     ),
//     Field("fromBytes") -> LigatureValue.Function(
//       HostFunction(
//         // FieldPath(Seq(Field("Int"), Field("fromBytes"))),
//         "Decode Bytes to an Int.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Bytes(value)) =>
//               Right(
//                 (
//                   ???, // LigatureValue.Int(LongBinding.entryToLong(ArrayByteIterable(value.toArray))),
//                   environment
//                 )
//               )
//       )
//     )
//   )
// )
