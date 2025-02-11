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

case class InMemoryNetwork(val value: Set[Triple]) extends Network {
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
