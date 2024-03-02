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
        tx.getAll("edge")
      case (Some(source), None, None) =>
        tx.find("edge", "source", source.value)
      case (None, Some(label), None) =>
        tx.find("edge", "label", label.value)
      case (None, None, Some(target)) =>
        tx.find("edge", "targetType", targetType(target))
          .intersect(tx.find("edge", "target", targetValue(target)))
      case (Some(source), Some(label), None) =>
        tx.find("edge", "source", source.value)
          .intersect(tx.find("edge", "label", label.value))
      case (Some(source), None, Some(target)) =>
        tx.find("edge", "source", source.value)
          .intersect(tx.find("edge", "targetType", targetType(target)))
          .intersect(tx.find("edge", "target", targetValue(target)))
      case (None, Some(label), Some(target)) =>
        tx.find("edge", "label", label.value)
          .intersect(tx.find("edge", "targetType", targetType(target)))
          .intersect(tx.find("edge", "target", targetValue(target)))
      case (Some(source), Some(label), Some(target)) =>
        tx.find("edge", "source", source.value)
          .intersect(tx.find("edge", "label", label.value))
          .intersect(tx.find("edge", "targetType", targetType(target)))
          .intersect(tx.find("edge", "target", targetValue(target)))
    entitiesToStatements(entities)
}

def entitiesToStatements(entities: EntityIterable): Iterator[Statement] =
  val buffer = ListBuffer[Statement]()
  entities.forEach(entity =>
    val source: LigatureValue.Identifier =
      LigatureValue.Identifier(entity.getProperty("source").asInstanceOf[String])
    val label: LigatureValue.Identifier =
      LigatureValue.Identifier(entity.getProperty("label").asInstanceOf[String])
    val target = entity.getProperty("targetType").asInstanceOf[Int] match
      case VERTEX => LigatureValue.Identifier(entity.getProperty("target").asInstanceOf[String])
      case INT    => LigatureValue.IntegerValue(entity.getProperty("target").asInstanceOf[Long])
      case STRING =>
        LigatureValue.StringValue(entity.getProperty("target").asInstanceOf[String])
    buffer += Statement(source, label, target)
  )
  buffer.iterator
