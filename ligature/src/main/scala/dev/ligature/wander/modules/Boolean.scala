/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.WanderValue
// import dev.ligature.wander.WanderError
// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.Field

val bool = 0

// val boolModule: WanderValue.Module = WanderValue.Module(
//   Map(
//     Field("not") -> WanderValue.Function(
//       HostFunction(
//         "Perform a not operation on a Bool value.",
//         Seq(TaggedField(Field("value"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Bool(value)) => Right((WanderValue.Bool(!value), environment))
//             case _                            => Left(WanderError("Unexpected input " + args))
//       )
//     ),
//     Field("and") -> WanderValue.Function(
//       HostFunction(
//         "Perform a logical and on two Bools.",
//         Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Bool(left), WanderValue.Bool(right)) =>
//               Right((WanderValue.Bool(left && right), environment))
//             case _ => ???
//       )
//     ),
//     Field("or") -> WanderValue.Function(
//       HostFunction(
//         "Perform a logical or on two Bools.",
//         Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
//         Tag.Untagged,
//         (_, _) => ???
//       )
//     ),
//     Field("toBytes") -> WanderValue.Function(
//       HostFunction(
//         "Encod a Bool as Bytes.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Bool(value)) =>
//               Right(
//                 (
//                   ???, /// WanderValue.Bytes(BooleanBinding.booleanToEntry(value).getBytesUnsafe().toSeq),
//                   environment
//                 )
//               )
//       )
//     ),
//     Field("fromBytes") -> WanderValue.Function(
//       HostFunction(
//         "Decode Bytes to a Bool.",
//         Seq(
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(WanderValue.Bytes(value)) =>
//               Right(
//                 (
//                   ???, // WanderValue.Bool(BooleanBinding.entryToBoolean(ArrayByteIterable(value.toArray))),
//                   environment
//                 )
//               )
//       )
//     )
//   )
// )
