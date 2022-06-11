/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import cats.effect.IO
import dev.ligature.*

import jetbrains.exodus.{ArrayByteIterable, ByteIterable}
import jetbrains.exodus.bindings.{BooleanBinding, StringBinding}
import jetbrains.exodus.env.Transaction

/** Represents a WriteTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusWriteTx(
    private val tx: Transaction,
    private val xodusOperations: XodusOperations,
    private val datasetID: ByteIterable
) extends WriteTx {

  private def lookupOrCreateIdentifier(identifier: Identifier): ByteIterable =
    val identifierToIdStore = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
    val encodedName = StringBinding.stringToEntry(identifier.name)
    val identifierResult = identifierToIdStore.get(tx, encodedName)
    if (identifierResult != null) {
      identifierResult
    } else {
      val internalId = xodusOperations.nextID(tx)
      val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
      identifierToIdStore.put(tx, encodedName, internalId)
      idToIdentifierStore.put(tx, internalId, encodedName)
      internalId
    }

  // TODO this function might be able to be merged with lookupOrCreateIdentifier
  private def lookupOrCreateStringLiteral(literal: StringLiteral): ByteIterable =
    val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    val encodedString = StringBinding.stringToEntry(literal.value)
    val result = stringToIdStore.get(tx, encodedString)
    if (result == null) {
      result
    } else {
      val internalId = xodusOperations.nextID(tx)
      val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
      stringToIdStore.put(tx, encodedString, internalId)
      idToStringStore.put(tx, internalId, encodedString)
      internalId
    }

  /** Takes a value and returns a ByteIterable that can be stored.
    * The ByteIterable is made up of a Byte that represents the Type and
    * a 8 Byte value that represents the value that can be stored.
    * Identifier -> Identifier's Internal ID
    * String -> String Internal ID
    * Long -> Value
    * Bytes -> Bytes Internal ID
    */
  private def encodeValue(value: Value): ByteIterable =
    value match {
      case identifier: Identifier =>
        val id = lookupOrCreateIdentifier(identifier)
        ???
      case stringLiteral: StringLiteral =>
        lookupOrCreateStringLiteral(stringLiteral)
      case integerLiteral: IntegerLiteral =>
        ???
    }

  private def statementExists(
      entity: ByteIterable,
      attribute: ByteIterable,
      value: ByteIterable
  ): Boolean = {
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    val encodedStatement =
      datasetID.getBytesUnsafe
        ++ entity.getBytesUnsafe
        ++ attribute.getBytesUnsafe
        ++ value.getBytesUnsafe
    eavStore.get(tx, ArrayByteIterable(encodedStatement)) != null
  }

  private def storeStatement(
      entity: ByteIterable,
      attribute: ByteIterable,
      value: ByteIterable
  ): Unit = {
    val d = datasetID.getBytesUnsafe.slice(0, 8)
    val e = entity.getBytesUnsafe.slice(0, 8)
    val a = attribute.getBytesUnsafe.slice(0, 8)
    val v = value.getBytesUnsafe.slice(0, 9)
    // EAV
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    eavStore.put(tx, ArrayByteIterable(d ++ e ++ a ++ v), BooleanBinding.booleanToEntry(true))
    // EVA
    val evaStore = xodusOperations.openStore(tx, LigatureStore.EVAStore)
    evaStore.put(tx, ArrayByteIterable(d ++ e ++ v ++ a), BooleanBinding.booleanToEntry(true))
    // AEV
    val aevStore = xodusOperations.openStore(tx, LigatureStore.AEVStore)
    aevStore.put(tx, ArrayByteIterable(d ++ a ++ e ++ v), BooleanBinding.booleanToEntry(true))
    // AVE
    val aveStore = xodusOperations.openStore(tx, LigatureStore.AVEStore)
    aveStore.put(tx, ArrayByteIterable(d ++ a ++ v ++ e), BooleanBinding.booleanToEntry(true))
    // VEA
    val veaStore = xodusOperations.openStore(tx, LigatureStore.VEAStore)
    veaStore.put(tx, ArrayByteIterable(d ++ v ++ e ++ a), BooleanBinding.booleanToEntry(true))
    // VAE
    val vaeStore = xodusOperations.openStore(tx, LigatureStore.VAEStore)
    vaeStore.put(tx, ArrayByteIterable(d ++ v ++ a ++ e), BooleanBinding.booleanToEntry(true))
  }

  /** Creates a new, unique Entity within this Dataset. */
  override def newIdentifier(prefix: String): IO[Identifier] = ???

  /** Adds a given Statement to this Dataset. If the Statement already exists
    * nothing happens (TODO maybe add it with a new context?). Note: Potentially
    * could trigger a ValidationError
    */
  override def addStatement(statement: Statement): IO[Unit] = IO {
    val entityID = lookupOrCreateIdentifier(statement.entity)
    val attributeID = lookupOrCreateIdentifier(statement.attribute)
    val encodedValue = encodeValue(statement.value)
    if (statementExists(entityID, attributeID, encodedValue)) {
      ()
    } else {
      storeStatement(entityID, attributeID, encodedValue)
    }
  }

  /** Removes a given Statement from this Dataset. If the Statement doesn't
    * exist nothing happens and returns Ok(false). This function returns
    * Ok(true) only if the given Statement was found and removed. Note:
    * Potentially could trigger a ValidationError.
    */
  def removeStatement(persistedStatement: Statement): IO[Unit] = ???
}
