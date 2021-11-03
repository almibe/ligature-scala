/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import cats.effect.IO
import dev.ligature._
import java.util.UUID
import cats.data.EitherT

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
class InMemoryWriteTx(private val store: DatasetStore) extends WriteTx {
    private var _isCanceled = false
    private var _newDatasetStore = store.copy()

    /** Creates a new, unique Entity within this Dataset.
     *  Note: Entities are shared across named graphs in a given Dataset. */
    override def newIdentifier(prefix: String): EitherT[IO, LigatureError, Identifier] = EitherT(IO {
        Identifier.fromString(prefix + UUID.randomUUID())
    })

    private def newAnonymousEntityInternal(prefix: String = ""): Either[LigatureError, Identifier] = Identifier.fromString(prefix + UUID.randomUUID())

    /** Adds a given Statement to this Dataset.
     *  If the Statement already exists nothing happens (TODO maybe add it with a new context?).
     *  Note: Potentially could trigger a ValidationError */
    override def addStatement(statement: Statement): EitherT[IO, LigatureError, Statement] = EitherT(IO {
        _newDatasetStore = _newDatasetStore.copy(statements = _newDatasetStore.statements + statement)
        Right(statement)
    })

    /** Removes a given Statement from this Dataset.
     *  If the Statement doesn't exist nothing happens and returns Ok(false).
     *  This function returns Ok(true) only if the given Statement was found and removed.
     *  Note: Potentially could trigger a ValidationError. */
    def removeStatement(persistedStatement: Statement): EitherT[IO, LigatureError, Boolean] = EitherT(IO {
        if (_newDatasetStore.statements.contains(persistedStatement)) {
            _newDatasetStore = _newDatasetStore.copy(statements = _newDatasetStore.statements.excl(persistedStatement))
            Right(true)
        } else {
            Right(false)
        }
    })

    /** Cancels this transaction so that none of the changes made so far will be stored.
     *  This also closes this transaction so no other methods can be called without returning a LigatureError. */
    def cancel(): EitherT[IO, LigatureError, Unit] = EitherT({
        _isCanceled = true
        IO(Right(()))
    })

    /** Tracks if the given WriteTx has been canceled or not.
     *  Used in the release method of the Resource[IO, WriteTx]. */
    private[inmemory] def isCanceled(): Boolean = _isCanceled

    /** Returns the DatasetStore that has been modified by this WriteTx.
     *  Used in the release method of the Resource[IO, WriteTx]. */
    private[inmemory] def newDatasetStore(): DatasetStore = _newDatasetStore
}
