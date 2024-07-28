/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.WanderError
// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.Field

val bool = 0

// val boolModule: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("not") -> LigatureValue.Function(
//       HostFunction(
//         "Perform a not operation on a Bool value.",
//         Seq(TaggedField(Field("value"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Bool(value)) => Right((LigatureValue.Bool(!value), environment))
//             case _                            => Left(WanderError("Unexpected input " + args))
//       )
//     ),
//     Field("and") -> LigatureValue.Function(
//       HostFunction(
//         "Perform a logical and on two Bools.",
//         Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Bool(left), LigatureValue.Bool(right)) =>
//               Right((LigatureValue.Bool(left && right), environment))
//             case _ => ???
//       )
//     ),
//     Field("or") -> LigatureValue.Function(
//       HostFunction(
//         "Perform a logical or on two Bools.",
//         Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
//         Tag.Untagged,
//         (_, _) => ???
//       )
//     ),
//     Field("toBytes") -> LigatureValue.Function(
//       HostFunction(
//         "Encod a Bool as Bytes.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Bool(value)) =>
//               Right(
//                 (
//                   ???, /// LigatureValue.Bytes(BooleanBinding.booleanToEntry(value).getBytesUnsafe().toSeq),
//                   environment
//                 )
//               )
//       )
//     ),
//     Field("fromBytes") -> LigatureValue.Function(
//       HostFunction(
//         "Decode Bytes to a Bool.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Bytes(value)) =>
//               Right(
//                 (
//                   ???, // LigatureValue.Bool(BooleanBinding.entryToBoolean(ArrayByteIterable(value.toArray))),
//                   environment
//                 )
//               )
//       )
//     )
//   )
// )
