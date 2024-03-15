/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.BendValue
import dev.ligature.bend.Field
import dev.ligature.bend.HostFunction
import dev.ligature.bend.Tag
import dev.ligature.bend.TaggedField
import scala.util.boundary
import dev.ligature.bend.BendError

val arrayModule: BendValue.Module = BendValue.Module(
  Map(
    Field("length") -> BendValue.Function(
      HostFunction(
        "Get the number of elements in an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              Right((BendValue.Int(value.length), environment))
            case _ => ???
      )
    ),
    Field("map") -> BendValue.Function(
      HostFunction(
        "Map the values of an Array with the given function.",
        Seq(TaggedField(Field("fn"), Tag.Untagged), TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Function(fn), BendValue.Array(values)) =>
              boundary:
                val results = values.map(value =>
                  fn.call(Seq(value), environment) match
                    case Left(_)      => ??? /// break(value)
                    case Right(value) => value
                )
                Right((BendValue.Array(results), environment))
            case _ => ???
      )
    ),
    Field("filter") -> BendValue.Function(
      HostFunction(
        "Filter an Array with the given predicate.",
        Seq(TaggedField(Field("fn"), Tag.Untagged), TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Function(fn), BendValue.Array(values)) =>
              boundary:
                val results = values.filter(value =>
                  fn.call(Seq(value), environment) match
                    case Left(_) => ??? /// break(err)
                    case Right(value) =>
                      value match
                        case BendValue.Bool(value) => value
                        case _                     => ??? /// break(Left(LigatureError("")
                )
                Right((BendValue.Array(results), environment))
            case _ => ???
      )
    ),
    Field("first") -> BendValue.Function(
      HostFunction(
        "Get the first element of an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              if (value.length > 0) {
                Right((value.head, environment))
              } else {
                Left(BendError("Cannot call Array.head on empty array."))
              }
            case _ => ???
      )
    ),
    Field("rest") -> BendValue.Function(
      HostFunction(
        "Get a Array containing all elements except the first.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              Right((BendValue.Array(value.tail), environment))
            case _ => ???
      )
    ),
    Field("last") -> BendValue.Function(
      HostFunction(
        "Get the last element of an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              if (value.length > 0) {
                Right((value.last, environment))
              } else {
                Left(BendError("Cannot call Array.last on empty array."))
              }
            case _ => ???
      )
    ),
    Field("cat") -> BendValue.Function(
      HostFunction(
        "Concat all Strings in this Array.",
        Seq(
          TaggedField(Field("array"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              Right(
                (
                  BendValue.String(
                    value
                      .map(
                        _ match
                          case BendValue.String(value) => value
                          case _                       => ???
                      )
                      .mkString("")
                  ),
                  environment
                )
              ) // TODO make separator an arg
            case _ => ???
      )
    ),
    Field("join") -> BendValue.Function(
      HostFunction(
        "Join this array.",
        Seq(
          TaggedField(Field("array"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              Right(
                (
                  BendValue.String(
                    value
                      .map(
                        _ match
                          case BendValue.String(value) => value
                          case _                       => ???
                      )
                      .mkString("\n")
                  ),
                  environment
                )
              ) // TODO make separator an arg
            case _ => ???
      )
    ),
    Field("foldLeft") -> BendValue.Function(
      HostFunction(
        "Perform foldLeft on this array.",
        Seq(
          TaggedField(Field("initial"), Tag.Untagged),
          TaggedField(Field("accumulator"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(BendValue.Array(value)) =>
              Right((BendValue.Array(value.tail), environment))
            case _ => ???
      )
    )
  )
)
