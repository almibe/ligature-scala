/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lib

// import dev.ligature.wander.HostAction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.Field

val stringModule = 0 //: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("replace") -> LigatureValue.Function(
//       HostAction(
//         "Replace all character sequences with another and create a new String.",
//         Seq(
//           TaggedField(Field("toMatch"), Tag.Untagged),
//           TaggedField(Field("replacement"), Tag.Untagged),
//           TaggedField(Field("data"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(
//                   LigatureValue.String(toMatch),
//                   LigatureValue.String(replacement),
//                   LigatureValue.String(data)
//                 ) =>
//               Right(
//                 (
//                   LigatureValue.String(data.replace(toMatch, replacement)),
//                   environment
//                 )
//               )
//       )
//     ),
//     Field("toBytes") -> LigatureValue.Function(
//       HostAction(
//         // FieldPath(Seq(Field("String"), Field("toBytes"))),
//         "Get a String encoded as Bytes.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.String(value)) =>
//               Right(
//                 (
//                   ???, // LigatureValue.Bytes(StringBinding.stringToEntry(value).getBytesUnsafe().toSeq),
//                   environment
//                 )
//               )
//       )
//     ),
//     Field("fromBytes") -> LigatureValue.Function(
//       HostAction(
//         // FieldPath(Seq(Field("String"), Field("fromBytes"))),
//         "Decode Bytes to a String.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Bytes(value)) =>
//               Right(
//                 (
//                   ???, // LigatureValue.String(StringBinding.entryToString(ArrayByteIterable(value.toArray))),
//                   environment
//                 )
//               )
//       )
//     )
//   )
// )
