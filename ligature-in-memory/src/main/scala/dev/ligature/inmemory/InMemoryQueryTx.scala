/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature._
import monix.eval.Task
import monix.reactive.Observable

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
class InMemoryQueryTx(private val store: DatasetStore) extends QueryTx {
  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Observable[Either[LigatureError, PersistedStatement]] = {
    Observable.fromIterable(store.statements.map(Right(_)))
  }

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
  def matchStatements(
                       entity: Option[Entity],
                       attribute: Option[Attribute],
                       value: Option[Value],
                     ): Observable[Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Retuns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all. */
  def matchStatementsRange(
                            entity: Option[Entity],
                            attribute: Option[Attribute],
                            value: dev.ligature.Range,
                          ): Observable[Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Returns the PersistedStatement for the given context. */
  def statementForContext(context: Entity): Task[Either[LigatureError, Option[PersistedStatement]]] = {
    ???
  }
}
