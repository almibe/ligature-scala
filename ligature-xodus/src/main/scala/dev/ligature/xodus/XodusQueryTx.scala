/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.*
import jetbrains.exodus.{ByteIterable, CompoundByteIterable}
import jetbrains.exodus.bindings.{ByteBinding, LongBinding, StringBinding}
import jetbrains.exodus.env.Transaction

import scala.collection.mutable.ArrayBuffer
import jetbrains.exodus.entitystore.PersistentEntityStore
import scala.util.boundary, boundary.break
import jetbrains.exodus.entitystore.StoreTransaction
import jetbrains.exodus.entitystore.EntityIterator
import jetbrains.exodus.entitystore.EntityIterable
import scala.collection.mutable.ListBuffer

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusQueryTx(
    private val tx: StoreTransaction,
) extends QueryTx {
  override def matchEdges(source: Option[Label], label: Option[Label], target: Option[Value]): Iterator[Edge] =
    val entities = (source, label, target) match
      case (None, None, None) => 
        tx.getAll("edge")
      case (Some(source), None, None) => 
        tx.find("edge", "source", source.text)
      case (None, Some(label), None) => 
        tx.find("edge", "label", label.text)
      case (None, None, Some(target)) =>
        tx.find("edge", "targetType", targetType(target))
          .intersect(tx.find("edge", "target", targetValue(target)))
      case (Some(source), Some(label), None) => 
        tx.find("edge", "source", source.text)
          .intersect(tx.find("edge", "label", label.text))
      case (Some(source), None, Some(target)) => 
        tx.find("edge", "source", source.text)
          .intersect(tx.find("edge", "targetType", targetType(target)))
          .intersect(tx.find("edge", "target", targetValue(target)))
      case (None, Some(label), Some(target)) => 
        tx.find("edge", "label", label.text)
          .intersect(tx.find("edge", "targetType", targetType(target)))
          .intersect(tx.find("edge", "target", targetValue(target)))
      case (Some(source), Some(label), Some(target)) =>
        tx.find("edge", "source", source.text)
          .intersect(tx.find("edge", "label", label.text))
          .intersect(tx.find("edge", "targetType", targetType(target)))
          .intersect(tx.find("edge", "target", targetValue(target)))
    entitiesToEdges(entities)
}

def entitiesToEdges(entities: EntityIterable): Iterator[Edge] =
  val buffer = ListBuffer[Edge]()
  entities.forEach(entity =>
    val source = Label(entity.getProperty("source").asInstanceOf[String])
    val label = Label(entity.getProperty("label").asInstanceOf[String])
    val target = entity.getProperty("targetType").asInstanceOf[Int] match
      case VERTEX => Label(entity.getProperty("target").asInstanceOf[String])
      case INT => LigatureLiteral.IntegerLiteral(entity.getProperty("target").asInstanceOf[Long])
      case STRING => LigatureLiteral.StringLiteral(entity.getProperty("target").asInstanceOf[String])
    buffer += Edge(source, label, target)
  )
  buffer.iterator
