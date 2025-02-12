/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lib

// import dev.ligature.wander.HostAction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Field
// import dev.ligature.wander.Tag
// import dev.ligature.wander.LigatureValue
// import io.hypersistence.tsid.TSID
// import com.github.f4b6a3.ulid.UlidCreator
// import dev.ligature.wander.LigatureValue

val id = 0

// val idModule: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("tsid") -> LigatureValue.Function(
//       HostAction(
//         "Get next random TSID value.",
//         Seq(
//           TaggedField(Field("_"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           Right(
//             (
//               LigatureValue.Word(LigatureValue.Word(TSID.Factory.getTsid().toString())),
//               environment
//             )
//           )
//       )
//     ),
//     Field("ulid") -> LigatureValue.Function(
//       HostAction(
//         "Get next random ULID value.",
//         Seq(
//           TaggedField(Field("_"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           Right(
//             (
//               LigatureValue.Word(LigatureValue.Word(UlidCreator.getUlid().toLowerCase())),
//               environment
//             )
//           )
//       )
//     )
//   )
// )
