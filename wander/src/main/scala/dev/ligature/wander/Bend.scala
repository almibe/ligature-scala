/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.wander.parse
import java.util.HexFormat
import dev.ligature.LigatureValue
import dev.ligature.Statement
import com.google.gson.Gson

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
  case Function(function: dev.ligature.wander.Function)
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
  ): Either[BendError, BendValue] =
    var env = environment
    lambda.parameters.zipWithIndex.foreach { (param, i) =>
      env = env.bindVariable(param, args(i))
    }
    val res = eval(lambda.body, env).map(_._1)
    res
}

case class PartialFunction(args: Seq[BendValue], function: dev.ligature.wander.Function)
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
  ): Either[BendError, BendValue] =
    fn.apply(args, environment).map(_._1)
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

def printBendValue(value: BendValue): String =
  value match
    case BendValue.QuestionMark       => "?"
    case BendValue.Bool(value)        => value.toString()
    case BendValue.Int(value)         => value.toString()
    case BendValue.String(value)      => printString(value)
    case BendValue.Function(function) => "\"[Function]\""
    case BendValue.Array(values) =>
      "[" + values.map(value => printBendValue(value)).mkString(", ") + "]"
    case BendValue.Module(values) =>
      "{" + values
        .map((field, value) => field.name + " = " + printBendValue(value))
        .mkString(", ") + "}"
    case BendValue.Bytes(value)           => printBytes(value)
    case BendValue.Graph(value)           => printGraph(value)
    case BendValue.Identifier(identifier) => printIdentifier(identifier)
    case BendValue.Statement(statement)   => printStatement(statement)

def printBytes(bytes: Seq[Byte]) = s"0x${formatter.formatHex(bytes.toArray)}"

def printIdentifier(identifier: LigatureValue.Identifier) = s"`${identifier.value}`"

def printStatement(statement: Statement) =
  val value = printStatementValue(statement.value)
  s"`${statement.entity.value}` `${statement.attribute.value}` $value"

def printStatementValue(value: LigatureValue): String =
  value match
    case LigatureValue.BytesValue(value)   => printBytes(value)
    case value: LigatureValue.Identifier   => printIdentifier(value)
    case LigatureValue.IntegerValue(value) => value.toString()
    case LigatureValue.StringValue(value)  => printString(value)
    case LigatureValue.Record(values)       => 
      "{" + values
        .map((field, value) => field + " = " + printStatementValue(value))
        .mkString(", ") + "}"

def printGraph(graph: Set[Statement]) = graph
  .map(printStatement)
  .mkString("{ ", ", ", " }") //s"{ ${graph.map(statement => printStatement(statement))} }"

def printString(value: String) =
  val gson = Gson()
  gson.toJson(value)
