/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*

case class Test2(val x: Int)

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
// class InMemoryQueryTx(private val store: DatasetStore) extends QueryTx {

//   /** Returns all PersistedTriples in this Dataset. */
//   def allTriples(): Iterator[Triple] = store.edges.iterator

//   /** Returns all PersistedTriples that match the given criteria. If a
//     * parameter is None then it matches all, so passing all Nones is the same as
//     * calling allTriples.
//     */
//   override def matchTriples(
//       entity: Option[LigatureValue.Word],
//       attribute: Option[LigatureValue.Word],
//       value: Option[LigatureValue]
//   ): Iterator[Triple] = {
//     var res = store.edges.iterator
//     if (entity.isDefined) {
//       res = res.filter(_.entity == entity.get)
//     }
//     if (attribute.isDefined) {
//       res = res.filter(_.attribute == attribute.get)
//     }
//     if (value.isDefined) {
//       res = res.filter(_.value == value.get)
//     }
//     res
//   }

//  /** Returns all PersistedTriples that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  override def matchTriplesRange(
//      source: Option[Word],
//      label: Option[Word],
//      range: dev.ligature.Range
//  ): Iterator[Triple] = {
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
//        case (String(v), StringRange(start, end)) =>
//          v >= start && v < end
//        case (Int(v), IntRange(start, end)) =>
//          v >= start && v < end
//        case _ => false
//      }
//    }
//    res
//  }
// }
