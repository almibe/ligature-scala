/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import cats.effect.IO
import dev.ligature._
import dev.ligature.idgen.genId
import cats.data.EitherT
import java.util.concurrent.locks.Lock

/** Represents a WriteTx within the context of a Ligature instance and a single
  * Dataset
  */
class InMemoryWriteTx(private val store: DatasetStore) extends WriteTx {
  private var isCanceled = false
  private var newDatasetStore = store.copy()

  /** Creates a new, unique Entity within this Dataset. */
  override def newIdentifier(prefix: String): IO[Identifier] = IO.defer {
    // TODO needs to assert that the generated Id is unique within this Dataset
    Identifier.fromString(prefix + genId()) match {
      case Right(id) => IO(id)
      case Left(_) =>
        IO.raiseError(
          RuntimeException(s"Illegal Identifier Prefix - $prefix")
        )
    }
  }

  private def newAnonymousEntityInternal(
      prefix: String = ""
  ): Either[LigatureError, Identifier] =
    Identifier.fromString(prefix + genId())

  /** Adds a given Statement to this Dataset. If the Statement already exists
    * nothing happens (TODO maybe add it with a new context?). Note: Potentially
    * could trigger a ValidationError
    */
  override def addStatement(statement: Statement): IO[Unit] = IO {
    newDatasetStore =
      newDatasetStore.copy(statements = newDatasetStore.statements + statement)
    ()
  }

  /** Removes a given Statement from this Dataset. If the Statement doesn't
    * exist nothing happens and returns Ok(false). This function returns
    * Ok(true) only if the given Statement was found and removed. Note:
    * Potentially could trigger a ValidationError.
    */
  override def removeStatement(persistedStatement: Statement): IO[Unit] = IO {
    if (newDatasetStore.statements.contains(persistedStatement)) {
      newDatasetStore = newDatasetStore.copy(statements =
        newDatasetStore.statements.excl(persistedStatement)
      )
      ()
    } else {
      ()
    }
  }

  /** Returns the DatasetStore that has been modified by this WriteTx. Used in
    * the release method of the Resource[IO, WriteTx].
    */
  private[inmemory] def modifiedDatasetStore(): DatasetStore = newDatasetStore
}
