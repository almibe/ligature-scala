/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.Dataset
import dev.ligature.Ligature
import dev.ligature.QueryTx
import dev.ligature.Statement
import dev.ligature.WriteTx

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class DatasetStore(val counter: Long, val statements: Set<Statement>)

class InMemoryLigature: Ligature {
  private val mutex = Mutex()
  private val store = mutableMapOf<Dataset, DatasetStore>()

  /** Returns all Datasets in a Ligature instance. */
  override fun allDatasets(): Flow<Dataset> = flow {
    mutex.withLock {
      for (dataset in store.keys) {
        emit(dataset)
      }
    }
  }

  /** Check if a given Dataset exists. */
  override suspend fun datasetExists(dataset: Dataset): Boolean = mutex.withLock {
    store.contains(dataset)
  }

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override fun matchDatasetsPrefix(prefix: String): Flow<Dataset> =
    flow {
      mutex.withLock {
        store.keys.filter { it.name.startsWith(prefix) }.forEach { emit(it) }
      }
    }

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override fun matchDatasetsRange(
      start: String,
      end: String
  ): Flow<Dataset> = flow {
    mutex.withLock {
      store.keys.filter { it.name >= start && it.name < end }.forEach { emit(it) }
    }
  }

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override suspend fun createDataset(dataset: Dataset): Unit = mutex.withLock {
    if (!store.contains(dataset)) {
      store[dataset] = DatasetStore(0L, mutableSetOf())
    }
  }

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override suspend fun deleteDataset(dataset: Dataset): Unit = mutex.withLock {
    store.remove(dataset)
  }

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override suspend fun <T>query(dataset: Dataset, fn: suspend (QueryTx) -> T): T =
    mutex.withLock {
      val ds = store[dataset]
      if (ds != null) {
        val tx = InMemoryQueryTx(ds)
        fn(tx)
      } else {
        TODO("handle case of DS not existing")
      }
    }

  /** Initializes a WriteTx TODO should probably return its own error type
    * CouldNotInitializeWriteTx
    */
  override suspend fun <T>write(dataset: Dataset, fn: suspend (WriteTx) -> T): T =
    mutex.withLock {
      val ds = store[dataset]
      if (ds != null) {
        val tx = InMemoryWriteTx(ds)
        try {
          fn(tx)
        } finally {
          tx.close()
        }
      } else {
        TODO("handle case of DS not existing")
      }
    }
//    IO {
//      val ds = this.store.get.get(dataset)
//      ds match {
//        case Some(ds) =>
//          val tx = InMemoryWriteTx(ds)
//          tx
//        case None =>
//          throw RuntimeException(
//            ""
//          ) // WriteResult.WriteError(s"Could not write to ${dataset.name}"))
//      }
//    }.bracket(tx => fn(tx))(tx =>
//      IO {
//        val newStore =
//          this.store.get.updated(dataset, tx.modifiedDatasetStore())
//        this.store.set(newStore)
//        ()
//      }
//    )

  // def write(dataset: Dataset): Resource[IO, WriteTx] = { //TODO acquire write lock
  //     val l = lock.writeLock()

  //     val acquire: IO[InMemoryWriteTx] = IO {
  //         l.lock()
  //         new InMemoryWriteTx(store(dataset))
  //     }

  //     val release: InMemoryWriteTx => IO[Unit] = writeTx => IO {
  //         if (!writeTx.isCanceled()) {
  //             val newStore = this.store.updated(dataset, writeTx.newDatasetStore())
  //             this.store = newStore
  //         }
  //         l.unlock()
  //         ()
  //     }

  //     Resource.make(acquire)(release)
  // }

  /**
   * Note: Currently for the In Memory implementation, close just wipes the
   * store and doesn't actually close anything.
   * This might change.
   */
  override suspend fun close(): Unit =
    mutex.withLock {
      store.clear()
    }
}
