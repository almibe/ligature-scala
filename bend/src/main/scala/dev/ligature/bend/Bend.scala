/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.bend.parse
import java.util.HexFormat
import dev.ligature.LigatureValue

/** Represents a Value in the Bend language.
  */
enum BendValue:
  case Int(value: Long)
  case Bool(value: Boolean)
  case Bytes(value: Seq[Byte])
  case String(value: java.lang.String)
  case Array(values: Seq[BendValue])
  case Identifier(value: LigatureValue.Identifier)
  case Statement(statement: dev.ligature.Statement)
  case Graph(value: Set[dev.ligature.Statement])
  case Module(values: Map[Field, BendValue])
  case Function(function: dev.ligature.bend.Function)
  case QuestionMark

case class Field(name: String)
case class FieldPath(parts: Seq[Field])
case class TaggedField(field: Field, tag: Tag)

enum Tag:
  case Untagged
  case Single(tag: Function)
  case Chain(names: Seq[Function])

trait Function:
  def call(args: Seq[BendValue], environment: Environment): Either[BendError, BendValue]

case class Lambda(val lambda: Expression.Lambda) extends Function {
  override def call(
      args: Seq[BendValue],
      environment: Environment
  ): Either[BendError, BendValue] = ???
}
case class PartialFunction(args: Seq[BendValue], function: dev.ligature.bend.Function)
    extends Function {
  override def call(
      args: Seq[BendValue],
      environment: Environment
  ): Either[BendError, BendValue] = ???
}

case class HostFunction(
    docString: String,
    parameters: Seq[TaggedField],
    resultTag: Tag,
    fn: (
        arguments: Seq[BendValue],
        environment: Environment
    ) => Either[BendError, (BendValue, Environment)]
) extends Function {
  override def call(
      args: Seq[BendValue],
      environment: Environment
  ): Either[BendError, BendValue] = ???
}

case class BendError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
    expression <- process(terms)
  } yield expression
  expression match
    case Left(value)  => Left(value)
    case Right(value) => environment.eval(value)

case class Inspect(
    tokens: Either[BendError, Seq[Token]],
    terms: Either[BendError, Seq[Term]],
    expression: Either[BendError, Seq[Expression]]
)

def inspect(script: String): Inspect = {
  val tokens = tokenize(script)

  val terms = if (tokens.isRight) {
    parse(tokens.getOrElse(???))
  } else {
    Left(BendError("Previous error."))
  }

  val expression = if (terms.isRight) {
    process(terms.getOrElse(???))
  } else {
    Left(BendError("Previous error."))
  }

  Inspect(tokens, terms, expression)
}

def printResult(value: Either[BendError, (BendValue, Environment)]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printBendValue(value._1)
  }

val formatter = HexFormat.of()

def printBendValue(value: BendValue, interpolation: Boolean = false): String =
  value match
    case BendValue.QuestionMark => "?"
    case BendValue.Bool(value)  => value.toString()
    case BendValue.Int(value)   => value.toString()
    case BendValue.String(value) =>
      if interpolation then value else s"\"$value\"" // TODO escape correctly
    case BendValue.Function(function) => "\"[Function]\""
    case BendValue.Array(values) =>
      "[" + values.map(value => printBendValue(value, interpolation)).mkString(", ") + "]"
    case BendValue.Module(values) =>
      "{" + values
        .map((field, value) => field.name + " = " + printBendValue(value, interpolation))
        .mkString(", ") + "}"
    case BendValue.Bytes(value)           => printBytes(value)
    case BendValue.Graph(value)           => "\"[Graph]\""
    case BendValue.Identifier(identifier) => printIdentifier(identifier)
    case BendValue.Statement(statement) =>
      val value = statement.value match
        case LigatureValue.BytesValue(value)   => printBytes(value)
        case value: LigatureValue.Identifier   => printIdentifier(value)
        case LigatureValue.IntegerValue(value) => value.toString()
        case LigatureValue.StringValue(value) =>
          if interpolation then value else s"\"$value\"" // TODO escape correctly
      s"<${statement.entity.value}> <${statement.attribute.value}> $value"

def printBytes(bytes: Seq[Byte]) = s"0x${formatter.formatHex(bytes.toArray)}"
def printIdentifier(identifier: LigatureValue.Identifier) = s"<${identifier.value}>"
