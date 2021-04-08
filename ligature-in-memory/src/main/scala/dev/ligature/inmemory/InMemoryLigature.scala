/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Attribute, Dataset, Entity, Ligature, LigatureError, PersistedStatement, 
    QueryTx, Statement, Value, WriteTx}
import io.smallrye.mutiny.{Multi, Uni}

import scala.collection.immutable.TreeMap
import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

protected case class DatasetStore(counter: Long, statements: Set[PersistedStatement])

final class InMemoryLigature extends Ligature {
    private var store = TreeMap[Dataset, DatasetStore]()
    private val lock: ReadWriteLock = new ReentrantReadWriteLock()

    /** Returns all Datasets in a Ligature instance. */
    def allDatasets(): Multi[Either[LigatureError, Dataset]] = {
        ???
//        val l = lock.readLock()
//        try {
//            l.lock()
//            Stream.emits(store.keys.toList.map(Right(_)))
//        } finally  {
//            l.unlock()
//        }
    }

    /** Check if a given Dataset exists. */
    def datasetExists(dataset: Dataset): Uni[Either[LigatureError, Boolean]] = ???//Uni {
//        Right(store.contains(dataset)) //todo should lock before reading
//    }

    /** Returns all Datasets in a Ligature instance that start with the given prefix. */
    def matchDatasetsPrefix(
                             prefix: String,
                           ): Multi[Either[LigatureError, Dataset]] = {
        ???
//        val l = lock.readLock()
//        try {
//            l.lock()
//            Stream.emits(store.filter(_._1.name.startsWith(prefix)).keys.map(Right(_)).toList)
//        } finally {
//            l.unlock()
//        }
    }

    /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
    def matchDatasetsRange(
                            start: String,
                            end: String,
                          ): Multi[Either[LigatureError, Dataset]] = {
        ???
//        val l = lock.readLock()
//        try {
//            l.lock()
//            Stream.emits(store.filter { case (k, v) => k.name >= start && k.name < end }.keys.map(Right(_)).toList)
//        } finally {
//            l.unlock()
//        }
    }

    /** Creates a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
    def createDataset(dataset: Dataset): Uni[Either[LigatureError, Unit]] = ??? //Uni {
//        if (store.contains(dataset)) { //todo should lock before reading
//            Right(())
//        } else {
//            val l = lock.writeLock()
//            try {
//                l.lock()
//                val newStore = store + (dataset -> DatasetStore(0L, Set()))
//                store = newStore
//                Right(())
//            } finally {
//                l.unlock()
//            }
//        }
//    }

    /** Deletes a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
    def deleteDataset(dataset: Dataset): Uni[Either[LigatureError, Unit]] = {
        ???
//        val l = lock.writeLock()
//        try {
//            l.lock()
//            if (store.contains(dataset)) {
//                val newStore = store - dataset
//                store = newStore
//                Uni(Right(()))
//            } else {
//                Uni(Right(()))
//            }
//        } finally {
//            l.unlock()
//        }
    }

    /** Initializes a QueryTx
     * TODO should probably return its own error type CouldNotInitializeQueryTx */
    def query[T](dataset: Dataset, fn: (queryTx: QueryTx) => T): Uni[T] = {
        ???
//        val l = lock.readLock()
//
//        val acquire: Uni[InMemoryQueryTx] = Uni {
//            l.lock()
//            new InMemoryQueryTx(store(dataset))
//        }
//
//        val release: QueryTx => Uni[Unit] = _ => Uni {
//            l.unlock()
//            ()
//        }
//
//        Resource.make(acquire)(release)
    }

    /** Initializes a WriteTx
     * TODO should probably return its own error type CouldNotInitializeWriteTx */
    def write[T](dataset: Dataset, fn: (writeTx: WriteTx) => T): Uni[T] = { //TODO acquire write lock
        ???
//        val l = lock.writeLock()
//
//        val acquire: Uni[InMemoryWriteTx] = Uni {
//            l.lock()
//            new InMemoryWriteTx(store(dataset))
//        }
//
//        val release: InMemoryWriteTx => Uni[Unit] = writeTx => Uni {
//            if (!writeTx.isCanceled()) {
//                val newStore = this.store.updated(dataset, writeTx.newDatasetStore())
//                this.store = newStore
//            }
//            l.unlock()
//            ()
//        }
//
//        Resource.make(acquire)(release)
    }
}
