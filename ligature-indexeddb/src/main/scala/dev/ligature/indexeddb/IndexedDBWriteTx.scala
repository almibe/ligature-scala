/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.indexeddb

// import cats.effect.IO
// import dev.ligature._

// import java.util.UUID

// /** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
// class InMemoryWriteTx(private val store: DatasetStore) extends WriteTx {
//     private var _isCanceled = false
//     private var _newDatasetStore = store.copy()

//     /** Creates a new, unique Entity within this Dataset.
//      *  Note: Entities are shared across named graphs in a given Dataset. */
//     override def newAnonymousEntity(prefix: String): IO[Either[LigatureError, Entity]] = IO {
//         Right(Entity(prefix + UUID.randomUUID()))
//     }

//     private def newAnonymousEntityInternal(prefix: String = ""): Entity = Entity(prefix + UUID.randomUUID())

//     /** Adds a given Statement to this Dataset.
//      *  If the Statement already exists nothing happens (TODO maybe add it with a new context?).
//      *  Note: Potentally could trigger a ValidationError */
//     override def addStatement(statement: Statement, prefix: String): IO[Either[LigatureError, PersistedStatement]] = IO {
//         val persistedStatement = PersistedStatement(statement, newAnonymousEntityInternal(prefix))
//         _newDatasetStore = _newDatasetStore.copy(statements = _newDatasetStore.statements + persistedStatement)
//         Right(persistedStatement)
//     }

//     /** Removes a given PersistedStatement from this Dataset.
//      *  If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
//      *  This function returns Ok(true) only if the given PersistedStatement was found and removed.
//      *  Note: Potentally could trigger a ValidationError. */
//     def removeStatement(persistedStatement: PersistedStatement): IO[Either[LigatureError, Boolean]] = IO {
//         if (_newDatasetStore.statements.contains(persistedStatement)) {
//             _newDatasetStore = _newDatasetStore.copy(statements = _newDatasetStore.statements.excl(persistedStatement))
//             Right(true)
//         } else {
//             Right(false)
//         }
//     }

//     /** Cancels this transaction so that none of the changes made so far will be stored.
//      *  This also closes this transaction so no other methods can be called without returning a LigatureError. */
//     def cancel(): IO[Either[LigatureError, Unit]] = {
//         _isCanceled = true
//         IO(Right(()))
//     }

//     /** Tracks if the given WriteTx has been canceled or not.
//      *  Used in the release method of the Resource[IO, WriteTx]. */
//     private[inmemory] def isCanceled(): Boolean = _isCanceled

//     /** Returns the DatasetStore that has been modified by this WriteTx.
//      *  Used in the release method of the Resource[IO, WriteTx]. */
//     private[inmemory] def newDatasetStore(): DatasetStore = _newDatasetStore
// }
