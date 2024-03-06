/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class InMemoryQueryTx(private val store: DatasetStore) extends QueryTx {

  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Iterator[Statement] = store.edges.iterator

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  override def matchStatements(
      entity: Option[LigatureValue.Identifier],
      attribute: Option[LigatureValue.Identifier],
      value: Option[LigatureValue]
  ): Iterator[Statement] = {
    var res = store.edges.iterator
    if (entity.isDefined) {
      res = res.filter(_.entity == entity.get)
    }
    if (attribute.isDefined) {
      res = res.filter(_.attribute == attribute.get)
    }
    if (value.isDefined) {
      res = res.filter(_.value == value.get)
    }
    res
  }

//  /** Returns all PersistedStatements that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  override def matchStatementsRange(
//      source: Option[Identifier],
//      label: Option[Identifier],
//      range: dev.ligature.Range
//  ): Iterator[Statement] = {
//    var res = Stream.emits(store.edges.toSeq)
//    if (source.isDefined) {
//      res = res.filter(_.source == source.get)
//    }
//    if (label.isDefined) {
//      res = res.filter(_.label == label.get)
//    }
//    res = res.filter { ps =>
//      val testValue = ps.target
//      (testValue, range) match {
//        case (StringValue(v), StringValueRange(start, end)) =>
//          v >= start && v < end
//        case (IntegerValue(v), IntegerValueRange(start, end)) =>
//          v >= start && v < end
//        case _ => false
//      }
//    }
//    res
//  }
}
