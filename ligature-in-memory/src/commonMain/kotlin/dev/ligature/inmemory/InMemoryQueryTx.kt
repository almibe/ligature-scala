/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*
import kotlinx.coroutines.flow.Flow
import arrow.core.Option

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class InMemoryQueryTx(private val store: DatasetStore): QueryTx {

  /** Returns all PersistedStatements in this Dataset. */
  override fun allStatements(): Flow<Statement> = TODO()
//    Stream.emits(store.statements.toSeq)

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  override fun matchStatements(
      entity: Option<Identifier>,
      attribute: Option<Identifier>,
      value: Option<Value>
  ): Flow<Statement> =  TODO() //{
//    var res = Stream.emits(store.statements.toSeq)
//    if (entity.isDefined) {
//      res = res.filter(_.entity == entity.get)
//    }
//    if (attribute.isDefined) {
//      res = res.filter(_.attribute == attribute.get)
//    }
//    if (value.isDefined) {
//      res = res.filter(_.value == value.get)
//    }
//    res
//  }

//  /** Returns all PersistedStatements that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  override def matchStatementsRange(
//      entity: Option[Identifier],
//      attribute: Option[Identifier],
//      range: dev.ligature.Range
//  ): Stream[IO, Statement] = {
//    var res = Stream.emits(store.statements.toSeq)
//    if (entity.isDefined) {
//      res = res.filter(_.entity == entity.get)
//    }
//    if (attribute.isDefined) {
//      res = res.filter(_.attribute == attribute.get)
//    }
//    res = res.filter { ps =>
//      val testValue = ps.value
//      (testValue, range) match {
//        case (StringLiteral(v), StringLiteralRange(start, end)) =>
//          v >= start && v < end
//        case (IntegerLiteral(v), IntegerLiteralRange(start, end)) =>
//          v >= start && v < end
//        case _ => false
//      }
//    }
//    res
//  }
}
