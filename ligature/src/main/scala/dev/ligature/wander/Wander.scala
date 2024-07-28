/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import java.util.HexFormat

/** Represents a Value in the Wander language.
  */
enum WanderValue:
  case Int(value: Long)
  case Bytes(value: Seq[Byte])
  case String(value: java.lang.String)
  case Array(values: Seq[WanderValue])
  case Word(value: LigatureValue.Word)
  case Slot(name: java.lang.String)
  case Network(value: Set[dev.ligature.wander.Statement])

enum Tag:
  case Untagged
  case Single(tag: Function)
  case Chain(names: Seq[Function])

trait Function:
  def call(args: Seq[WanderValue]): Either[WanderError, WanderValue]

// case class Lambda(val lambda: Expression.Lambda) extends Function {
//   override def call(
//       args: Seq[WanderValue],
//       environment: Environment
//   ): Either[WanderError, WanderValue] =
//     var env = environment
//     lambda.parameters.zipWithIndex.foreach { (param, i) =>
//       env = env.bindVariable(param, args(i))
//     }
//     val res = eval(lambda.body, env).map(_._1)
//     res
// }

// case class PartialFunction(args: Seq[WanderValue], function: dev.ligature.wander.Function)
//     extends Function {
//   override def call(
//       args: Seq[WanderValue],
//       environment: Environment
//   ): Either[WanderError, WanderValue] = ???
// }

case class HostFunction(
    docString: String,
    parameters: Seq[String],
    resultTag: Tag,
    fn: (
        arguments: Seq[WanderValue],
    ) => Either[WanderError, (WanderValue)]
) extends Function {
  override def call(
      args: Seq[WanderValue],
  ): Either[WanderError, WanderValue] = ???
//    fn.apply(args).map(_)
}

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
): Either[WanderError, (WanderValue)] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
    expression <- process(terms)
  } yield expression
  expression match
    case Left(value)  => Left(value)
    case Right(value) => ???//Right(value)

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

def printResult(value: Either[WanderError, (WanderValue)]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printWanderValue(value)
  }

val formatter = HexFormat.of()

def printWanderValue(value: WanderValue): String =
  value match
    case WanderValue.Slot(name)         => s"?$name"
    case WanderValue.Int(value)         => value.toString()
    case WanderValue.String(value)      => printString(value)
    case WanderValue.Array(values) =>
      "[" + values.map(value => printWanderValue(value)).mkString(", ") + "]"
    case WanderValue.Bytes(value)           => printBytes(value)
    case WanderValue.Network(value)           => printNetwork(value)
    case WanderValue.Word(word) => printWord(word)

def printBytes(bytes: Seq[Byte]) = s"0x${formatter.formatHex(bytes.toArray)}"

def printWord(word: LigatureValue.Word) = s"`${word.value}`"

def printStatement(statement: Statement) =
  val value = printStatementValue(statement.value)
  s"`${statement.entity.value}` `${statement.attribute.value}` $value"

def printStatementValue(value: LigatureValue): String =
  value match
    case LigatureValue.BytesValue(value)   => printBytes(value)
    case value: LigatureValue.Word   => printWord(value)
    case LigatureValue.IntegerValue(value) => value.toString()
    case LigatureValue.StringValue(value)  => printString(value)
    case LigatureValue.Record(values) =>
      "{" + values
        .map((field, value) => field + " = " + printStatementValue(value))
        .mkString(", ") + "}"
    case LigatureValue.Quote(quote) => ???
    case LigatureValue.Network => ???

def printNetwork(network: Set[Statement]) = network
  .map(printStatement)
  .mkString("{ ", ", ", " }") //s"{ ${network.map(statement => printStatement(statement))} }"

def printString(value: String) = ???
  // val gson = Gson()
  // gson.toJson(value)
