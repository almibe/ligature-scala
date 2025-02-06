/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import java.util.HexFormat
//import com.google.gson.Gson

enum WanderValue:
  case Element(element: Element)
  case Network(network: Set[Entry])
  case Quote(name: Element, args: Seq[WanderValue])

trait Function:
  def call(args: Seq[WanderValue]): Either[WanderError, WanderValue]

case class HostFunction(
    docString: String,
    parameters: Seq[String],
    fn: (
        arguments: Seq[WanderValue]
    ) => Either[WanderError, WanderValue]
) extends Function {
  override def call(
      args: Seq[WanderValue]
  ): Either[WanderError, WanderValue] = ???
  //  fn.apply(args).map(_)
}

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    state: Ligature
): Either[WanderError, Ligature] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
    expression <- process(terms)
  } yield expression
  expression match
    case Left(value)  => Left(value)
    case Right(value) => ???
    // eval(value.head, state) match
    //   case Right(WanderValue.Network(value)) => ???//Right(value)
    //   case Right(WanderValue.Element(element)) => ???
    //   case Right(_) => ???
    //   case Left(value) => ???//Left(value)

case class Inspect(
    tokens: Either[WanderError, Seq[Token]],
    terms: Either[WanderError, Seq[Term]]
)

def inspect(script: String): Inspect = {
  val tokens = tokenize(script)

  val terms = if (tokens.isRight) {
    parse(tokens.getOrElse(???))
  } else {
    Left(WanderError("Previous error."))
  }

  // val expression = if (terms.isRight) {
  //   process(terms.getOrElse(???))
  // } else {
  //   Left(WanderError("Previous error."))
  // }

  Inspect(tokens, terms)
}

def printResult(value: Either[WanderError, WanderValue]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printWanderValue(value)
  }

val formatter = HexFormat.of()

def printWanderValue(value: WanderValue): String = ???

// def printLigatureValue(value: LigatureValue): String =
//   value match
//     case LigatureValue.Slot(name)         => s"?$name"
//     case LigatureValue.Int(value)         => value.toString()
//     case LigatureValue.StringValue(value)      => printString(value)
//     case LigatureValue.Quote(values) =>
//       "[" + values.map(value => printLigatureValue(value)).mkString(", ") + "]"
//     case LigatureValue.Bytes(value)           => printBytes(value)
//     case LigatureValue.Network(value)           => printNetwork(value)
//     case LigatureValue.Word(word) => word

// def printTriple(triple: Triple) =
//   val value = printTripleValue(triple.value)
//   s"${triple.entity.value} ${triple.attribute.value} $value"

// def printTripleValue(value: LigatureValue): String =
//   value match
//     case LigatureValue.Bytes(value)   => printBytes(value)
//     case LigatureValue.Word(word)   => word
//     case LigatureValue.Int(value) => value.toString()
//     case LigatureValue.StringValue(value)  => printString(value)
//     case LigatureValue.Quote(quote) => printQuote(quote)
//     case LigatureValue.Network(network) => ???
//     case LigatureValue.Slot(slot) => "$" + slot

// def printQuote(network: INetwork) = network.write()
//   .map(printTriple)
//   .mkString("{ ", ", ", " }") //s"{ ${network.map(triple => printTriple(triple))} }"

// def printNetwork(network: INetwork) = network.write()
//   .map(printTriple)
//   .mkString("{ ", ", ", " }") //s"{ ${network.map(triple => printTriple(triple))} }"

// def printString(value: String) =
//   val gson = Gson()
//   gson.toJson(value)
