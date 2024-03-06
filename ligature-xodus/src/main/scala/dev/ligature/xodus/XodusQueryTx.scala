/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.*
import jetbrains.exodus.entitystore.StoreTransaction
import jetbrains.exodus.entitystore.EntityIterable
import scala.collection.mutable.ListBuffer

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusQueryTx(
    private val tx: StoreTransaction
) extends QueryTx {
  override def matchStatements(
      source: Option[LigatureValue.Identifier],
      label: Option[LigatureValue.Identifier],
      target: Option[LigatureValue]
  ): Iterator[Statement] =
    val entities = (source, label, target) match
      case (None, None, None) =>
        tx.getAll("statement")
      case (Some(source), None, None) =>
        tx.find("statement", "entity", source.value)
      case (None, Some(label), None) =>
        tx.find("statement", "attribute", label.value)
      case (None, None, Some(target)) =>
        tx.find("statement", "valueType", valueType(target))
          .intersect(tx.find("statement", "value", targetValue(target)))
      case (Some(source), Some(label), None) =>
        tx.find("statement", "entity", source.value)
          .intersect(tx.find("statement", "attribute", label.value))
      case (Some(source), None, Some(target)) =>
        tx.find("statement", "entity", source.value)
          .intersect(tx.find("statement", "valueType", valueType(target)))
          .intersect(tx.find("statement", "value", targetValue(target)))
      case (None, Some(label), Some(target)) =>
        tx.find("statement", "attribute", label.value)
          .intersect(tx.find("statement", "valueType", valueType(target)))
          .intersect(tx.find("statement", "value", targetValue(target)))
      case (Some(source), Some(label), Some(target)) =>
        tx.find("statement", "entity", source.value)
          .intersect(tx.find("statement", "attribute", label.value))
          .intersect(tx.find("statement", "valueType", valueType(target)))
          .intersect(tx.find("statement", "value", targetValue(target)))
    entitiesToStatements(entities)
}

def entitiesToStatements(entities: EntityIterable): Iterator[Statement] =
  val buffer = ListBuffer[Statement]()
  entities.forEach(entity =>
    val source: LigatureValue.Identifier =
      LigatureValue.Identifier(entity.getProperty("entity").asInstanceOf[String])
    val label: LigatureValue.Identifier =
      LigatureValue.Identifier(entity.getProperty("attribute").asInstanceOf[String])
    val target = entity.getProperty("valueType").asInstanceOf[Int] match
      case IDENTIFIER => LigatureValue.Identifier(entity.getProperty("value").asInstanceOf[String])
      case INT        => LigatureValue.IntegerValue(entity.getProperty("value").asInstanceOf[Long])
      case STRING =>
        LigatureValue.StringValue(entity.getProperty("value").asInstanceOf[String])
    buffer += Statement(source, label, target)
  )
  buffer.iterator
