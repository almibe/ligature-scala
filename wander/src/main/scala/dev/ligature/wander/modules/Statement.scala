/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.LigatureValue
import dev.ligature.wander.{BendValue, Field, HostFunction, TaggedField, Tag}
import dev.ligature.wander.BendError

val statementModule: BendValue.Module = BendValue.Module(
  Map(
    Field("entity") -> BendValue.Function(
      HostFunction(
        "Extract the Entity from a Statement.",
        Seq(
          TaggedField(Field("statement"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
        ),
        Tag.Untagged, // Tag.Single(Field("Core.Int")),
        (args, environment) =>
          args match
            case Seq(BendValue.Statement(statement)) =>
              Right((BendValue.Identifier(statement.entity), environment))
            case _ => Left(BendError("Unexpected value."))
      )
    ),
    Field("attribute") -> BendValue.Function(
      HostFunction(
        "Extract the Attribute from a Statement.",
        Seq(
          TaggedField(Field("statement"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
        ),
        Tag.Untagged, // Tag.Single(Field("Core.Int")),
        (args, environment) =>
          args match
            case Seq(BendValue.Statement(statement)) =>
              Right((BendValue.Identifier(statement.attribute), environment))
            case _ => Left(BendError("Unexpected value."))
      )
    ),
    Field("value") -> BendValue.Function(
      HostFunction(
        "Extract the Value from a Statement.",
        Seq(
          TaggedField(Field("statement"), Tag.Untagged) // Tag.Single(Name("Core.Int"))),
        ),
        Tag.Untagged, // Tag.Single(Field("Core.Int")),
        (args, environment) =>
          args match
            case Seq(BendValue.Statement(statement)) =>
              statement.value match {
                case identifier: LigatureValue.Identifier =>
                  Right((BendValue.Identifier(identifier), environment))
                case LigatureValue.StringValue(value) =>
                  Right((BendValue.String(value), environment))
                case LigatureValue.IntegerValue(value) => Right((BendValue.Int(value), environment))
                case LigatureValue.BytesValue(value) => Right((BendValue.Bytes(value), environment))
                case LigatureValue.Record(_)         => ???
              }
            case _ => Left(BendError("Unexpected value."))
      )
    )
  )
)
