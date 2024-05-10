/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.WanderValue
import dev.ligature.wander.Field
import dev.ligature.wander.HostFunction
import dev.ligature.wander.Tag
import dev.ligature.wander.TaggedField
import scala.util.boundary
import dev.ligature.wander.WanderError

val arrayModule: WanderValue.Module = WanderValue.Module(
  Map(
    Field("length") -> WanderValue.Function(
      HostFunction(
        "Get the number of elements in an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              Right((WanderValue.Int(value.length), environment))
            case _ => ???
      )
    ),
    Field("map") -> WanderValue.Function(
      HostFunction(
        "Map the values of an Array with the given function.",
        Seq(TaggedField(Field("fn"), Tag.Untagged), TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Function(fn), WanderValue.Array(values)) =>
              boundary:
                val results = values.map(value =>
                  fn.call(Seq(value), environment) match
                    case Left(_)      => ??? /// break(value)
                    case Right(value) => value
                )
                Right((WanderValue.Array(results), environment))
            case _ => ???
      )
    ),
    Field("filter") -> WanderValue.Function(
      HostFunction(
        "Filter an Array with the given predicate.",
        Seq(TaggedField(Field("fn"), Tag.Untagged), TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Function(fn), WanderValue.Array(values)) =>
              boundary:
                val results = values.filter(value =>
                  fn.call(Seq(value), environment) match
                    case Left(_) => ??? /// break(err)
                    case Right(value) =>
                      value match
                        case WanderValue.Bool(value) => value
                        case _                     => ??? /// break(Left(LigatureError("")
                )
                Right((WanderValue.Array(results), environment))
            case _ => ???
      )
    ),
    Field("first") -> WanderValue.Function(
      HostFunction(
        "Get the first element of an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              if (value.length > 0) {
                Right((value.head, environment))
              } else {
                Left(WanderError("Cannot call Array.head on empty array."))
              }
            case _ => ???
      )
    ),
    Field("rest") -> WanderValue.Function(
      HostFunction(
        "Get a Array containing all elements except the first.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              Right((WanderValue.Array(value.tail), environment))
            case _ => ???
      )
    ),
    Field("last") -> WanderValue.Function(
      HostFunction(
        "Get the last element of an Array.",
        Seq(TaggedField(Field("array"), Tag.Untagged)),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              if (value.length > 0) {
                Right((value.last, environment))
              } else {
                Left(WanderError("Cannot call Array.last on empty array."))
              }
            case _ => ???
      )
    ),
    Field("cat") -> WanderValue.Function(
      HostFunction(
        "Concat all Strings in this Array.",
        Seq(
          TaggedField(Field("array"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              Right(
                (
                  WanderValue.String(
                    value
                      .map(
                        _ match
                          case WanderValue.String(value) => value
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
    Field("join") -> WanderValue.Function(
      HostFunction(
        "Join this array.",
        Seq(
          TaggedField(Field("array"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              Right(
                (
                  WanderValue.String(
                    value
                      .map(
                        _ match
                          case WanderValue.String(value) => value
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
    Field("foldLeft") -> WanderValue.Function(
      HostFunction(
        "Perform foldLeft on this array.",
        Seq(
          TaggedField(Field("initial"), Tag.Untagged),
          TaggedField(Field("accumulator"), Tag.Untagged)
        ),
        Tag.Untagged,
        (args, environment) =>
          args match
            case Seq(WanderValue.Array(value)) =>
              Right((WanderValue.Array(value.tail), environment))
            case _ => ???
      )
    )
  )
)
