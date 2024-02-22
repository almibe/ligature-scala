/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.Environment
import dev.ligature.wander.Token
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Term
import dev.ligature.wander.WanderError
import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedField
import dev.ligature.wander.Tag
import dev.ligature.wander.Field
import dev.ligature.wander.FieldPath
import dev.ligature.wander.eval
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
