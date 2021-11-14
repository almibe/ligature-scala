/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.{Identifier, Value, QueryTx, WriteTx, Statement, Range}
import cats.effect.IO
import fs2.Stream

/**
 * The Graph type is a non-transactional in-memory data structure that can be used internally in Wander scripts.
 * It implements both QueryTx and WriteTx.
 */
class Graph extends QueryTx, WriteTx {
  /** Returns all PersistedStatements in this Dataset. */
  override def allStatements(): Stream[IO, Statement] = ???

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
  override def matchStatements(
                       entity: Option[Identifier] = None,
                       attribute: Option[Identifier] = None,
                       value: Option[Value] = None
                     ): Stream[IO, Statement] = ???

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all. */
  override def matchStatementsRange(
                            entity: Option[Identifier] = None,
                            attribute: Option[Identifier] = None,
                            value: Range
                          ): Stream[IO, Statement] = ???

  /** Returns the PersistedStatement for the given context. */
  override def statementForContext(
                           context: Identifier,
                         ): IO[Option[Statement]] = ???

                           /** Creates a new, unique Entity within this Dataset by combining a UUID and an optional prefix.
   * Note: Entities are shared across named graphs in a given Dataset. */
  override def newIdentifier(prefix: String = ""): IO[Identifier] = ???

  /** Adds a given Statement to this Dataset.
   * If the Statement already exists nothing happens (TODO maybe add it with a new context?).
   * Note: Potentially could trigger a ValidationError if the Statement's Context already exists
   * for a different Statement. */
  override def addStatement(statement: Statement): IO[Unit] = ???

  /** Removes a given PersistedStatement from this Dataset.
   * If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
   * This function returns Ok(true) only if the given PersistedStatement was found and removed. */
  override def removeStatement(
                       statement: Statement,
                     ): IO[Unit] = ???
}
