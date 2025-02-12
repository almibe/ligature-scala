/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lib

// import dev.ligature.wander.Environment
// import dev.ligature.wander.HostAction
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.WanderError
// import dev.ligature.wander.Field
// import dev.ligature.wander.FieldPath

val core = 0

// val coreModule: LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("eq") -> LigatureValue.Function(
//       HostAction(
//         "Check if two values are equal.",
//         Seq(
//           TaggedField(Field("left"), Tag.Untagged),
//           TaggedField(Field("right"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (args: Seq[LigatureValue], environment: Environment) =>
//           args match
//             case Seq(first, second) =>
//               val res = first.asInstanceOf[LigatureValue] == second.asInstanceOf[LigatureValue]
//               Right((LigatureValue.Bool(res), environment))
//             case _ => ???
//       )
//     ),
//     Field("environment") -> LigatureValue.Function(
//       HostAction(
//         "Read all Bindings in the current scope.",
//         Seq(TaggedField(Field(""), Tag.Untagged)),
//         Tag.Untagged,
//         (_, environment: Environment) => Right((environment.readAllBindings(), environment))
//       )
//     )
//   )
// )

// val importFunction = LigatureValue.Function(
//   HostAction(
//     // FieldPath(Seq(Field("import"))),
//     "",
//     Seq(TaggedField(Field("import"), Tag.Untagged)),
//     Tag.Untagged,
//     (args, environment) =>
//       args match
//         case Seq(LigatureValue.String(value)) =>
//           val fieldPath = value.split('.').map(Field(_))
//           environment.importModule(FieldPath(fieldPath.toSeq)) match
//             case Left(err) => Left(err)
//             case Right(environment) =>
//               Right((LigatureValue.Module(Map()), environment))
//         case x => Left(WanderError(s"Error: Unexpected value $x"))
//   )
// )

// // HostAction(
// //   Name("Core.Any"),
// //   "Checks if a value is an Any.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     Right((LigatureValue.Bool(true), environment))
// // ),
// // HostAction(
// //   Name("Core.Int"),
// //   "Check if a value is an Int.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.Int(_)) => Right((LigatureValue.Bool(true), environment))
// //       case Seq(_)                  => Right((LigatureValue.Bool(false), environment))
// //       case _                       => ???
// // ),
// // HostAction(
// //   Name("Core.Bool"),
// //   "Check if a value is a Bool.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.Bool(_)) => Right((LigatureValue.Bool(true), environment))
// //       case Seq(_)                   => Right((LigatureValue.Bool(false), environment))
// //       case _                        => ???
// // ),
// // HostAction(
// //   Name("Core.Module"),
// //   "Check if a value is a Module.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.Module(_)) => Right((LigatureValue.Bool(true), environment))
// //       case Seq(_)                     => Right((LigatureValue.Bool(false), environment))
// //       case _                          => ???
// // ),
// // HostAction(
// //   Name("Core.Array"),
// //   "Check if a value is an Array.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.Array(_)) => Right((LigatureValue.Bool(true), environment))
// //       case Seq(_)                    => Right((LigatureValue.Bool(false), environment))
// //       case _                         => ???
// // ),
// // HostAction(
// //   Name("Core.String"),
// //   "Check if a value is a String.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.String(_)) => Right((LigatureValue.Bool(true), environment))
// //       case Seq(_)                     => Right((LigatureValue.Bool(false), environment))
// //       case _                          => ???
// // ),
// // HostAction(
// //   Name("Core.Nothing"),
// //   "Check if a value is Nothing.",
// //   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
// //   Tag.Single(Name("Core.Bool")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.Nothing) => Right((LigatureValue.Bool(true), environment))
// //       case Seq(_)                   => Right((LigatureValue.Bool(false), environment))
// //       case _                        => ???
// // ),
// // HostAction(
// //   Name("Core.todo"),
// //   "Exit a script at this point with the given TODO message, useful during development.",
// //   Seq(TaggedField(Name("message"), Tag.Single(Name("Core.String")))),
// //   Tag.Single(Name("Core.Nothing")),
// //   (args: Seq[LigatureValue], environment: Environment) =>
// //     args match
// //       case Seq(LigatureValue.String(message)) => Left(WanderError(message))
// // )
// //)

// // private def readProperties(evironment: Environment): LigatureValue.Module =
// //   ???
