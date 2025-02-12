/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parse
import java.util.HexFormat
//import com.google.gson.Gson
import cats.effect.IO

trait Action:
  def call(stack: List[LigatureValue]): IO[List[LigatureValue]]

case class HostAction(
    docString: String,
    fn: (
        stack: List[LigatureValue]
    ) => IO[List[LigatureValue]]
) extends Action {
  override def call(
      stack: List[LigatureValue]
  ): IO[List[LigatureValue]] =
    fn.apply(stack)
}

case class WanderError(val userMessage: String) extends Throwable(userMessage)

def run(
    script: String,
    actions: Map[LigatureValue.Element, Action] = dev.ligature.wander.lib.stdActions // Map()
): IO[List[LigatureValue]] =
  val expression = for {
    tokens <- tokenize(script)
    terms <- parse(tokens)
  } yield terms
  expression match
    case Left(value)  => ??? // Left(value)
    case Right(value) => eval(actions, value, List())

case class Inspect(
    tokens: Either[WanderError, Seq[Token]],
    terms: Either[WanderError, Seq[LigatureValue]]
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

def printResult(value: Either[WanderError, LigatureValue]): String =
  value match {
    case Left(value)  => "Error: " + value.userMessage
    case Right(value) => printLigatureValue(value)
  }

val formatter = HexFormat.of()

def printLigatureValue(value: LigatureValue): String = ???

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
