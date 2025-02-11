/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import cats.effect.IO
import fs2.Stream

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

type Value = LigatureValue.Element | LigatureValue.Literal | LigatureValue.Quote

type Triple = (LigatureValue.Element, LigatureValue.Element, Value)

trait Network {
  def toStream(): Stream[IO, Triple]
}

class InMemoryNetwork(val value: Set[Triple]) extends Network {
  override def toStream(): Stream[IO, Triple] = 
    Stream.emits(value.toSeq)
}

enum LigatureValue:
  case Element(value: String)
  case Literal(value: String)
  case Variable(value: String)
  case Quote(value: Seq[LigatureValue])
  case NetworkRef(value: Network)
  case Pattern(
      value: Set[(Element | Variable, Element | Variable, Variable | Element | Literal | Quote)]
  )

// trait Ligature {
//   def networks(): Stream[IO, String]
//   def addNetwork(name: String): IO[Unit]
//   def removeNetwork(name: String): IO[Unit]
//   def read(name: String): Stream[IO, Triple]
//   def addEntries(name: String, entries: Stream[IO, Triple]): Stream[IO, Unit]
//   def removeEntries(name: String, entries: Stream[IO, Triple]): Stream[IO, Unit]
//   def query(
//       name: String,
//       query: LigatureValue.Pattern,
//       template: LigatureValue.Pattern | LigatureValue.Quote
//   ): Stream[IO, Triple]
// }
