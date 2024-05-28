/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import java.util.HexFormat
import dev.ligature.LigatureValue
import dev.ligature.Statement
import com.google.gson.Gson

/** Represents a Value in the Wander language.
  */
enum WanderValue:
  case Int(value: Long)
  case Bool(value: Boolean)
  case Bytes(value: Seq[Byte])
  case String(value: java.lang.String)
  case Array(values: Seq[WanderValue])
  case Identifier(value: LigatureValue.Identifier)
  case Statement(statement: dev.ligature.Statement)
  case Graph(value: Set[dev.ligature.Statement])
  case Module(values: Map[Field, WanderValue])
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
  def call(args: Seq[WanderValue], environment: Environment): Either[WanderError, WanderValue]

case class Lambda(val lambda: Expression.Lambda) extends Function {
  override def call(
      args: Seq[WanderValue],
      environment: Environment
  ): Either[WanderError, WanderValue] =
    var env = environment
    lambda.parameters.zipWithIndex.foreach { (param, i) =>
      env = env.bindVariable(param, args(i))
    }
    val res = eval(lambda.body, env).map(_._1)
    res
}

case class PartialFunction(args: Seq[WanderValue], function: dev.ligature.wander.Function)
    extends Function {
  override def call(
      args: Seq[WanderValue],
      environment: Environment
  ): Either[WanderError, WanderValue] = ???
}

case class HostFunction(
    docString: String,
    parameters: Seq[TaggedField],
    resultTag: Tag,
    fn: (
        arguments: Seq[WanderValue],
        environment: Environment
    ) => Either[WanderError, (WanderValue, Environment)]
) extends Function {
  override def call(
      args: Seq[WanderValue],
      environment: Environment
  ): Either[WanderError, WanderValue] =
    fn.apply(args, environment).map(_._1)
}

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
    expression <- process(terms)
  } yield expression
  expression match
    case Left(value)  => Left(value)
    case Right(value) => environment.eval(value)

case class Inspect(
    tokens: Either[WanderError, Seq[Token]],
    terms: Either[WanderError, Seq[Term]],
    expression: Either[WanderError, Seq[Expression]]
)

def inspect(script: String): Inspect = {
  val tokens = tokenize(script)

  val terms = if (tokens.isRight) {
    parse(tokens.getOrElse(???))
  } else {
    Left(WanderError("Previous error."))
  }

  val expression = if (terms.isRight) {
    process(terms.getOrElse(???))
  } else {
    Left(WanderError("Previous error."))
  }

  Inspect(tokens, terms, expression)
}

def printResult(value: Either[WanderError, (WanderValue, Environment)]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printWanderValue(value._1)
  }

val formatter = HexFormat.of()

def printWanderValue(value: WanderValue): String =
  value match
    case WanderValue.QuestionMark       => "?"
    case WanderValue.Bool(value)        => value.toString()
    case WanderValue.Int(value)         => value.toString()
    case WanderValue.String(value)      => printString(value)
    case WanderValue.Function(function) => "\"[Function]\""
    case WanderValue.Array(values) =>
      "[" + values.map(value => printWanderValue(value)).mkString(", ") + "]"
    case WanderValue.Module(values) =>
      "{" + values
        .map((field, value) => field.name + " = " + printWanderValue(value))
        .mkString(", ") + "}"
    case WanderValue.Bytes(value)           => printBytes(value)
    case WanderValue.Graph(value)           => printGraph(value)
    case WanderValue.Identifier(identifier) => printIdentifier(identifier)
    case WanderValue.Statement(statement)   => printStatement(statement)

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
