/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import java.util.HexFormat

enum Tag:
  case Untagged
  case Single(tag: Function)
  case Chain(names: Seq[Function])

trait Function:
  def call(args: Seq[LigatureValue]): Either[WanderError, LigatureValue]

// case class Lambda(val lambda: Expression.Lambda) extends Function {
//   override def call(
//       args: Seq[LigatureValue],
//       environment: Environment
//   ): Either[WanderError, LigatureValue] =
//     var env = environment
//     lambda.parameters.zipWithIndex.foreach { (param, i) =>
//       env = env.bindVariable(param, args(i))
//     }
//     val res = eval(lambda.body, env).map(_._1)
//     res
// }

// case class PartialFunction(args: Seq[LigatureValue], function: dev.ligature.wander.Function)
//     extends Function {
//   override def call(
//       args: Seq[LigatureValue],
//       environment: Environment
//   ): Either[WanderError, LigatureValue] = ???
// }

case class HostFunction(
    docString: String,
    parameters: Seq[String],
    resultTag: Tag,
    fn: (
        arguments: Seq[LigatureValue],
    ) => Either[WanderError, (LigatureValue)]
) extends Function {
  override def call(
      args: Seq[LigatureValue],
  ): Either[WanderError, LigatureValue] = ???
//    fn.apply(args).map(_)
}

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
): Either[WanderError, (LigatureValue)] =
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

def printResult(value: Either[WanderError, (LigatureValue)]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printLigatureValue(value)
  }

val formatter = HexFormat.of()

def printLigatureValue(value: LigatureValue): String =
  value match
    case LigatureValue.Slot(name)         => s"?$name"
    case LigatureValue.Int(value)         => value.toString()
    case LigatureValue.StringValue(value)      => printString(value)
    case LigatureValue.Quote(values) =>
      "[" + values.map(value => printLigatureValue(value)).mkString(", ") + "]"
    case LigatureValue.Bytes(value)           => printBytes(value)
    case LigatureValue.Network(value)           => printNetwork(value)
    case LigatureValue.Word(word) => word

def printBytes(bytes: Seq[Byte]) = s"0x${formatter.formatHex(bytes.toArray)}"

def printTriple(triple: Triple) =
  val value = printTripleValue(triple.value)
  s"`${triple.entity.value}` `${triple.attribute.value}` $value"

def printTripleValue(value: LigatureValue): String =
  value match
    case LigatureValue.Bytes(value)   => printBytes(value)
    case LigatureValue.Word(word)   => word
    case LigatureValue.Int(value) => value.toString()
    case LigatureValue.StringValue(value)  => printString(value)
    case LigatureValue.Quote(quote) => ???///printQuote(quote)
    case LigatureValue.Network(network) => ???
    case LigatureValue.Slot(_) => ???
    
def printNetwork(network: INetwork) = network.write()
  .map(printTriple)
  .mkString("{ ", ", ", " }") //s"{ ${network.map(triple => printTriple(triple))} }"

def printString(value: String) = ???
  // val gson = Gson()
  // gson.toJson(value)
