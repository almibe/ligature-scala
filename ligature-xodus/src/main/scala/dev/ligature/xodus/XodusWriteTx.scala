/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

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
) {
  private def lookupLabel(label: Label): Option[ByteIterable] =
    val labelToIdStore = xodusOperations.openStore(tx, LigatureStore.LabelToIdStore)
    val encodedName = CompoundByteIterable(
      Array(datasetID, StringBinding.stringToEntry(label.name))
    )
    val result = labelToIdStore.get(tx, encodedName)
    Option(result)

  private def lookupOrCreateLabel(label: Label): ByteIterable =
    val encodedName = StringBinding.stringToEntry(label.name)
    lookupLabel(label) match {
      case Some(res) => res
      case None =>
        val labelToIdStore = xodusOperations.openStore(tx, LigatureStore.LabelToIdStore)
        val internalId = xodusOperations.nextID(tx)
        val internalIdWithDataset = CompoundByteIterable(Array(datasetID, internalId))
        val encodedNameWithDataset = CompoundByteIterable(Array(datasetID, encodedName))
        val idToLabelStore = xodusOperations.openStore(tx, LigatureStore.IdToLabelStore)
        labelToIdStore.put(tx, encodedNameWithDataset, internalId)
        idToLabelStore.put(tx, internalIdWithDataset, encodedName)
        internalId
    }

  private def lookupStringLiteral(literal: LigatureLiteral.StringLiteral): Option[ByteIterable] =
    val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    val encodedString = CompoundByteIterable(
      Array(datasetID, StringBinding.stringToEntry(literal.value))
    )
    val result = stringToIdStore.get(tx, encodedString)
    Option(result)

  // TODO this function might be able to be merged with lookupOrCreateLabel
  private def lookupOrCreateStringLiteral(literal: LigatureLiteral.StringLiteral): ByteIterable =
    val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
    val encodedString = StringBinding.stringToEntry(literal.value)
    val encodedStringWithDataset = CompoundByteIterable(Array(datasetID, encodedString))
    val result = stringToIdStore.get(tx, encodedStringWithDataset)
    if (result != null) {
      result
    } else {
      val internalId = xodusOperations.nextID(tx)
      val internalIdWithDataset = CompoundByteIterable(Array(datasetID, internalId))
      val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
      stringToIdStore.put(tx, encodedStringWithDataset, internalId)
      idToStringStore.put(tx, internalIdWithDataset, encodedString)
      internalId
    }

  /** Takes a value and returns a ByteIterable that can be stored.
    * The ByteIterable is made up of a Byte that represents the Type and
    * a 8 Byte value that represents the value that can be stored.
    * Label -> Label's Internal ID
    * String -> String Internal ID
    * Long -> Value
    * Bytes -> Bytes Internal ID
    */
  private def encodeValue(value: Value): ByteIterable =
    value match {
      case label: Label =>
        val temp = CompoundByteIterable(
          Array(
            ByteBinding.byteToEntry(LigatureValueType.Label.id),
            lookupOrCreateLabel(label)
          )
        )
        temp
      case stringLiteral: LigatureLiteral.StringLiteral =>
        CompoundByteIterable(
          Array(
            ByteBinding.byteToEntry(LigatureValueType.String.id),
            lookupOrCreateStringLiteral(stringLiteral)
          )
        )
      case integerLiteral: LigatureLiteral.IntegerLiteral =>
        CompoundByteIterable(
          Array(
            ByteBinding.byteToEntry(LigatureValueType.Integer.id),
            LongBinding.longToEntry(integerLiteral.value)
          )
        )
      // TODO Bytes
    }

  private def edgeExists(
      source: ByteIterable,
      label: ByteIterable,
      value: ByteIterable
  ): Boolean = {
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    val encodedEdge = CompoundByteIterable(Array(datasetID, source, label, value))
    eavStore.get(tx, encodedEdge) != null
  }

  private def storeEdge(
      source: ByteIterable,
      label: ByteIterable,
      value: ByteIterable
  ): Unit = {
    val d = datasetID
    val e = source
    val a = label
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
  def newLabel(prefix: String): IO[Label] = IO {
    var newLabel = Label.fromString(s"$prefix${genId()}").getOrElse(???)
    var encodedLabel = StringBinding.stringToEntry(newLabel.name)
    var exists = true
    val store = xodusOperations.openStore(tx, LigatureStore.LabelToIdStore)
    while (exists) {
      exists = store.get(tx, encodedLabel) != null
      if (exists) {
        newLabel = Label.fromString(s"$prefix${genId()}").getOrElse(???)
        encodedLabel = StringBinding.stringToEntry(newLabel.name)
      }
    }
    newLabel
  }

  /** Adds a given Edge to this Dataset. If the Edge already exists
    * nothing happens (TODO maybe add it with a new context?). Note: Potentially
    * could trigger a ValidationError
    */
  def addEdge(edge: Edge): IO[Unit] = IO {
    val sourceID = lookupOrCreateLabel(edge.source)
    val labelID = lookupOrCreateLabel(edge.label)
    val encodedValue = encodeValue(edge.target)
    if (edgeExists(sourceID, labelID, encodedValue)) {
      ()
    } else {
      storeEdge(sourceID, labelID, encodedValue)
    }
  }

  /** Removes a given Edge from this Dataset. If the Edge doesn't
    * exist nothing happens and returns Ok(false). This function returns
    * Ok(true) only if the given Edge was found and removed. Note:
    * Potentially could trigger a ValidationError.
    */
  def removeEdge(edge: Edge): IO[Unit] = IO {
    val sourceID = lookupLabel(edge.source)
    val labelID = lookupLabel(edge.label)
    val encodedValue = lookupValue(edge.target)
    if (
      sourceID.isDefined && labelID.isDefined && encodedValue.isDefined && edgeExists(
        sourceID.get,
        labelID.get,
        encodedValue.get
      )
    ) {
      removeEdge(edge, sourceID.get, labelID.get, encodedValue.get)
    } else {
      ()
    }
  }

  /** This method checks if a Value exists in a Dataset.
    * If the Value is an Label it checks LabelToIdStore.
    * If the Value is a String it checks StringToIdStore.
    * If the Value is a Bytes it check IntegerToIdStore.
    * If the Value is an Integer it returns the encoded value.
    */
  private def lookupValue(value: Value): Option[ByteIterable] =
    value match {
      case label: Label =>
        lookupLabel(label) match {
          case None => None
          case Some(id) =>
            Some(
              CompoundByteIterable(
                Array(ByteBinding.byteToEntry(LigatureValueType.Label.id), id)
              )
            )
        }
      case stringLiteral: LigatureLiteral.StringLiteral =>
        lookupStringLiteral(stringLiteral) match {
          case None => None
          case Some(stringId) =>
            Some(
              CompoundByteIterable(
                Array(ByteBinding.byteToEntry(LigatureValueType.String.id), stringId)
              )
            )
        }
      case LigatureLiteral.IntegerLiteral(value) =>
        Some(LongBinding.longToEntry(value)) // TODO needs value type
    }

  private def removeEdge(
      edge: Edge,
      sourceID: ByteIterable,
      labelID: ByteIterable,
      encodedValue: ByteIterable
  ): Unit = {
    // remove Edge from all six of the index Stores
    // check if any of the Labels contained in the Edge are used anywhere else
    // if the value contains a String or Bytes remove the references
    val d = datasetID
    val e = sourceID
    val a = labelID
    val v = encodedValue

    // remove eav
    val eavStore = xodusOperations.openStore(tx, LigatureStore.EAVStore)
    eavStore.delete(tx, CompoundByteIterable(Array(d, e, a, v)))
    // remove eva
    val evaStore = xodusOperations.openStore(tx, LigatureStore.EVAStore)
    evaStore.delete(tx, CompoundByteIterable(Array(d, e, v, a)))
    // remove aev
    val aevStore = xodusOperations.openStore(tx, LigatureStore.AEVStore)
    aevStore.delete(tx, CompoundByteIterable(Array(d, a, e, v)))
    // remove ave
    val aveStore = xodusOperations.openStore(tx, LigatureStore.AVEStore)
    aveStore.delete(tx, CompoundByteIterable(Array(d, a, v, e)))
    // remove vea
    val veaStore = xodusOperations.openStore(tx, LigatureStore.VEAStore)
    veaStore.delete(tx, CompoundByteIterable(Array(d, v, e, a)))
    // remove vae
    val vaeStore = xodusOperations.openStore(tx, LigatureStore.VAEStore)
    vaeStore.delete(tx, CompoundByteIterable(Array(d, v, a, e)))

    checkAndRemoveLabel(edge.source, sourceID)
    checkAndRemoveLabel(edge.label, labelID)
    cleanUpValue(edge.target, encodedValue)
  }

  private def checkAndRemoveLabel(label: Label, id: ByteIterable): Unit =
    // check if an label is used in any position in any Edge in the Dataset
    val eavResult = startsWith(LigatureStore.EAVStore, CompoundByteIterable(Array(datasetID, id)))

    if (eavResult) {
      val aevResult = startsWith(LigatureStore.AEVStore, CompoundByteIterable(Array(datasetID, id)))

      if (aevResult) {
        val veaResult = startsWith(
          LigatureStore.VEAStore,
          CompoundByteIterable(
            Array(datasetID, ByteBinding.byteToEntry(LigatureValueType.Label.id), id)
          )
        )

        if (veaResult) {
          val idToLabelStore = xodusOperations.openStore(tx, LigatureStore.IdToLabelStore)
          val labelToIdStore = xodusOperations.openStore(tx, LigatureStore.LabelToIdStore)
          idToLabelStore.delete(tx, CompoundByteIterable(Array(datasetID, id)))
          labelToIdStore.delete(
            tx,
            CompoundByteIterable(Array(datasetID, StringBinding.stringToEntry(label.name)))
          )
        }
      }
    }

  private def startsWith(ligatureStore: LigatureStore, prefix: ByteIterable): Boolean = {
    val store = xodusOperations.openStore(tx, ligatureStore)
    val cursor = store.openCursor(tx)
    cursor.getSearchKeyRange(prefix)
    val key = cursor.getKey()
    cursor.close()
    key.subIterable(0, prefix.getLength) == prefix
  }

  private def cleanUpValue(value: Value, valueEncoded: ByteIterable): Unit =
    // if value is an Label call checkAndRemoveLabel
    // if value is a String remove it from stringToId and idToString stores
    // if value is a Bytes remove it from bytesToId and idToBytes stores
    // if value is an Integer do nothing
    value match {
      case label: Label =>
        checkAndRemoveLabel(label, valueEncoded.subIterable(1, 8))
      case stringLiteral: LigatureLiteral.StringLiteral =>
        val stringToIdStore = xodusOperations.openStore(tx, LigatureStore.StringToIdStore)
        val idToStringStore = xodusOperations.openStore(tx, LigatureStore.IdToStringStore)
        stringToIdStore.delete(tx, StringBinding.stringToEntry(stringLiteral.value))
        idToStringStore.delete(tx, valueEncoded.subIterable(1, 8))
      case _: LigatureLiteral.IntegerLiteral => ()
      // TODO support bytes
    }
}
