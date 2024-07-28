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
  def query(search: INetwork, template: INetwork): INetwork
  def infer(search: INetwork, template: INetwork): INetwork
}

final case class Triple(
    entity: LigatureValue.Word,
    attribute: LigatureValue.Word,
    value: LigatureValue
)

enum LigatureValue:
  case Word(value: String)
  case Slot(value: String)
  case StringValue(value: String)
  case Int(value: Long)
  case Bytes(value: Seq[Byte])
  case Quote(value: Seq[LigatureValue])
  case Network(network: INetwork)
