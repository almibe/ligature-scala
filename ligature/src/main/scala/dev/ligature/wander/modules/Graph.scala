/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.Field
// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.Tag
// import dev.ligature.wander.TaggedField

val graph = 0

// val networkModule: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("length") -> LigatureValue.Function(
//       HostFunction(
//         "Get the number of elements in a Network.",
//         Seq(TaggedField(Field("array"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Network(value)) =>
//               Right((LigatureValue.Int(value.size), environment))
//             case _ => ???
//       )
//     ),
//     Field("merge") -> LigatureValue.Function(
//       HostFunction(
//         "Merge two Networks.",
//         Seq(TaggedField(Field("array"), Tag.Untagged), TaggedField(Field("array"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, environment) =>
//           args match
//             case Seq(LigatureValue.Network(lvalue), LigatureValue.Network(rvalue)) =>
//               Right((LigatureValue.Network(lvalue ++ rvalue), environment))
//             case _ => ???
//       )
//     )
//   )
// )
