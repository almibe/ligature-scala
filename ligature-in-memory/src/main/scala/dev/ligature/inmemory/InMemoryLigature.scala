/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Attribute, Dataset, Entity, Ligature, LigatureError,
    QueryTx, Statement, Value, WriteTx}
import cats.effect.{IO, Resource}
import fs2.Stream
import scala.collection.immutable.TreeMap

import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

protected case class DatasetStore(counter: Long, statements: Set[Statement])

final class InMemoryLigature extends Ligature {
    private var store = TreeMap[Dataset, DatasetStore]()
    private val lock: ReadWriteLock = new ReentrantReadWriteLock()

    /** Returns all Datasets in a Ligature instance. */
    def allDatasets(): Stream[IO, Either[LigatureError, Dataset]] = {
        val l = lock.readLock()
        try {
            l.lock()
            Stream.emits(store.keys.toList.map(Right(_)))
        } finally  {
            l.unlock()
        }
    }

    /** Check if a given Dataset exists. */
    def datasetExists(dataset: Dataset): IO[Either[LigatureError, Boolean]] = IO {
        Right(store.contains(dataset)) //todo should lock before reading
    }

    /** Returns all Datasets in a Ligature instance that start with the given prefix. */
    def matchDatasetsPrefix(
                             prefix: String,
                           ): Stream[IO, Either[LigatureError, Dataset]] = {
        val l = lock.readLock()
        try {
            l.lock()
            Stream.emits(store.filter(_._1.name.startsWith(prefix)).keys.map(Right(_)).toList)
        } finally {
            l.unlock()
        }
    }

    /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
    def matchDatasetsRange(
                            start: String,
                            end: String,
                          ): Stream[IO, Either[LigatureError, Dataset]] = {
        val l = lock.readLock()
        try {
            l.lock()
            Stream.emits(store.filter { case (k, v) => k.name >= start && k.name < end }.keys.map(Right(_)).toList)
        } finally {
            l.unlock()
        }
    }

    /** Creates a dataset with the given name.
     * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
    def createDataset(dataset: Dataset): IO[Either[LigatureError, Unit]] = IO {
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
    def deleteDataset(dataset: Dataset): IO[Either[LigatureError, Unit]] = {
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
    def query(dataset: Dataset): Resource[IO, QueryTx] = {
        val l = lock.readLock()

        val acquire: IO[InMemoryQueryTx] = IO {
            l.lock()
            new InMemoryQueryTx(store(dataset))
        }

        val release: QueryTx => IO[Unit] = _ => IO {
            l.unlock()
            ()
        }

        Resource.make(acquire)(release)
    }

    /** Initializes a WriteTx
     * TODO should probably return its own error type CouldNotInitializeWriteTx */
    def write(dataset: Dataset): Resource[IO, WriteTx] = { //TODO acquire write lock
        val l = lock.writeLock()

        val acquire: IO[InMemoryWriteTx] = IO {
            l.lock()
            new InMemoryWriteTx(store(dataset))
        }

        val release: InMemoryWriteTx => IO[Unit] = writeTx => IO {
            if (!writeTx.isCanceled()) {
                val newStore = this.store.updated(dataset, writeTx.newDatasetStore())
                this.store = newStore
            }
            l.unlock()
            ()
        }

        Resource.make(acquire)(release)
    }
}
