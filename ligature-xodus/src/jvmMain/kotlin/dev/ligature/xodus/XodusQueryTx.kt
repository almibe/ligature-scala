/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.*
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.CompoundByteIterable
import jetbrains.exodus.bindings.ByteBinding
import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
class XodusQueryTx(
    private val tx: Transaction,
    private val xodusOperations: XodusOperations,
    private val datasetID: ByteIterable
) : QueryTx {

  private fun lookupIdentifier(internalIdentifier: ByteIterable): Identifier? {
    val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
    val result =
        idToIdentifierStore.get(tx, CompoundByteIterable(arrayOf(datasetID, internalIdentifier)))
    return if (result != null) {
      Identifier(StringBinding.entryToString(result))
    } else {
      null
    }
  }

  private fun lookupIdentifier(identifier: Identifier?): ByteIterable? =
      when (identifier) {
        null -> null
        else -> {
          val store = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
          val result =
              store.get(
                  tx,
                  CompoundByteIterable(
                      arrayOf(datasetID, StringBinding.stringToEntry(identifier.name))))
          result
        }
      }

  private fun lookupStringLiteral(stringLiteral: StringLiteral): ByteIterable? {
    val store = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    return store.get(
        tx,
        CompoundByteIterable(arrayOf(datasetID, StringBinding.stringToEntry(stringLiteral.value))))
  }

  private fun lookupValue(value: Value?): ByteIterable? =
      when (value) {
        null -> null
        else ->
            when (value) {
              is Identifier ->
                  lookupIdentifier(value)?.let {
                    CompoundByteIterable(
                        arrayOf(ByteBinding.byteToEntry(LigatureValueType.Identifier.id), it))
                  }
              is StringLiteral ->
                  lookupStringLiteral(value)?.let {
                    CompoundByteIterable(
                        arrayOf(ByteBinding.byteToEntry(LigatureValueType.String.id), it))
                  }
              is IntegerLiteral ->
                  CompoundByteIterable(
                      arrayOf(
                          ByteBinding.byteToEntry(LigatureValueType.Integer.id),
                          LongBinding.longToEntry(value.value)))
              is BytesLiteral -> TODO()
            }
      }

  private fun lookupStringLiteral(internalIdentifier: ByteIterable): StringLiteral {
    val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
    val result =
        idToStringStore.get(tx, CompoundByteIterable(arrayOf(datasetID, internalIdentifier)))
    return if (result != null) {
      StringLiteral(StringBinding.entryToString(result))
    } else {
      TODO("case not handled")
    }
  }

  private fun constructValue(valueTypeId: Byte, valueContent: ByteIterable): Value? =
      when (LigatureValueType.getValueType(valueTypeId)) {
        LigatureValueType.Identifier -> lookupIdentifier(valueContent)
        LigatureValueType.Integer -> IntegerLiteral(LongBinding.entryToLong(valueContent))
        LigatureValueType.String -> lookupStringLiteral(valueContent)
        LigatureValueType.Bytes -> TODO()
      }

  enum class ReadStatementOffsets(
      val entityOffset: Int,
      val attributeOffset: Int,
      val valueOffset: Int
  ) {
    EAVRange(8, 16, 24),
    EVARange(8, 25, 16),
    AVERange(25, 8, 16),
    VEARange(17, 25, 8)
  }

  private fun readStatement(bytes: ByteIterable, offsets: ReadStatementOffsets): Statement {
    val entityID = bytes.subIterable(offsets.entityOffset, 8)
    val entity = lookupIdentifier(entityID)

    val attributeID = bytes.subIterable(offsets.attributeOffset, 8)
    val attribute = lookupIdentifier(attributeID)

    val valueTypeId = ByteBinding.entryToByte(bytes.subIterable(offsets.valueOffset, 1))
    val valueContent = bytes.subIterable(offsets.valueOffset + 1, 8)
    val value = constructValue(valueTypeId, valueContent)

    return Statement(entity!!, attribute!!, value!!) // TODO handle error better
  }

  /** Returns all Statements in this Dataset. */
  override fun allStatements(): Flow<Statement> = flow {
    val output = mutableListOf<Statement>()
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    val eavCursor = eavStore.openCursor(tx)
    var `continue` = eavCursor.getSearchKeyRange(datasetID) != null
    while (`continue`) {
      val statement = eavCursor.key
      `continue` =
          if (datasetID == statement.subIterable(0, datasetID.length)) {
            output.add(readStatement(statement, ReadStatementOffsets.EAVRange))
            eavCursor.next
          } else {
            false
          }
    }
    output.forEach { emit(it) }
  }

  /**
   * Returns all Statements that match the given criteria. If a parameter is None then it matches
   * all, so passing all Nones is the same as calling allStatements.
   */
  override fun matchStatements(
      entity: Identifier?,
      attribute: Identifier?,
      value: Value?
  ): Flow<Statement> {
    val luEntity = lookupIdentifier(entity)
    val luAttribute = lookupIdentifier(attribute)
    val luValue = lookupValue(value)

    return if (entity == null && attribute == null && value == null) {
      allStatements()
    } else if (entity != null && luEntity == null ||
        attribute != null && luAttribute == null ||
        value != null && luValue == null) {
      emptyFlow()
    } else {
      if (entity != null && luEntity != null) {
        if (attribute != null && luAttribute != null) {
          matchStatementsEAV(luEntity, luAttribute, luValue)
        } else {
          matchStatementsEVA(luEntity, luValue) // we know attribute isn't set
        }
      } else if (attribute != null && luAttribute != null) {
        matchStatementsAVE(luAttribute, luValue) // we know entity isn't set
      } else if (luValue != null) {
        matchStatementsVEA(luValue) // we know entity and attribute aren't set
      } else {
        emptyFlow()
      }
    }
  }

  private fun matchStatementsEAV(
      entityId: ByteIterable,
      attributeId: ByteIterable,
      valueId: ByteIterable?
  ): Flow<Statement> = flow {
    val store = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    val prefix =
        when (valueId) {
          null -> CompoundByteIterable(arrayOf(datasetID, entityId, attributeId))
          else -> CompoundByteIterable(arrayOf(datasetID, entityId, attributeId, valueId))
        }
    val cursor = store.openCursor(tx)
    cursor.getSearchKeyRange(prefix)
    var `continue` = true
    val results = mutableListOf<Statement>()
    while (`continue`) {
      val key = cursor.key
      `continue` =
          if (key.subIterable(0, prefix.length) == prefix) {
            val statement = readStatement(key, ReadStatementOffsets.EAVRange)
            results.add(statement)
            cursor.next
          } else {
            false
          }
    }
    results.forEach { emit(it) }
  }

  private fun matchStatementsEVA(entityId: ByteIterable, valueId: ByteIterable?): Flow<Statement> =
      flow {
        val store = xodusOperations.openStore(tx, LigatureStore.EVAStore)
        val prefix =
            when (valueId) {
              null -> CompoundByteIterable(arrayOf(datasetID, entityId))
              else -> CompoundByteIterable(arrayOf(datasetID, entityId, valueId))
            }
        val cursor = store.openCursor(tx)
        cursor.getSearchKeyRange(prefix)
        var `continue` = true
        val results = mutableListOf<Statement>()
        while (`continue`) {
          val key = cursor.key
          `continue` =
              if (key.subIterable(0, prefix.length) == prefix) {
                val statement = readStatement(key, ReadStatementOffsets.EVARange)
                results.add(statement)
                cursor.next
              } else {
                false
              }
        }
        results.forEach { emit(it) }
      }

  private fun matchStatementsAVE(
      attributeId: ByteIterable,
      valueId: ByteIterable?
  ): Flow<Statement> = flow {
    val store = xodusOperations.openStore(tx, LigatureStore.AVEStore)
    val prefix =
        when (valueId) {
          null -> CompoundByteIterable(arrayOf(datasetID, attributeId))
          else -> CompoundByteIterable(arrayOf(datasetID, attributeId, valueId))
        }
    val cursor = store.openCursor(tx)
    cursor.getSearchKeyRange(prefix)
    var `continue` = true
    val results = mutableListOf<Statement>()
    while (`continue`) {
      val key = cursor.key
      `continue` =
          if (key.subIterable(0, prefix.length) == prefix) {
            val statement = readStatement(key, ReadStatementOffsets.AVERange)
            results.add(statement)
            cursor.next
          } else {
            false
          }
    }
    results.forEach { emit(it) }
  }

  private fun matchStatementsVEA(valueId: ByteIterable): Flow<Statement> = flow {
    val store = xodusOperations.openStore(tx, LigatureStore.VEAStore)
    val prefix = CompoundByteIterable(arrayOf(datasetID, valueId))
    val cursor = store.openCursor(tx)
    cursor.getSearchKeyRange(prefix)
    var `continue` = true
    val results = mutableListOf<Statement>()
    while (`continue`) {
      val key = cursor.key
      `continue` =
          if (key.subIterable(0, prefix.length) == prefix) {
            val statement = readStatement(key, ReadStatementOffsets.VEARange)
            results.add(statement)
            cursor.next
          } else {
            false
          }
    }
    results.forEach { emit(it) }
  }

  //  /** Returns all Statements that match the given criteria. If a
  //    * parameter is None then it matches all.
  //    */
  //  override def matchStatementsRange(
  //      entity: Option[Identifier],
  //      attribute: Option[Identifier],
  //      range: Range
  //  ): Stream[IO, Statement] = {
  //    val luEntity = lookupIdentifier(entity)
  //    val luAttribute = lookupIdentifier(attribute)
  //
  //    if (entity.isDefined) {
  //      if (attribute.isDefined) {
  //        matchStatementsEAV(luEntity.get, luAttribute.get, range)
  //      } else {
  //        matchStatementsEVA(luEntity.get, range) // we know attribute isn't set
  //      }
  //    } else if (attribute.isDefined) {
  //      matchStatementsAVE(luAttribute.get, range) // we know entity isn't set
  //    } else {
  //      matchStatementsVEA(range) // we know entity and attribute aren' set
  //    }
  //  }
  //
  //  case class EncodedRange(start: ByteIterable, end: ByteIterable)
  //
  //  private def encodeRange(valueRange: Range): EncodedRange =
  //    valueRange match {
  //      case StringLiteralRange(start, end) =>
  //        ???
  //      case IntegerLiteralRange(start, end) =>
  //        ???
  //    }
  //
  //  private def matchStatementsEAV(
  //      entityId: ByteIterable,
  //      attributeId: ByteIterable,
  //      valueRange: Range
  //  ): Stream[IO, Statement] = Stream.emits {
  //    val store = xodusOperations.openStore(tx, LigatureStore.EAVStore)
  //    val encodedRange = encodeRange(valueRange)
  //    val start = CompoundByteIterable(arrayOf(datasetID, entityId, attributeId,
  // encodedRange.start))
  //    val end = CompoundByteIterable(arrayOf(datasetID, entityId, attributeId, encodedRange.end))
  //    val cursor = store.openCursor(tx)
  //    cursor.getSearchKeyRange(start)
  //    var continue = true
  //    val results: ArrayBuffer[Statement] = ArrayBuffer()
  //    while (continue) {
  //      val key = cursor.getKey
  //      if (key.subIterable(0, end.getLength).compareTo(end) < 0) {
  //        val statement = readStatement(key, ReadStatementOffsets.EAVRange)
  //        results.append(statement)
  //        continue = cursor.getNext
  //      } else {
  //        continue = false
  //      }
  //    }
  //    results
  //  }
  //
  //  private def matchStatementsEVA(
  //      entityId: ByteIterable,
  //      valueId: Range
  //  ): Stream[IO, Statement] = Stream.emits {
  //    ???
  ////    val store = xodusOperations.openStore(tx, LigatureStore.EVAStore)
  ////    val prefix = valueId match {
  ////      case None      => CompoundByteIterable(arrayOf(datasetID, entityId))
  ////      case Some(vid) => CompoundByteIterable(arrayOf(datasetID, entityId, vid))
  ////    }
  ////    val cursor = store.openCursor(tx)
  ////    cursor.getSearchKeyRange(prefix)
  ////    var continue = true
  ////    val results: ArrayBuffer[Statement] = ArrayBuffer()
  ////    while (continue) {
  ////      val key = cursor.getKey
  ////      if (key.subIterable(0, prefix.getLength) == prefix) {
  ////        val statement = readStatement(key, ReadStatementOffsets.EVARange)
  ////        results.append(statement)
  ////        continue = cursor.getNext
  ////      } else {
  ////        continue = false
  ////      }
  ////    }
  ////    results
  //  }
  //
  //  private def matchStatementsAVE(
  //      attributeId: ByteIterable,
  //      valueId: Range
  //  ): Stream[IO, Statement] = Stream.emits {
  //    ???
  ////    val store = xodusOperations.openStore(tx, LigatureStore.AVEStore)
  ////    val prefix = valueId match {
  ////      case None      => CompoundByteIterable(arrayOf(datasetID, attributeId))
  ////      case Some(vid) => CompoundByteIterable(arrayOf(datasetID, attributeId, vid))
  ////    }
  ////    val cursor = store.openCursor(tx)
  ////    cursor.getSearchKeyRange(prefix)
  ////    var continue = true
  ////    val results: ArrayBuffer[Statement] = ArrayBuffer()
  ////    while (continue) {
  ////      val key = cursor.getKey
  ////      if (key.subIterable(0, prefix.getLength) == prefix) {
  ////        val statement = readStatement(key, ReadStatementOffsets.AVERange)
  ////        results.append(statement)
  ////        continue = cursor.getNext
  ////      } else {
  ////        continue = false
  ////      }
  ////    }
  ////    results
  //  }
  //
  //  private def matchStatementsVEA(valueId: Range): Stream[IO, Statement] = Stream.emits {
  //    ???
  ////    val store = xodusOperations.openStore(tx, LigatureStore.VEAStore)
  ////    val prefix = CompoundByteIterable(arrayOf(datasetID, valueId))
  ////    val cursor = store.openCursor(tx)
  ////    cursor.getSearchKeyRange(prefix)
  ////    var continue = true
  ////    val results: ArrayBuffer[Statement] = ArrayBuffer()
  ////    while (continue) {
  ////      val key = cursor.getKey
  ////      if (key.subIterable(0, prefix.getLength) == prefix) {
  ////        val statement = readStatement(key, ReadStatementOffsets.VEARange)
  ////        results.append(statement)
  ////        continue = cursor.getNext
  ////      } else {
  ////        continue = false
  ////      }
  ////    }
  ////    results
  //  }
}
