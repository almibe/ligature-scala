/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import dev.ligature.*
import java.util.*

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
class InMemoryWriteTx(store: DatasetStore) : WriteTx {
    private var _isCanceled = false
    private var _newDatasetStore = store.copy()

    /** Creates a new, unique Entity within this Dataset.
     *  Note: Entities are shared across named graphs in a given Dataset. */
    override suspend fun newAnonymousEntity(prefix: String): Either<LigatureError, Entity> {
        val id = prefix + UUID.randomUUID().toString()
        return when (val res = Entity.from(id)) {
            is None -> Either.Left(LigatureError("Could not create Entity with id - $id"))
            is Some -> Either.Right(res.value)
        }
    }

    /** Adds a given Statement to this Dataset.
     *  If the Statement already exists nothing happens (TODO maybe add it with a new context?).
     *  Note: Potentially could trigger a ValidationError */
    override suspend fun addStatement(statement: Statement): Either<LigatureError, Unit> {
        _newDatasetStore = _newDatasetStore.copy(statements = _newDatasetStore.statements + statement)
        return Either.Right(Unit)
    }

    /** Removes a given PersistedStatement from this Dataset.
     *  If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
     *  This function returns Ok(true) only if the given PersistedStatement was found and removed.
     *  Note: Potentially could trigger a ValidationError. */
    override suspend fun removeStatement(statement: Statement): Either<LigatureError, Boolean> =
        if (_newDatasetStore.statements.contains(statement)) {
            _newDatasetStore = _newDatasetStore.copy(statements = _newDatasetStore.statements.minus(statement))
            Either.Right(true)
        } else {
            Either.Right(false)
        }

    /** Cancels this transaction so that none of the changes made so far will be stored.
     *  This also closes this transaction so no other methods can be called without returning a LigatureError. */
    override suspend fun cancel(): Either<LigatureError, Unit> {
        _isCanceled = true
        return Either.Right(Unit)
    }

    /** Tracks if the given WriteTx has been canceled or not.
     *  Used in the release method of the Resource[Task, WriteTx]. */
    fun isCanceled(): Boolean = _isCanceled

    /** Returns the DatasetStore that has been modified by this WriteTx.
     *  Used in the release method of the Resource[Task, WriteTx]. */
    fun newDatasetStore(): DatasetStore = _newDatasetStore
}
