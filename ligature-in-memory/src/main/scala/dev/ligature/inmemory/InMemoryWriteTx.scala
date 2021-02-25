/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature._
import monix.eval.Task

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
class InMemoryWriteTx(private val store: DatasetStore) extends WriteTx {
  private var _isCanceled = false
  private var _newDatasetStore = store.copy()

  /** Creates a new, unique Entity within this Dataset.
   *  Note: Entities are shared across named graphs in a given Dataset. */
  def newEntity(): Task[Either[LigatureError, Entity]] = Task {
    val counter = _newDatasetStore.counter + 1L
    _newDatasetStore = _newDatasetStore.copy(counter = counter)
    Right(Entity(counter))
  }

  /** Adds a given Statement to this Dataset.
   *  If the Statement already exists nothing happens (TODO maybe add it with a new context?).
   *  Note: Potentally could trigger a ValidationError */
  def addStatement(statement: Statement): Task[Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Removes a given PersistedStatement from this Dataset.
   *  If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
   *  This function returns Ok(true) only if the given PersistedStatement was found and removed.
   *  Note: Potentally could trigger a ValidationError. */
  def removeStatement(persistedStatement: PersistedStatement): Task[Either[LigatureError, Boolean]] = {
    ???
  }

  /** Cancels this transaction so that none of the changes made so far will be stored.
   *  This also closes this transaction so no other methods can be called without returning a LigatureError. */
  def cancel(): Task[Either[LigatureError, Unit]] = {
    _isCanceled = true
    Task(Right(()))
  }

  /** Tracks if the given WriteTx has been canceled or not.
   *  Used in the release method of the Resource[Task, WriteTx]. */
  def isCanceled(): Boolean = _isCanceled

  /** Returns the DatasetStore that has been modified by this WriteTx.
   *  Used in the release method of the Resource[Task, WriteTx]. */
  def newDatasetStore(): DatasetStore = _newDatasetStore
}
