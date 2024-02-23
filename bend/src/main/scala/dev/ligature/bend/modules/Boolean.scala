/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.Environment
import dev.ligature.bend.Token
import dev.ligature.bend.WanderValue
import dev.ligature.bend.Term
import dev.ligature.bend.WanderError
import dev.ligature.bend.HostFunction
import dev.ligature.bend.TaggedField
import dev.ligature.bend.Tag
import dev.ligature.bend.Field
import dev.ligature.bend.FieldPath
import dev.ligature.bend.eval
import jetbrains.exodus.bindings.BooleanBinding
import jetbrains.exodus.ArrayByteIterable

val boolModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("not") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Bool"), Field("not"))),
        "Perform a not operation on a Bool value.",
        Seq(TaggedField(Field("value"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Bool(value)) => Right((WanderValue.Bool(!value), environment))
            case _                            => Left(WanderError("Unexpected input " + args))
      )
    ),
    Field("and") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Bool"), Field("and"))),
        "Perform a logical and on two Bools.",
        Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Bool(left), WanderValue.Bool(right)) =>
              Right((WanderValue.Bool(left && right), environment))
            case _ => ???
      )
    ),
    Field("or") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Bool"), Field("or"))),
        "Perform a logical or on two Bools.",
        Seq(TaggedField(Field("left"), Tag.Untagged), TaggedField(Field("right"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) => ???
      )
    ),
    Field("toBytes") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Bool"), Field("toBytes"))),
        "Encod a Bool as Bytes.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Bool(value)) =>
              Right(
                (
                  WanderValue.Bytes(BooleanBinding.booleanToEntry(value).getBytesUnsafe().toSeq),
                  environment
                )
              )
      )
    ),
    Field("fromBytes") -> WanderValue.Function(
      HostFunction(
        // FieldPath(Seq(Field("Bool"), Field("fromBytes"))),
        "Decode Bytes to a Bool.",
        Seq(
          TaggedField(Field("value"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Bytes(value)) =>
              Right(
                (
                  WanderValue.Bool(BooleanBinding.entryToBoolean(ArrayByteIterable(value.toArray))),
                  environment
                )
              )
      )
    )
  )
)

//   stdLib = stdLib
//     .bindVariable(
//       Name("and"),
//       WanderValue.HostFunction(
//         (arguments: Seq[Expression], environment: Environment) => ???
//           // if arguments.length == 2 then
//           //   val res = for {
//           //     left <- evalTerm(arguments(0), environment)
//           //     right <- evalTerm(arguments(1), environment)
//           //   } yield (left, right)
//           //   res.map { r =>
//           //     (r._1.result, r._2.result) match
//           //       case (WanderValue.BooleanValue(left), WanderValue.BooleanValue(right)) => WanderValue.BooleanValue(left && right)
//           //       case _ => throw LigatureError("`and` function requires two booleans")
//           //   }
//           // else
//           //   IO.raiseError(LigatureError("`and` function requires two booleans"))
//       )
//     )
//     .getOrElse(???)

//   stdLib = stdLib
//     .bindVariable(
//       Name("or"),
//       WanderValue.HostFunction(
//         (arguments: Seq[Expression], environment: Environment) => ???
//           // if arguments.length == 2 then
//           //   val res = for {
//           //     left <- evalTerm(arguments(0), environment)
//           //     right <- evalTerm(arguments(1), environment)
//           //   } yield (left, right)
//           //   res.map { r =>
//           //     (r._1.result, r._2.result) match
//           //       case (WanderValue.BooleanValue(left), WanderValue.BooleanValue(right)) => WanderValue.BooleanValue(left || right)
//           //       case _ => throw LigatureError("`or` function requires two booleans")
//           //   }
//           // else
//           //   Left(WanderError("`or` function requires two booleans")))
//       )
//     )
//     .getOrElse(???)
