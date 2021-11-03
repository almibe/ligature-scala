/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Dataset, Identifier, Ligature, LigatureError,
    QueryTx, Statement, Value, WriteTx}
import cats.effect.{IO}
import fs2.Stream
import scala.collection.immutable.TreeMap

import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}
import dev.ligature.WriteResult

protected case class DatasetStore(counter: Long, statements: Set[Statement])

final class InMemoryLigature extends Ligature {
    private var store = TreeMap[Dataset, DatasetStore]()
    private val lock: ReadWriteLock = new ReentrantReadWriteLock()

    /** Returns all Datasets in a Ligature instance. */
    def allDatasets(): Stream[IO, Dataset] = {
        val l = lock.readLock()
        try {
            l.lock()
            Stream.emits(store.keys.toList)
        } finally  {
            l.unlock()
        }
    }

    /** Check if a given Dataset exists. */
    def datasetExists(dataset: Dataset): IO[Boolean] = IO {
        store.contains(dataset) //todo should lock before reading
    }

    /** Returns all Datasets in a Ligature instance that start with the given prefix. */
    def matchDatasetsPrefix(
                             prefix: String,
                           ): Stream[IO, Dataset] = {
        val l = lock.readLock()
        try {
            l.lock()
            Stream.emits(store.filter(_._1.name.startsWith(prefix)).keys.toList)
        } finally {
            l.unlock()
        }
    }

    /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
    def matchDatasetsRange(
                            start: String,
                            end: String,
                          ): Stream[IO, Dataset] = {
        val l = lock.readLock()
        try {
            l.lock()
            Stream.emits(store.filter { case (k, v) => k.name >= start && k.name < end }.keys.toList)
        } finally {
            l.unlock()
        }
    }

    /** Creates a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
    def createDataset(dataset: Dataset): IO[Unit] = IO {
        if (store.contains(dataset)) { //todo should lock before reading
            Right(())
        } else {
            val l = lock.writeLock()
            try {
                l.lock()
                val newStore = store + (dataset -> DatasetStore(0L, Set()))
                store = newStore
                Right(())
            } finally {
                l.unlock()
            }
        }
    }

    /** Deletes a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
    def deleteDataset(dataset: Dataset): IO[Unit] = {
        val l = lock.writeLock()
        try {
            l.lock()
            if (store.contains(dataset)) {
                val newStore = store - dataset
                store = newStore
                IO(Right(()))
            } else {
                IO(Right(()))
            }
        } finally {
            l.unlock()
        }
    }

    /** Initializes a QueryTx
     * TODO should probably return its own error type CouldNotInitializeQueryTx */
    def query[R](dataset: Dataset, query: (QueryTx) => IO[R]): IO[R] = IO.defer {
        val l = lock.readLock()
        try {
            var res: Option[IO[R]] = None
            l.lock()
            val ds = this.store.get(dataset)
            ds match {
                case Some(ds) => {
                    val tx = InMemoryQueryTx(ds)
                    query(tx)
                }
                case None => {
                    return IO.raiseError(RuntimeException(""))
                }
            }
        } finally {
            l.unlock()
        }
    }

    /** Initializes a WriteTx
     * TODO should probably return its own error type CouldNotInitializeWriteTx */
    def write(dataset: Dataset, write: (WriteTx) => IO[WriteResult]): IO[WriteResult] = { //TODO this doesn't update the store
        val l = lock.writeLock()
        try {
            l.lock()
            val ds = this.store.get(dataset)
            ds match {
                case Some(ds) => {
                    val tx = InMemoryWriteTx(ds)
                    write(tx)
                }
                case None => {
                    return IO.raiseError(RuntimeException(""))
                }
            }
        } finally {
            l.unlock()
        }
    }

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
}
