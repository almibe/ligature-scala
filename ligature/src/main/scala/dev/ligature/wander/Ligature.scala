/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

trait INetwork {
  def write(): Set[Triple]
  def count(): Long
  def union(other: INetwork): INetwork
  def minus(other: INetwork): INetwork
  def apply(values: Map[String, LigatureValue]): INetwork
  def educe(pattern: INetwork): Set[Map[String, LigatureValue]]
  // def query: Network -> Network -> Network
  // def infer: Network -> Network -> Network
}

//sealed trait Range
//final case class StringValueRange(start: String, end: String) extends Range
//final case class IntegerValueRange(start: Long, end: Long) extends Range
final case class Triple(
    entity: LigatureValue.Word,
    attribute: LigatureValue.Word,
    value: LigatureValue
)

enum LigatureValue:
  case Word(value: String)
  case StringValue(value: String)
  case IntegerValue(value: Long)
  case BytesValue(value: Seq[Byte])
  case Record(values: Map[String, LigatureValue])
  case Quote(value: Seq[LigatureValue])
  case Network(network: INetwork)
