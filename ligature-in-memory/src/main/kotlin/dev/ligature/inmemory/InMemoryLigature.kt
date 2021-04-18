/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.Result.Companion.success

data class DatasetStore(val statements: Set<Statement>)

/** A trait that all Ligature implementations implement. */
class InMemoryLigature(): Ligature {
    private var store = TreeMap<Dataset, DatasetStore>()
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    /** Returns all Datasets in a Ligature instance. */
    override suspend fun allDatasets(): Flow<Result<Dataset>> {
        val l = lock.readLock()
        return try {
            l.lock()
            store.keys.map { success(it) }.asFlow()
        } finally  {
            l.unlock()
        }
    }

    /** Check if a given Dataset exists. */
    override suspend fun datasetExists(dataset: Dataset): Result<Boolean> {
        val l = lock.readLock()
        return try {
            l.lock()
            success(store.contains(dataset))
        } finally {
            l.unlock()
        }
    }

    /** Returns all Datasets in a Ligature instance that start with the given prefix. */
    override suspend fun matchDatasetsPrefix(prefix: String): Flow<Result<Dataset>> {
        val l = lock.readLock()
        return try {
            l.lock()
            store.filter { (key, _) -> key.name.startsWith(prefix) }.keys.map { success(it) }.asFlow()
        } finally {
            l.unlock()
        }
    }

    /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
    override suspend fun matchDatasetsRange(
            start: String,
            end: String,
    ): Flow<Result<Dataset>> {
        val l = lock.readLock()
        return try {
            l.lock()
            store.filter { (key, _) -> key.name >= start && key.name < end }.keys.map { success(it) }.asFlow()
        } finally {
            l.unlock()
        }
    }

    /** Creates a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
    override suspend fun createDataset(dataset: Dataset): Result<Unit> {
        val l = lock.writeLock()
        return try {
            l.lock()
            if (store.contains(dataset)) {
                success(Unit)
            } else {
                store[dataset] = DatasetStore(setOf())
//                val newStore = store.put(dataset, DatasetStore(0L, setOf()))
//                store = newStore
                success(Unit)
            }
        } finally {
            l.unlock()
        }
    }

    /** Deletes a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
    override suspend fun deleteDataset(dataset: Dataset): Result<Unit> {
        val l = lock.writeLock()
        return try {
            l.lock()
            if (store.contains(dataset)) {
//                val newStore = store - dataset
//                store = newStore
                store.remove(dataset)
                success(Unit)
            } else {
                success(Unit)
            }
        } finally {
            l.unlock()
        }
    }

    /** Initializes a QueryTx
     * TODO should probably return its own error type CouldNotInitializeQueryTx */
    override suspend fun <T> query(dataset: Dataset, fn: suspend (QueryTx) -> T): T {
        val l = lock.readLock()
        return try {
            l.lock()
            val datasetStore: DatasetStore? = store[dataset]
            if (datasetStore != null) {
                val queryTx = InMemoryQueryTx(datasetStore)
                fn(queryTx)
            } else {
                TODO()
            }
        } finally {
            l.unlock()
        }
    }

    /** Initializes a WriteTx
     * TODO should probably return its own error type CouldNotInitializeWriteTx */
    override suspend fun <T> write(dataset: Dataset, fn: suspend (WriteTx) -> T): T {
        val l = lock.writeLock()
        return try {
            l.lock()
            val datasetStore: DatasetStore? = store[dataset]?.copy()
            if (datasetStore != null) {
                val writeTx = InMemoryWriteTx(datasetStore)
                val res = fn(writeTx)
                if (!writeTx.isCanceled()) {
                    store[dataset] = writeTx.newDatasetStore()
                    res
                } else {
                    res
                }
            } else {
                TODO()
            }
        } finally {
            l.unlock()
        }
    }
}
