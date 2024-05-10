/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.Environment
import dev.ligature.wander.HostFunction
import dev.ligature.wander.WanderValue
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Tag
import dev.ligature.wander.WanderError
import dev.ligature.wander.Field
import dev.ligature.wander.FieldPath

val coreModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("eq") -> WanderValue.Function(
      HostFunction(
        "Check if two values are equal.",
        Seq(
          TaggedField(Field("left"), Tag.Untagged),
          TaggedField(Field("right"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(first, second) =>
              val res = first.asInstanceOf[WanderValue] == second.asInstanceOf[WanderValue]
              Right((WanderValue.Bool(res), environment))
            case _ => ???
      )
    ),
    Field("environment") -> WanderValue.Function(
      HostFunction(
        "Read all Bindings in the current scope.",
        Seq(TaggedField(Field(""), Tag.Untagged)),
        Tag.Untagged,
        (_, environment: Environment) => Right((environment.readAllBindings(), environment))
      )
    )
  )
)

val importFunction = WanderValue.Function(
  HostFunction(
    // FieldPath(Seq(Field("import"))),
    "",
    Seq(TaggedField(Field("import"), Tag.Untagged)),
    Tag.Untagged,
    (args, environment) =>
      args match
        case Seq(WanderValue.String(value)) =>
          val fieldPath = value.split('.').map(Field(_))
          environment.importModule(FieldPath(fieldPath.toSeq)) match
            case Left(err) => Left(err)
            case Right(environment) =>
              Right((WanderValue.Module(Map()), environment))
        case x => Left(WanderError(s"Error: Unexpected value $x"))
  )
)

// HostFunction(
//   Name("Core.Any"),
//   "Checks if a value is an Any.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     Right((WanderValue.Bool(true), environment))
// ),
// HostFunction(
//   Name("Core.Int"),
//   "Check if a value is an Int.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.Int(_)) => Right((WanderValue.Bool(true), environment))
//       case Seq(_)                  => Right((WanderValue.Bool(false), environment))
//       case _                       => ???
// ),
// HostFunction(
//   Name("Core.Bool"),
//   "Check if a value is a Bool.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.Bool(_)) => Right((WanderValue.Bool(true), environment))
//       case Seq(_)                   => Right((WanderValue.Bool(false), environment))
//       case _                        => ???
// ),
// HostFunction(
//   Name("Core.Module"),
//   "Check if a value is a Module.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.Module(_)) => Right((WanderValue.Bool(true), environment))
//       case Seq(_)                     => Right((WanderValue.Bool(false), environment))
//       case _                          => ???
// ),
// HostFunction(
//   Name("Core.Array"),
//   "Check if a value is an Array.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.Array(_)) => Right((WanderValue.Bool(true), environment))
//       case Seq(_)                    => Right((WanderValue.Bool(false), environment))
//       case _                         => ???
// ),
// HostFunction(
//   Name("Core.String"),
//   "Check if a value is a String.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.String(_)) => Right((WanderValue.Bool(true), environment))
//       case Seq(_)                     => Right((WanderValue.Bool(false), environment))
//       case _                          => ???
// ),
// HostFunction(
//   Name("Core.Nothing"),
//   "Check if a value is Nothing.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.Nothing) => Right((WanderValue.Bool(true), environment))
//       case Seq(_)                   => Right((WanderValue.Bool(false), environment))
//       case _                        => ???
// ),
// HostFunction(
//   Name("Core.todo"),
//   "Exit a script at this point with the given TODO message, useful during development.",
//   Seq(TaggedField(Name("message"), Tag.Single(Name("Core.String")))),
//   Tag.Single(Name("Core.Nothing")),
//   (args: Seq[WanderValue], environment: Environment) =>
//     args match
//       case Seq(WanderValue.String(message)) => Left(WanderError(message))
// )
//)

// private def readProperties(evironment: Environment): WanderValue.Module =
//   ???
