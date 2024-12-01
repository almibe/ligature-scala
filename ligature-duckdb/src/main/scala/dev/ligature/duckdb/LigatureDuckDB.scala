/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.duckdb

import dev.ligature.wander.Ligature
import dev.ligature.wander.LigatureError
import dev.ligature.wander.Entry
import dev.ligature.wander.Query

class LigatureDuckDB extends Ligature {
  override def collections(): Either[LigatureError, Seq[String]] = ???

  override def addCollection(name: String): Either[LigatureError, Unit] = ???

  override def removeCollection(name: String): Either[LigatureError, Unit] = ???

  override def entries(name: String): Either[LigatureError, Seq[Entry]] = ???

  override def addEntries(name: String, entries: Seq[Entry]): Either[LigatureError, Unit] = ???

  override def removeEntries(name: String, entries: Seq[Entry]): Either[LigatureError, Unit] = ???
  override def query(name: String, query: Set[Query]): Either[LigatureError, Seq[Entry]] = ???

}
