/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lib

// import dev.ligature.wander.HostAction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Field
// import dev.ligature.wander.Tag
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.WanderError

val word = 0

// val wordModule: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("toString") -> LigatureValue.Function(
//       HostAction(
//         "Convert an Word to a String.",
//         Seq(
//           TaggedField(Field("word"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           arguments match
//             case Seq(LigatureValue.Word(value)) =>
//               Right((LigatureValue.String(s"`${value.value}`"), environment))
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     ),
//     Field("value") -> LigatureValue.Function(
//       HostAction(
//         "Get value of Word as a String.",
//         Seq(
//           TaggedField(Field("word"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           arguments match
//             case Seq(LigatureValue.Word(value)) =>
//               Right((LigatureValue.String(value.value), environment))
//             case _ => Left(WanderError("Unexpected value."))
//       )
//     )
//   )
// )
