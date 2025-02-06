/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

final case class Element(value: String)

enum Entry:
  case Extends(
      element: Element,
      concept: Element
  ) extends Entry
  case NotExtends(
      element: Element,
      concept: Element
  ) extends Entry
  case Role(
      first: Element,
      role: Element,
      second: Element
  ) extends Entry

given Ordering[Entry] with
  def compare(left: Entry, right: Entry): Int = left.ordinal.compare(right.ordinal)

enum Slot:
  case Any extends Slot
  case Variable(name: String) extends Slot
  case Value(element: Element) extends Slot

final case class Query(
    first: Slot,
    role: Slot,
    second: Slot
)

trait Ligature {
  def collections(): Either[LigatureError, Seq[String]]
  def addCollection(name: String): Either[LigatureError, Unit]
  def removeCollection(name: String): Either[LigatureError, Unit]
  def entries(name: String): Either[LigatureError, Seq[Entry]]
  def addEntries(name: String, entries: Seq[Entry]): Either[LigatureError, Unit]
  def removeEntries(name: String, entries: Seq[Entry]): Either[LigatureError, Unit]
  def filter(name: String, pattern: Query): Either[LigatureError, Seq[Entry]]
//  def query(name: String, query: Set[Query]): Either[LigatureError, Seq[Entry]]
}
