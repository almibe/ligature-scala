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

import arrow.core.Option
import arrow.core.Some
import arrow.core.None
import arrow.core.none
import kotlinx.coroutines.flow.Flow

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusQueryTx(
    private val tx: Transaction,
    private val xodusOperations: XodusOperations,
    private val datasetID: ByteIterable
): QueryTx {

  private fun lookupIdentifier(internalIdentifier: ByteIterable): Identifier {
    val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
    val result =
      idToIdentifierStore.get(tx, CompoundByteIterable(arrayOf(datasetID, internalIdentifier)))
    return if (result != null) {
      Identifier(StringBinding.entryToString(result))
    } else {
      TODO("Not impl'd")
    }
  }

  private fun lookupIdentifier(identifier: Option<Identifier>): Option<ByteIterable> =
    when(identifier) {
      is None -> none()
      is Some -> {
        val store = xodusOperations.openStore(tx, LigatureStore.IdentifierToIdStore)
        val result = store.get(
          tx,
          CompoundByteIterable(arrayOf(datasetID, StringBinding.stringToEntry(identifier.value.name)))
        )
        if (result == null) none() else Some(result)
      }
    }

  private fun lookupStringLiteral(stringLiteral: StringLiteral): Option<ByteIterable> {
    val store = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    val result = store.get(
      tx,
      CompoundByteIterable(arrayOf(datasetID, StringBinding.stringToEntry(stringLiteral.value)))
    )
    return if (result == null) none() else Some(result)
  }

  private fun lookupValue(value: Option<Value>): Option<ByteIterable> = TODO()
//    value match {
//      case None => None
//      case Some(value) =>
//        value match {
//          case identifier: Identifier =>
//            lookupIdentifier(Some(identifier)).map(id =>
//              CompoundByteIterable(
//                arrayOf(ByteBinding.byteToEntry(LigatureValueType.Identifier.id), id)
//              )
//            )
//          case stringLiteral: StringLiteral =>
//            lookupStringLiteral(stringLiteral).map(id =>
//              CompoundByteIterable(arrayOf(ByteBinding.byteToEntry(LigatureValueType.String.id), id))
//            )
//          case integerLiteral: IntegerLiteral =>
//            Some(
//              CompoundByteIterable(
//                arrayOf(
//                  ByteBinding.byteToEntry(LigatureValueType.Integer.id),
//                  LongBinding.longToEntry(integerLiteral.value)
//                )
//              )
//            )
//        }
//    }

  private fun lookupStringLiteral(internalIdentifier: ByteIterable): StringLiteral {
    val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
    val result = idToStringStore.get(tx, CompoundByteIterable(arrayOf(datasetID, internalIdentifier)))
    return if (result != null) {
      StringLiteral(StringBinding.entryToString(result))
    } else {
      TODO("case not handled")
    }
  }

  private fun constructValue(valueTypeId: Byte, valueContent: ByteIterable): Value = TODO()
//    LigatureValueType.getValueType(valueTypeId) match {
//      case LigatureValueType.Identifier => lookupIdentifier(valueContent)
//      case LigatureValueType.Integer    => IntegerLiteral(LongBinding.entryToLong(valueContent))
//      case LigatureValueType.String     => lookupStringLiteral(valueContent)
//      case LigatureValueType.Bytes      => ???
//    }

  enum class ReadStatementOffsets(val entityOffset: Int, val attributeOffset: Int, val valueOffset: Int) {
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

    return Statement(entity, attribute, value)
  }

  /** Returns all Statements in this Dataset. */
  override fun allStatements(): Flow<Statement> = TODO() //Stream.emits {
//    val output: ArrayBuffer[Statement] = ArrayBuffer()
//    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
//    val eavCursor = eavStore.openCursor(tx)
//    var continue = eavCursor.getSearchKeyRange(datasetID) != null
//    while (continue) {
//      val statement = eavCursor.getKey
//      if (datasetID == statement.subIterable(0, datasetID.getLength)) {
//        output.append(readStatement(statement, ReadStatementOffsets.EAVRange))
//        continue = eavCursor.getNext
//      } else {
//        continue = false
//      }
//    }
//    output
//  }

  /** Returns all Statements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  override fun matchStatements(
      entity: Identifier?,
      attribute: Identifier?,
      value: Value?
  ): Flow<Statement> = TODO() //{
//    val luEntity = lookupIdentifier(entity)
//    val luAttribute = lookupIdentifier(attribute)
//    val luValue = lookupValue(value)
//
//    if (entity.isEmpty && attribute.isEmpty && value.isEmpty) {
//      allStatements()
//    } else if (
//      entity.isDefined && luEntity.isEmpty || attribute.isDefined && luAttribute.isEmpty || value.isDefined && luValue.isEmpty
//    ) {
//      Stream.empty
//    } else {
//      if (entity.isDefined) {
//        if (attribute.isDefined) {
//          matchStatementsEAV(luEntity.get, luAttribute.get, luValue)
//        } else {
//          matchStatementsEVA(luEntity.get, luValue) // we know attribute isn't set
//        }
//      } else if (attribute.isDefined) {
//        matchStatementsAVE(luAttribute.get, luValue) // we know entity isn't set
//      } else {
//        matchStatementsVEA(luValue.get) // we know entity and attribute aren' set
//      }
//    }
//  }

//  private def matchStatementsEAV(
//      entityId: ByteIterable,
//      attributeId: ByteIterable,
//      valueId: Option[ByteIterable]
//  ): Stream[IO, Statement] = Stream.emits {
//    val store = xodusOperations.openStore(tx, LigatureStore.EAVStore)
//    val prefix = valueId match {
//      case None      => CompoundByteIterable(arrayOf(datasetID, entityId, attributeId))
//      case Some(vid) => CompoundByteIterable(arrayOf(datasetID, entityId, attributeId, vid))
//    }
//    val cursor = store.openCursor(tx)
//    cursor.getSearchKeyRange(prefix)
//    var continue = true
//    val results: ArrayBuffer[Statement] = ArrayBuffer()
//    while (continue) {
//      val key = cursor.getKey
//      if (key.subIterable(0, prefix.getLength) == prefix) {
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
//      valueId: Option[ByteIterable]
//  ): Stream[IO, Statement] = Stream.emits {
//    val store = xodusOperations.openStore(tx, LigatureStore.EVAStore)
//    val prefix = valueId match {
//      case None      => CompoundByteIterable(arrayOf(datasetID, entityId))
//      case Some(vid) => CompoundByteIterable(arrayOf(datasetID, entityId, vid))
//    }
//    val cursor = store.openCursor(tx)
//    cursor.getSearchKeyRange(prefix)
//    var continue = true
//    val results: ArrayBuffer[Statement] = ArrayBuffer()
//    while (continue) {
//      val key = cursor.getKey
//      if (key.subIterable(0, prefix.getLength) == prefix) {
//        val statement = readStatement(key, ReadStatementOffsets.EVARange)
//        results.append(statement)
//        continue = cursor.getNext
//      } else {
//        continue = false
//      }
//    }
//    results
//  }
//
//  private def matchStatementsAVE(
//      attributeId: ByteIterable,
//      valueId: Option[ByteIterable]
//  ): Stream[IO, Statement] = Stream.emits {
//    val store = xodusOperations.openStore(tx, LigatureStore.AVEStore)
//    val prefix = valueId match {
//      case None      => CompoundByteIterable(arrayOf(datasetID, attributeId))
//      case Some(vid) => CompoundByteIterable(arrayOf(datasetID, attributeId, vid))
//    }
//    val cursor = store.openCursor(tx)
//    cursor.getSearchKeyRange(prefix)
//    var continue = true
//    val results: ArrayBuffer[Statement] = ArrayBuffer()
//    while (continue) {
//      val key = cursor.getKey
//      if (key.subIterable(0, prefix.getLength) == prefix) {
//        val statement = readStatement(key, ReadStatementOffsets.AVERange)
//        results.append(statement)
//        continue = cursor.getNext
//      } else {
//        continue = false
//      }
//    }
//    results
//  }
//
//  private def matchStatementsVEA(valueId: ByteIterable): Stream[IO, Statement] = Stream.emits {
//    val store = xodusOperations.openStore(tx, LigatureStore.VEAStore)
//    val prefix = CompoundByteIterable(arrayOf(datasetID, valueId))
//    val cursor = store.openCursor(tx)
//    cursor.getSearchKeyRange(prefix)
//    var continue = true
//    val results: ArrayBuffer[Statement] = ArrayBuffer()
//    while (continue) {
//      val key = cursor.getKey
//      if (key.subIterable(0, prefix.getLength) == prefix) {
//        val statement = readStatement(key, ReadStatementOffsets.VEARange)
//        results.append(statement)
//        continue = cursor.getNext
//      } else {
//        continue = false
//      }
//    }
//    results
//  }

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
//    val start = CompoundByteIterable(arrayOf(datasetID, entityId, attributeId, encodedRange.start))
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
