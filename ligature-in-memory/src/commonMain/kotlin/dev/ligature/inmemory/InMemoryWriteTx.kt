/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*
import dev.ligature.idgen.genId

/** Represents a WriteTx within the context of a Ligature instance and a single
 * Dataset
 */
class InMemoryWriteTx(private val store: DatasetStore) : WriteTx {
  private sealed interface Operation
  private data class AddOperation(val statement: Statement) : Operation
  private data class DeleteOperation(val statement: Statement) : Operation

  private var isCanceled = false
  private val operations = mutableListOf<Operation>()

  /** Creates a new, unique Entity within this Dataset. */
  override suspend fun newIdentifier(prefix: String): Identifier {
    return Identifier(prefix + genId()) //TODO check that id doesn't exist
  }

//  private def newAnonymousEntityInternal(
//      prefix: String = ""
//  ): Either[LigatureError, Identifier] =
//    Identifier.fromString(prefix + genId())

  /** Adds a given Statement to this Dataset. If the Statement already exists
   * nothing happens (TODO maybe add it with a new context?). Note: Potentially
   * could trigger a ValidationError
   */
  override suspend fun addStatement(statement: Statement): Unit {
    operations.add(AddOperation(statement))
  }

  /** Removes a given Statement from this Dataset. If the Statement doesn't
   * exist nothing happens and returns Ok(false). This function returns
   * Ok(true) only if the given Statement was found and removed. Note:
   * Potentially could trigger a ValidationError.
   */
  override suspend fun removeStatement(statement: Statement): Unit {
    operations.add(DeleteOperation(statement))
  }

  /**
   * This method handles cleaning up the Tx.
   * If the Tx is canceled nothing happens,
   * but if the Tx is not canceled then all operations are applied to the store.
   */
  fun close() {
    if (!isCanceled) {
      for (operation in operations) {
        when (operation) {
          is AddOperation -> {
            store.statements.add(operation.statement)
          }

          is DeleteOperation -> {
            store.statements.remove(operation.statement)
          }
        }
      }
    }
  }
}
