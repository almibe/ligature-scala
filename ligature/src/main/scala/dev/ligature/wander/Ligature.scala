/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import scala.math.Ordered.orderingToOrdered

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

enum LigatureValue:
  case Element(value: String)
  case Literal(value: String)
  case Variable(value: String)
  case Quote(value: Seq[LigatureValue])
  case Network(value: Set[(Element, Element, Element | Literal | Quote)])
  case Pattern(value: Set[(Element | Variable, Element | Variable, Variable | Element | Literal | Quote)])

type Value = LigatureValue.Element | LigatureValue.Literal | LigatureValue.Quote

type Triple = (LigatureValue.Element, LigatureValue.Element, Value)

given Ordering[Triple] with
  def compare(left: Triple, right: Triple) = ???
//    left._1.value compare right._1.value

trait Ligature {
  def networks(): Either[LigatureError, Multi[String]]
  def addNetwork(name: String): Uni[Unit]
  def removeNetwork(name: String): Uni[Unit]
  def read(name: String): Multi[Triple]
  def addEntries(name: String, entries: Multi[Triple]): Uni[Unit]
  def removeEntries(name: String, entries: Multi[Triple]): Uni[Unit]
  def query(name: String, query: LigatureValue.Pattern, template: LigatureValue.Pattern | LigatureValue.Quote): Multi[Triple]
}
