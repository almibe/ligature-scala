/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.*
import dev.ligature.idgen.genId
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.CompoundByteIterable
import jetbrains.exodus.bindings.BooleanBinding
import jetbrains.exodus.bindings.ByteBinding
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Transaction

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
class XodusWriteTx(
    private val tx: Transaction,
    private val xodusOperations: XodusOperations,
    private val datasetID: ByteIterable
) : WriteTx {

  private fun lookupIdentifier(identifier: Identifier): ByteIterable? {
    val identifierToIdStore = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
    val encodedName =
        CompoundByteIterable(arrayOf(datasetID, StringBinding.stringToEntry(identifier.name)))
    return identifierToIdStore.get(tx, encodedName)
  }

  private fun lookupOrCreateIdentifier(identifier: Identifier): ByteIterable =
      when (val res = lookupIdentifier(identifier)) {
        null -> {
          val encodedName = StringBinding.stringToEntry(identifier.name)
          val identifierToIdStore = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
          val internalId = xodusOperations.nextID(tx)
          val internalIdWithDataset = CompoundByteIterable(arrayOf(datasetID, internalId))
          val encodedNameWithDataset = CompoundByteIterable(arrayOf(datasetID, encodedName))
          val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
          identifierToIdStore.put(tx, encodedNameWithDataset, internalId)
          idToIdentifierStore.put(tx, internalIdWithDataset, encodedName)
          internalId
        }
        else -> res
      }

  private fun lookupStringLiteral(literal: StringLiteral): ByteIterable? {
    val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    val encodedString =
        CompoundByteIterable(arrayOf(datasetID, StringBinding.stringToEntry(literal.value)))
    return stringToIdStore.get(tx, encodedString)
  }

  // TODO this function might be able to be merged with lookupOrCreateIdentifier
  private fun lookupOrCreateStringLiteral(literal: StringLiteral): ByteIterable {
    val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    val encodedString = StringBinding.stringToEntry(literal.value)
    val encodedStringWithDataset = CompoundByteIterable(arrayOf(datasetID, encodedString))
    val result = stringToIdStore.get(tx, encodedStringWithDataset)
    return if (result != null) {
      result
    } else {
      val internalId = xodusOperations.nextID(tx)
      val internalIdWithDataset = CompoundByteIterable(arrayOf(datasetID, internalId))
      val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
      stringToIdStore.put(tx, encodedStringWithDataset, internalId)
      idToStringStore.put(tx, internalIdWithDataset, encodedString)
      internalId
    }
  }

  /**
   * Takes a value and returns a ByteIterable that can be stored. The ByteIterable is made up of a
   * Byte that represents the Type and an 8 Byte value that represents the value that can be stored.
   * Identifier -> Identifier's Internal ID String -> String Internal ID Long -> Value Bytes ->
   * Bytes Internal ID
   */
  private fun encodeValue(value: Value): ByteIterable =
      when (value) {
        is Identifier -> {
          val temp =
              CompoundByteIterable(
                  arrayOf(
                      ByteBinding.byteToEntry(LigatureValueType.Identifier.id),
                      lookupOrCreateIdentifier(value)))
          temp
        }
        is StringLiteral -> {
          CompoundByteIterable(
              arrayOf(
                  ByteBinding.byteToEntry(LigatureValueType.String.id),
                  lookupOrCreateStringLiteral(value)))
        }
        is IntegerLiteral -> {
          CompoundByteIterable(
              arrayOf(
                  ByteBinding.byteToEntry(LigatureValueType.Integer.id),
                  LongBinding.longToEntry(value.value)))
        }
        is BytesLiteral -> TODO()
      }

  private fun statementExists(
      entity: ByteIterable,
      attribute: ByteIterable,
      value: ByteIterable
  ): Boolean {
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    val encodedStatement = CompoundByteIterable(arrayOf(datasetID, entity, attribute, value))
    return eavStore.get(tx, encodedStatement) != null
  }

  private fun storeStatement(
      entity: ByteIterable,
      attribute: ByteIterable,
      value: ByteIterable
  ): Unit {
    val d = datasetID
    val e = entity
    val a = attribute
    val v = value
    // EAV
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    eavStore.put(tx, CompoundByteIterable(arrayOf(d, e, a, v)), BooleanBinding.booleanToEntry(true))
    // EVA
    val evaStore = xodusOperations.openStore(tx, LigatureStore.EVAStore)
    evaStore.put(tx, CompoundByteIterable(arrayOf(d, e, v, a)), BooleanBinding.booleanToEntry(true))
    // AEV
    val aevStore = xodusOperations.openStore(tx, LigatureStore.AEVStore)
    aevStore.put(tx, CompoundByteIterable(arrayOf(d, a, e, v)), BooleanBinding.booleanToEntry(true))
    // AVE
    val aveStore = xodusOperations.openStore(tx, LigatureStore.AVEStore)
    aveStore.put(tx, CompoundByteIterable(arrayOf(d, a, v, e)), BooleanBinding.booleanToEntry(true))
    // VEA
    val veaStore = xodusOperations.openStore(tx, LigatureStore.VEAStore)
    veaStore.put(tx, CompoundByteIterable(arrayOf(d, v, e, a)), BooleanBinding.booleanToEntry(true))
    // VAE
    val vaeStore = xodusOperations.openStore(tx, LigatureStore.VAEStore)
    vaeStore.put(tx, CompoundByteIterable(arrayOf(d, v, a, e)), BooleanBinding.booleanToEntry(true))
  }

  /** Returns an ID that doesn't exist within this Dataset. */
  override suspend fun newIdentifier(prefix: String): Either<LigatureError, Identifier> {
    var newIdentifier = Identifier.create("$prefix${genId()}")
    return when (newIdentifier) {
      is Right -> {
        var encodedIdentifier = StringBinding.stringToEntry(newIdentifier.value.name)
        var exists = true
        val store = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
        while (exists) {
          exists = store.get(tx, encodedIdentifier) != null
          if (exists) {
            newIdentifier = Identifier.create("$prefix${genId()}")
            val id = newIdentifier.orNull()!!
            encodedIdentifier = StringBinding.stringToEntry(id.name)
          }
        }
        Right(newIdentifier.orNull()!!)
      }
      is Left -> {
        return newIdentifier
      }
    }
  }

  /**
   * Adds a given Statement to this Dataset. If the Statement already exists nothing happens. Note:
   * Potentially could trigger a ValidationError
   */
  override suspend fun addStatement(statement: Statement) {
    val entityID = lookupOrCreateIdentifier(statement.entity)
    val attributeID = lookupOrCreateIdentifier(statement.attribute)
    val encodedValue = encodeValue(statement.value)
    if (!statementExists(entityID, attributeID, encodedValue)) {
      storeStatement(entityID, attributeID, encodedValue)
    }
  }

  /**
   * Removes a given Statement from this Dataset. If the Statement doesn't exist nothing happens and
   * returns Ok(false). This function returns Ok(true) only if the given Statement was found and
   * removed. Note: Potentially could trigger a ValidationError.
   */
  override suspend fun removeStatement(statement: Statement) {
    val entityID = lookupIdentifier(statement.entity)
    val attributeID = lookupIdentifier(statement.attribute)
    val encodedValue = lookupValue(statement.value)
    if (entityID != null &&
        attributeID != null &&
        encodedValue != null &&
        statementExists(entityID, attributeID, encodedValue)) {
      removeStatement(statement, entityID, attributeID, encodedValue)
    }
  }

  /**
   * This method checks if a Value exists in a Dataset. If the Value is an Identifier it checks
   * IdentifierToIdStore. If the Value is a String it checks StringToIdStore. If the Value is a
   * Bytes it check IntegerToIdStore. If the Value is an Integer it returns the encoded value.
   */
  private fun lookupValue(value: Value): ByteIterable? =
      when (value) {
        is Identifier ->
            when (val id = lookupIdentifier(value)) {
              null -> null
              else ->
                  CompoundByteIterable(
                      arrayOf(ByteBinding.byteToEntry(LigatureValueType.Identifier.id), id))
            }
        is StringLiteral ->
            when (val stringId = lookupStringLiteral(value)) {
              null -> null
              else ->
                  CompoundByteIterable(
                      arrayOf(ByteBinding.byteToEntry(LigatureValueType.String.id), stringId))
            }
        is IntegerLiteral -> LongBinding.longToEntry(value.value) // TODO needs value type
        is BytesLiteral -> TODO()
      }

  private fun removeStatement(
      statement: Statement,
      entityID: ByteIterable,
      attributeID: ByteIterable,
      encodedValue: ByteIterable
  ) {
    // remove Statement from all six of the index Stores
    // check if any of the Identifiers contained in the Statement are used anywhere else
    // if the value contains a String or Bytes remove the references
    val d = datasetID
    val e = entityID
    val a = attributeID
    val v = encodedValue

    // remove eav
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    eavStore.delete(tx, CompoundByteIterable(arrayOf(d, e, a, v)))
    // remove eva
    val evaStore = xodusOperations.openStore(tx, LigatureStore.EVAStore)
    evaStore.delete(tx, CompoundByteIterable(arrayOf(d, e, v, a)))
    // remove aev
    val aevStore = xodusOperations.openStore(tx, LigatureStore.AEVStore)
    aevStore.delete(tx, CompoundByteIterable(arrayOf(d, a, e, v)))
    // remove ave
    val aveStore = xodusOperations.openStore(tx, LigatureStore.AVEStore)
    aveStore.delete(tx, CompoundByteIterable(arrayOf(d, a, v, e)))
    // remove vea
    val veaStore = xodusOperations.openStore(tx, LigatureStore.VEAStore)
    veaStore.delete(tx, CompoundByteIterable(arrayOf(d, v, e, a)))
    // remove vae
    val vaeStore = xodusOperations.openStore(tx, LigatureStore.VAEStore)
    vaeStore.delete(tx, CompoundByteIterable(arrayOf(d, v, a, e)))

    checkAndRemoveIdentifier(statement.entity, entityID)
    checkAndRemoveIdentifier(statement.attribute, attributeID)
    cleanUpValue(statement.value, encodedValue)
  }

  private fun checkAndRemoveIdentifier(identifier: Identifier, id: ByteIterable): Unit {
    // check if an identifier is used in any position in any Statement in the Dataset
    val eavResult = startsWith(LigatureStore.EAVStore, CompoundByteIterable(arrayOf(datasetID, id)))

    if (eavResult) {
      val aevResult =
          startsWith(LigatureStore.AEVStore, CompoundByteIterable(arrayOf(datasetID, id)))

      if (aevResult) {
        val veaResult =
            startsWith(
                LigatureStore.VEAStore,
                CompoundByteIterable(
                    arrayOf(
                        datasetID, ByteBinding.byteToEntry(LigatureValueType.Identifier.id), id)))

        if (veaResult) {
          val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
          val identifierToIdStore = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
          idToIdentifierStore.delete(tx, CompoundByteIterable(arrayOf(datasetID, id)))
          identifierToIdStore.delete(
              tx,
              CompoundByteIterable(
                  arrayOf(datasetID, StringBinding.stringToEntry(identifier.name))))
        }
      }
    }
  }

  private fun startsWith(ligatureStore: LigatureStore, prefix: ByteIterable): Boolean {
    val store = xodusOperations.openStore(tx, ligatureStore)
    val cursor = store.openCursor(tx)
    cursor.getSearchKeyRange(prefix)
    val key = cursor.key
    cursor.close()
    return key.subIterable(0, prefix.length) == prefix
  }

  private fun cleanUpValue(value: Value, valueEncoded: ByteIterable) {
    // if value is an Identifier call checkAndRemoveIdentifier
    // if value is a String remove it from stringToId and idToString stores
    // if value is a Bytes remove it from bytesToId and idToBytes stores
    // if value is an Integer do nothing
    when (value) {
      is Identifier -> checkAndRemoveIdentifier(value, valueEncoded.subIterable(1, 8))
      is StringLiteral -> {
        val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
        val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
        stringToIdStore.delete(tx, StringBinding.stringToEntry(value.value))
        idToStringStore.delete(tx, valueEncoded.subIterable(1, 8))
      }
      is IntegerLiteral -> {}
      is BytesLiteral -> TODO()
    }
  }
}
