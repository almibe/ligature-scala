/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.Dataset
import dev.ligature.Identifier
import dev.ligature.Ligature
import dev.ligature.LigatureError
import dev.ligature.QueryTx
import dev.ligature.Statement
import dev.ligature.Value
import dev.ligature.WriteTx

import jetbrains.exodus.ByteIterable

import java.io.File
import jetbrains.exodus.env.EnvironmentConfig
import jetbrains.exodus.env.Environments
import jetbrains.exodus.env.ReadonlyTransaction
import jetbrains.exodus.env.Store
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.env.Transaction
import jetbrains.exodus.env.TransactionalComputable
import jetbrains.exodus.env.TransactionalExecutable

import jetbrains.exodus.bindings.LongBinding
import jetbrains.exodus.bindings.StringBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface XodusOperations {
  fun openStore(tx: Transaction, store: LigatureStore): Store
  fun nextID(tx: Transaction): ByteIterable
}

enum class LigatureStore(val storeName: String) {
  CountersStore("Counters"),
  DatasetToIdStore("DatasetToID"),
  IdToDatasetStore("IDToDataset"),
  EAVStore("EAV"),
  EVAStore("EVA"),
  AEVStore("AEV"),
  AVEStore("AVE"),
  VEAStore("VEA"),
  VAEStore("VAE"),
  IdentifierToIdStore("IdentifierToID"),
  IdToIdentifierStore("IDToIdentifier"),
  StringToIdStore("StringToID"),
  IdToStringStore("IDToString"),
  BytesToIdStore("BytesToID"),
  IdToBytesStore("IDToBytes")
}

enum class LigatureValueType(val id: Byte) {
  Identifier(0),
  Integer(1),
  String(2),
  Bytes(3);

  companion object {
    fun getValueType(id: Byte): LigatureValueType = when (id) {
      LigatureValueType.Identifier.id -> LigatureValueType.Identifier
      LigatureValueType.Integer.id -> LigatureValueType.Integer
      LigatureValueType.String.id -> LigatureValueType.String
      LigatureValueType.Bytes.id -> LigatureValueType.Bytes
      else -> TODO("handle error case, invalid type")
    }
  }
}

class XodusLigature(private val dbDirectory: File): Ligature, XodusOperations {
  private val environment = Environments.newInstance(dbDirectory, EnvironmentConfig())

  init {
    setupStores()
  }

  /** This method is ran once at the start to make sure all Stores exist.
    * This is done so that Stores can be opened in readonly mode.
    *  It also checks and initializes Counters
    * TODO: This method could probably check if the Environment exists and check the status of the Environment first.
    */
  private fun setupStores(): Unit {
    // create all Stores
    val createStoresTC: TransactionalComputable<Unit> = TransactionalComputable<Unit> { tx ->
      LigatureStore.values().forEach { openStore(tx, it) }
    }
    environment.computeInTransaction(createStoresTC)
    // set up counters -- for now it's just a single counter
    val checkCountersTC: TransactionalComputable<Unit> = TransactionalComputable<Unit> { tx ->
      val counterStore = openStore(tx, LigatureStore.CountersStore)
      val currentCount = counterStore.get(tx, StringBinding.stringToEntry("counter"))
      if (currentCount == null) {
        counterStore.put(tx, StringBinding.stringToEntry("counter"), LongBinding.longToEntry(0))
      }
    }
    environment.computeInTransaction(checkCountersTC)
  }

  /** Used to uniformly open Stores.
    * This is mainly so that StoreConfigs can be consistent.
    * Right now all stores use the same config but that is likely to change.
    */
  override fun openStore(tx: Transaction, store: LigatureStore): Store =
    environment.openStore(store.storeName, StoreConfig.WITHOUT_DUPLICATES, tx)

  /** Computes the next ID.
    * Right now IDs are shared between all Stores but that might change.
    * Also, right now IDs are positive Long values and that might also change.
    */
  override fun nextID(tx: Transaction): ByteIterable {
    val counterStore = openStore(tx, LigatureStore.CountersStore)
    val nextCount =
      LongBinding.entryToLong(counterStore.get(tx, StringBinding.stringToEntry("counter"))!!) + 1L
    counterStore.put(tx, StringBinding.stringToEntry("counter"), LongBinding.longToEntry(nextCount))
    return LongBinding.longToEntry(nextCount)
  }

  /** Returns all Datasets in a Ligature instance. */
  override fun allDatasets(): Flow<Dataset> = flow {
    val tc = TransactionalComputable<List<String>> { tx ->
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val datasetsCursor = datasetToIdStore.openCursor(tx)
      val datasets: MutableList<String> = mutableListOf()
      while (datasetsCursor.next)
        datasets.add(StringBinding.entryToString(datasetsCursor.key))
      datasetsCursor.close() // TODO should use bracket
      datasets
    }
    environment.computeInReadonlyTransaction(tc).map { Dataset.create(it) }.forEach {
      when (it) {
        is Right -> emit(it.value)
        is Left  -> TODO()
      }
    }
  }

  private fun fetchDatasetID(dataset: Dataset): ByteIterable? {
    val tc = TransactionalComputable<ByteIterable?> { tx ->
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      datasetToIdStore.get(tx, StringBinding.stringToEntry(dataset.name))
    }
    return environment.computeInReadonlyTransaction(tc)
  }

  /** Check if a given Dataset exists. */
  override suspend fun datasetExists(dataset: Dataset): Boolean =
    fetchDatasetID(dataset) != null

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override fun matchDatasetsPrefix(prefix: String): Flow<Dataset> = flow {
    val tc = TransactionalComputable<List<Dataset>> { tx ->
      val results = mutableListOf<Dataset>()
      val cursor = openStore(tx, LigatureStore.DatasetToIdStore).openCursor(tx)
      val prefixBytes = StringBinding.stringToEntry(prefix)
      cursor.getSearchKeyRange(prefixBytes)
      var r: ByteIterable? = cursor.key
      var `continue` = true
      while (`continue` && r != null && StringBinding.entryToString(r).startsWith(prefix)) {
        when (val ds = Dataset.create(StringBinding.entryToString(r))) {
          is Right -> {
            results.add(ds.value)
            `continue` = cursor.next
            r = cursor.key
          }
          is Left -> {
            TODO()
          }
        }
      }
      results
    }
    environment.computeInReadonlyTransaction(tc).forEach { emit(it) }
  }

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override fun matchDatasetsRange(start: String, end: String): Flow<Dataset> = flow {
    val tc = TransactionalComputable<List<Dataset>> { tx ->
      val results = mutableListOf<Dataset>()
      val cursor = openStore(tx, LigatureStore.DatasetToIdStore).openCursor(tx)
      val prefixBytes = StringBinding.stringToEntry(start)
      cursor.getSearchKeyRange(prefixBytes)
      var r: ByteIterable? = cursor.key
      var `continue` = true
      while (`continue` && r != null && stringInRange(StringBinding.entryToString(r), start, end)) {
        when (val ds = Dataset.create(StringBinding.entryToString(r))) {
          is Right -> {
            results.add(ds.value)
            `continue` = cursor.next
            r = cursor.key
          }
          is Left -> {
            TODO()
          }
        }
      }
      results
    }
    environment.computeInReadonlyTransaction(tc)
  }

  private fun stringInRange(toTest: String, start: String, end: String): Boolean =
    toTest >= start && toTest < end

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override suspend fun createDataset(dataset: Dataset) {
    val tc = TransactionalExecutable { tx ->
      val nameEntry = StringBinding.stringToEntry(dataset.name)
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val datasetExists = datasetToIdStore.get(tx, nameEntry)
      if (datasetExists == null) {
        val nextId = nextID(tx)
        val idToDatasetStore = openStore(tx, LigatureStore.IdToDatasetStore)
        datasetToIdStore.put(tx, nameEntry, nextId)
        idToDatasetStore.putRight(tx, nextId, nameEntry)
        tx.commit()
      }
    }
    environment.executeInTransaction(tc)
  }

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override suspend fun deleteDataset(dataset: Dataset) {
    val tc = TransactionalExecutable { tx ->
      val nameEntry = StringBinding.stringToEntry(dataset.name)
      val datasetToIdStore = openStore(tx, LigatureStore.DatasetToIdStore)
      val datasetId = datasetToIdStore.get(tx, nameEntry)
      if (datasetId != null) {
        val idToDatasetStore = openStore(tx, LigatureStore.IdToDatasetStore)
        datasetToIdStore.delete(tx, nameEntry)
        idToDatasetStore.delete(tx, datasetId)
        deleteAllStatements(tx, datasetId)
        tx.commit()
      }
    }
    environment.executeInTransaction(tc)
  }

  /** This method iterates through all the index stores and removes all Statements
    * in a given Dataset.
    * TODO this method also needs to clean up all Identifiers, Strings, and Bytes
    */
  private fun deleteAllStatements(tx: Transaction, datasetId: ByteIterable) {
    val stores = listOf(
      LigatureStore.EAVStore,
      LigatureStore.EVAStore,
      LigatureStore.AEVStore,
      LigatureStore.AVEStore,
      LigatureStore.VEAStore,
      LigatureStore.VAEStore,
      LigatureStore.IdentifierToIdStore,
      LigatureStore.IdToIdentifierStore,
      LigatureStore.StringToIdStore,
      LigatureStore.IdToStringStore,
      LigatureStore.BytesToIdStore,
      LigatureStore.IdToBytesStore
    )
    stores.forEach { store ->
      val cursor = openStore(tx, store).openCursor(tx)
      var `continue` = cursor.getSearchKeyRange(datasetId) != null
      while (`continue`) {
        val key = cursor.key
        `continue` = if (key.subIterable(0, 8) == datasetId) {
          cursor.deleteCurrent()
          cursor.next
        } else {
          false
        }
      }
    }
  }

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  //TODO probably use arrow's Resource here?
  override suspend fun <T>query(dataset: Dataset, fn: suspend (QueryTx) -> T): T {
    val tx = environment.beginReadonlyTransaction()
    val res = when (val datasetId = fetchDatasetID(dataset)) {
      null -> TODO()
      else -> {
        val queryTx = XodusQueryTx(tx, this, datasetId)
        fn(queryTx)
      }
    }
    if (!tx.isFinished) {
      tx.abort()
    }
    return res
  }

  /** Initializes a WriteTx TODO should probably return its own error type
    * CouldNotInitializeWriteTx
    */
  //TODO probably use arrow's Resource here?
  override suspend fun <T>write(dataset: Dataset, fn: suspend (WriteTx) -> T): T {
    val tx = environment.beginTransaction()
    val res = when(val datasetId = fetchDatasetID(dataset)) {
      null -> TODO()
      else -> {
        val writeTx = XodusWriteTx(tx, this, datasetId)
        fn(writeTx)
      }
    }
    if (!tx.isFinished) {
      tx.commit()
    }
    return res
  }

  override suspend fun close(): Unit =
    environment.close()
}
