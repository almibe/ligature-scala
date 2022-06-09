/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import cats.effect.IO
import dev.ligature.*

import java.util.UUID
import cats.data.EitherT
import jetbrains.exodus.env.Transaction

import java.util.concurrent.locks.Lock

/** Represents a WriteTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusWriteTx(private val tx: Transaction) extends WriteTx {

  /** Creates a new, unique Entity within this Dataset. */
  override def newIdentifier(prefix: String): IO[Identifier] = ???

  /** Adds a given Statement to this Dataset. If the Statement already exists
    * nothing happens (TODO maybe add it with a new context?). Note: Potentially
    * could trigger a ValidationError
    */
  override def addStatement(statement: Statement): IO[Unit] = ???

  /** Removes a given Statement from this Dataset. If the Statement doesn't
    * exist nothing happens and returns Ok(false). This function returns
    * Ok(true) only if the given Statement was found and removed. Note:
    * Potentially could trigger a ValidationError.
    */
  def removeStatement(persistedStatement: Statement): IO[Unit] = ???
}
