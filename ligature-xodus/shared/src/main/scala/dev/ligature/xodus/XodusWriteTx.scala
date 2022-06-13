/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import cats.effect.IO
import dev.ligature.*
import dev.ligature.idgen.genId
import jetbrains.exodus.{ByteIterable, CompoundByteIterable}
import jetbrains.exodus.bindings.{BooleanBinding, ByteBinding, LongBinding, StringBinding}
import jetbrains.exodus.env.Transaction
import jetbrains.exodus.util.IdGenerator

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
      val internalIdWithDataset = CompoundByteIterable(Array(datasetID, internalId))
      val encodedNameWithDataset = CompoundByteIterable(Array(datasetID, encodedName))
      val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
      identifierToIdStore.put(tx, encodedNameWithDataset, internalId)
      idToIdentifierStore.put(tx, internalIdWithDataset, encodedName)
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
      val internalIdWithDataset = CompoundByteIterable(Array(datasetID, internalId))
      val encodedStringWithDataset = CompoundByteIterable(Array(datasetID, encodedString))
      val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
      stringToIdStore.put(tx, encodedStringWithDataset, internalId)
      idToStringStore.put(tx, internalIdWithDataset, encodedString)
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
        val temp = CompoundByteIterable(
          Array(
            ByteBinding.byteToEntry(LigatureValueType.Identifier.id),
            lookupOrCreateIdentifier(identifier)
          )
        )
        temp
      case stringLiteral: StringLiteral =>
        CompoundByteIterable(
          Array(
            ByteBinding.byteToEntry(LigatureValueType.String.id),
            lookupOrCreateStringLiteral(stringLiteral)
          )
        )
      case integerLiteral: IntegerLiteral =>
        CompoundByteIterable(
          Array(
            ByteBinding.byteToEntry(LigatureValueType.Integer.id),
            LongBinding.longToEntry(integerLiteral.value)
          )
        )
      // TODO Bytes
    }

  private def statementExists(
      entity: ByteIterable,
      attribute: ByteIterable,
      value: ByteIterable
  ): Boolean = {
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    val encodedStatement = CompoundByteIterable(Array(datasetID, entity, attribute, value))
    eavStore.get(tx, encodedStatement) != null
  }

  private def storeStatement(
      entity: ByteIterable,
      attribute: ByteIterable,
      value: ByteIterable
  ): Unit = {
    val d = datasetID
    val e = entity
    val a = attribute
    val v = value
    // EAV
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    eavStore.put(tx, CompoundByteIterable(Array(d, e, a, v)), BooleanBinding.booleanToEntry(true))
    // EVA
    val evaStore = xodusOperations.openStore(tx, LigatureStore.EVAStore)
    evaStore.put(tx, CompoundByteIterable(Array(d, e, v, a)), BooleanBinding.booleanToEntry(true))
    // AEV
    val aevStore = xodusOperations.openStore(tx, LigatureStore.AEVStore)
    aevStore.put(tx, CompoundByteIterable(Array(d, a, e, v)), BooleanBinding.booleanToEntry(true))
    // AVE
    val aveStore = xodusOperations.openStore(tx, LigatureStore.AVEStore)
    aveStore.put(tx, CompoundByteIterable(Array(d, a, v, e)), BooleanBinding.booleanToEntry(true))
    // VEA
    val veaStore = xodusOperations.openStore(tx, LigatureStore.VEAStore)
    veaStore.put(tx, CompoundByteIterable(Array(d, v, e, a)), BooleanBinding.booleanToEntry(true))
    // VAE
    val vaeStore = xodusOperations.openStore(tx, LigatureStore.VAEStore)
    vaeStore.put(tx, CompoundByteIterable(Array(d, v, a, e)), BooleanBinding.booleanToEntry(true))
  }

  /** Returns an ID that doesn't exist within this Dataset.
    */
  override def newIdentifier(prefix: String): IO[Identifier] = IO {
    var newIdentifier = Identifier.fromString(s"$prefix${genId()}").getOrElse(???)
    var encodedIdentifier = StringBinding.stringToEntry(newIdentifier.name)
    var exists = true
    val store = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
    while (exists) {
      exists = store.get(tx, encodedIdentifier) != null
      if (exists) {
        newIdentifier = Identifier.fromString(s"$prefix${genId()}").getOrElse(???)
        encodedIdentifier = StringBinding.stringToEntry(newIdentifier.name)
      }
    }
    newIdentifier
  }

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
  def removeStatement(persistedStatement: Statement): IO[Unit] =
    ???
}
