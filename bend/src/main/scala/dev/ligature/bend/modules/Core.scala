/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.Environment
import dev.ligature.bend.HostFunction
import dev.ligature.bend.BendValue
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Tag
import dev.ligature.bend.WanderError
import dev.ligature.bend.Field
import dev.ligature.bend.FieldPath

val coreModule: BendValue.Module = BendValue.Module(
  Map(
    Field("eq") -> BendValue.Function(
      HostFunction(
        "Check if two values are equal.",
        Seq(
          TaggedField(Field("left"), Tag.Untagged),
          TaggedField(Field("right"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args: Seq[BendValue], environment: Environment) =>
          args match
            case Seq(first, second) =>
              val res = first.asInstanceOf[BendValue] == second.asInstanceOf[BendValue]
              Right((BendValue.Bool(res), environment))
            case _ => ???
      )
    ),
    Field("environment") -> BendValue.Function(
      HostFunction(
        "Read all Bindings in the current scope.",
        Seq(TaggedField(Field(""), Tag.Untagged)),
        Tag.Untagged,
        (_, environment: Environment) => Right((environment.readAllBindings(), environment))
      )
    )
  )
)

val importFunction = BendValue.Function(
  HostFunction(
    // FieldPath(Seq(Field("import"))),
    "",
    Seq(TaggedField(Field("import"), Tag.Untagged)),
    Tag.Untagged,
    (args, environment) =>
      args match
        case Seq(BendValue.String(value)) =>
          val fieldPath = value.split('.').map(Field(_))
          environment.importModule(FieldPath(fieldPath.toSeq)) match
            case Left(err) => Left(err)
            case Right(environment) =>
              Right((BendValue.Module(Map()), environment))
        case x => Left(WanderError(s"Error: Unexpected value $x"))
  )
)

// HostFunction(
//   Name("Core.Any"),
//   "Checks if a value is an Any.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     Right((BendValue.Bool(true), environment))
// ),
// HostFunction(
//   Name("Core.Int"),
//   "Check if a value is an Int.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.Int(_)) => Right((BendValue.Bool(true), environment))
//       case Seq(_)                  => Right((BendValue.Bool(false), environment))
//       case _                       => ???
// ),
// HostFunction(
//   Name("Core.Bool"),
//   "Check if a value is a Bool.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.Bool(_)) => Right((BendValue.Bool(true), environment))
//       case Seq(_)                   => Right((BendValue.Bool(false), environment))
//       case _                        => ???
// ),
// HostFunction(
//   Name("Core.Module"),
//   "Check if a value is a Module.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.Module(_)) => Right((BendValue.Bool(true), environment))
//       case Seq(_)                     => Right((BendValue.Bool(false), environment))
//       case _                          => ???
// ),
// HostFunction(
//   Name("Core.Array"),
//   "Check if a value is an Array.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.Array(_)) => Right((BendValue.Bool(true), environment))
//       case Seq(_)                    => Right((BendValue.Bool(false), environment))
//       case _                         => ???
// ),
// HostFunction(
//   Name("Core.String"),
//   "Check if a value is a String.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.String(_)) => Right((BendValue.Bool(true), environment))
//       case Seq(_)                     => Right((BendValue.Bool(false), environment))
//       case _                          => ???
// ),
// HostFunction(
//   Name("Core.Nothing"),
//   "Check if a value is Nothing.",
//   Seq(TaggedField(Name("value"), Tag.Single(Name("Core.Any")))),
//   Tag.Single(Name("Core.Bool")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.Nothing) => Right((BendValue.Bool(true), environment))
//       case Seq(_)                   => Right((BendValue.Bool(false), environment))
//       case _                        => ???
// ),
// HostFunction(
//   Name("Core.todo"),
//   "Exit a script at this point with the given TODO message, useful during development.",
//   Seq(TaggedField(Name("message"), Tag.Single(Name("Core.String")))),
//   Tag.Single(Name("Core.Nothing")),
//   (args: Seq[BendValue], environment: Environment) =>
//     args match
//       case Seq(BendValue.String(message)) => Left(WanderError(message))
// )
//)

// private def readProperties(evironment: Environment): BendValue.Module =
//   ???
