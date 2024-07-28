/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

final case class DatasetName(name: String) extends Ordered[DatasetName]:
  override def compare(that: DatasetName): Int = this.name.compare(that.name)

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

enum LigatureValue:
  case Word(value: String)
  case StringValue(value: String)
  case IntegerValue(value: Long)
  case BytesValue(value: Seq[Byte])
  case Record(values: Map[String, LigatureValue])
  case Quote(value: Seq[LigatureValue])
  case Network

//sealed trait Range
//final case class StringValueRange(start: String, end: String) extends Range
//final case class IntegerValueRange(start: Long, end: Long) extends Range
final case class Statement(
    entity: LigatureValue.Word,
    attribute: LigatureValue.Word,
    value: LigatureValue
)
