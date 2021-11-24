/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import cats.effect.IO
import dev.ligature.*
import fs2.Stream

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class InMemoryQueryTx(private val store: DatasetStore) extends QueryTx {

  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Stream[IO, Statement] = {
    Stream.emits(store.statements.toSeq)
  }

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  def matchStatements(
      entity: Option[Identifier],
      attribute: Option[Identifier],
      value: Option[Value]
  ): Stream[IO, Statement] = {
    var res = Stream.emits(store.statements.toSeq)
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

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all.
    */
  def matchStatementsRange(
      entity: Option[Identifier],
      attribute: Option[Identifier],
      range: dev.ligature.Range
  ): Stream[IO, Statement] = {
    var res = Stream.emits(store.statements.toSeq)
    if (entity.isDefined) {
      res = res.filter(_.entity == entity.get)
    }
    if (attribute.isDefined) {
      res = res.filter(_.attribute == attribute.get)
    }
    res = res.filter { ps =>
      val testValue = ps.value
      (testValue, range) match {
        case (StringLiteral(v), StringLiteralRange(start, end)) =>
          v >= start && v < end
        case (IntegerLiteral(v), IntegerLiteralRange(start, end)) =>
          v >= start && v < end
        case _ => false
      }
    }
    res
  }

  /** Returns the PersistedStatement for the given context. */
  def statementForContext(context: Identifier): IO[Option[Statement]] = IO {
    store.statements.find(_.context == context)
  }
}
