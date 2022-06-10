/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{
  Dataset,
  Identifier,
  Ligature,
  LigatureError,
  QueryTx,
  Statement,
  Value,
  WriteTx
}
import cats.effect.IO
import fs2.Stream
import scala.collection.immutable.TreeMap

import cats.effect.kernel.Resource
import cats.effect.kernel.Ref
import java.util.concurrent.atomic.AtomicReference

protected case class DatasetStore(counter: Long, statements: Set[Statement])

final class InMemoryLigature extends Ligature {
  private val store = AtomicReference(TreeMap[Dataset, DatasetStore]())

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = Stream.evalSeq {
    for {
      ref <- IO(store)
    } yield ref.get.keys.toSeq
  }

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] =
    for {
      ref <- IO(store)
    } yield ref.get.contains(dataset)

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Stream[IO, Dataset] =
    Stream.evalSeq {
      for {
        ref <- IO(store)
      } yield ref.get.keys.filter(_.name.startsWith(prefix)).toSeq
    }

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  override def matchDatasetsRange(
      start: String,
      end: String
  ): Stream[IO, Dataset] =
    Stream.evalSeq {
      for {
        ref <- IO(store)
      } yield ref.get.keys.filter(k => k.name >= start && k.name < end).toSeq
    }

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(dataset: Dataset): IO[Unit] =
    for {
      atomRef <- IO(store)
      ref <- IO(store.get)
      // _ <- ref.modify(s => (s + (dataset -> DatasetStore(0L, Set())), ())) //TODO this needs to check if Dataset already exists
      _ <- IO {
        atomRef.set(ref + (dataset -> DatasetStore(0L, Set())))
      }
    } yield ()
  // if (store.contains(dataset)) { //todo should lock before reading
  //     Right(())
  // } else {
  //     val l = lock.writeLock()
  //     try {
  //         l.lock()
  //         val newStore = store + (dataset -> DatasetStore(0L, Set()))
  //         store = newStore
  //         Right(())
  //     } finally {
  //         l.unlock()
  //     }
  // }

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(dataset: Dataset): IO[Unit] =
    for {
      atomRef <- IO(store)
      ref <- IO(store.get)
      _ <- IO {
        atomRef.set(ref.removed(dataset))
      }
    } yield ()
  // val l = lock.writeLock()
  // try {
  //     l.lock()
  //     if (store.contains(dataset)) {
  //         val newStore = store - dataset
  //         store = newStore
  //         IO(Right(()))
  //     } else {
  //         IO(Right(()))
  //     }
  // } finally {
  //     l.unlock()
  // }

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override def query[T](dataset: Dataset, fn: QueryTx => IO[T]): IO[T] =
    IO {
      val ds = this.store.get.get(dataset)
      ds match {
        case Some(ds) =>
          val tx = InMemoryQueryTx(ds)
          tx
        case None =>
          throw RuntimeException("")
      }
    }.bracket(tx => fn(tx))(_ => IO.unit)

  /** Initializes a WriteTx TODO should probably return its own error type
    * CouldNotInitializeWriteTx
    */
  override def write(dataset: Dataset, fn: WriteTx => IO[Unit]): IO[Unit] =
    IO {
      val ds = this.store.get.get(dataset)
      ds match {
        case Some(ds) =>
          val tx = InMemoryWriteTx(ds)
          tx
        case None =>
          throw RuntimeException(
            ""
          ) // WriteResult.WriteError(s"Could not write to ${dataset.name}"))
      }
    }.bracket(tx => fn(tx))(tx =>
      IO {
        val newStore =
          this.store.get.updated(dataset, tx.modifiedDatasetStore())
        this.store.set(newStore)
        ()
      }
    )

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

  override def close(): IO[Unit] =
    IO.unit
}
