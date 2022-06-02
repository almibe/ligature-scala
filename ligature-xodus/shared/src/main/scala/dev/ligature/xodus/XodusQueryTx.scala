/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import cats.effect.IO
import dev.ligature.*
import fs2.Stream

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusQueryTx() extends QueryTx {

  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Stream[IO, Statement] = ???

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  def matchStatements(
      entity: Option[Identifier],
      attribute: Option[Identifier],
      value: Option[Value]
  ): Stream[IO, Statement] = ???

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all.
    */
  def matchStatementsRange(
      entity: Option[Identifier],
      attribute: Option[Identifier],
      range: dev.ligature.Range
  ): Stream[IO, Statement] = ???
}
