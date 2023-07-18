/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Dataset, Identifier, Ligature, LigatureError, QueryTx, Statement, Value}
import cats.effect.IO
import cats.effect.std.AtomicCell
import fs2.Stream
import scala.collection.immutable.TreeMap
import cats.effect.kernel.Resource

protected case class DatasetStore(counter: Long, statements: Set[Statement])

def createInMemoryLigature(): Resource[IO, InMemoryLigature] =
  Resource.make(
    for {
      store <- AtomicCell[IO].of(TreeMap[Dataset, DatasetStore]())
    } yield InMemoryLigature(store)
  )(store => store.close())

final class InMemoryLigature(private val store: AtomicCell[IO, TreeMap[Dataset, DatasetStore]])
    extends Ligature {
//  private val store = AtomicCell[IO].of(TreeMap[Dataset, DatasetStore]())

  /** Returns all Datasets in a Ligature instance. */
  override def allDatasets(): Stream[IO, Dataset] = Stream.evalSeq {
    for {
      store <- store.get
    } yield store.keys.toSeq
  }

  /** Check if a given Dataset exists. */
  override def datasetExists(dataset: Dataset): IO[Boolean] =
    for {
      store <- store.get
    } yield store.contains(dataset)

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  override def matchDatasetsPrefix(prefix: String): Stream[IO, Dataset] = Stream.evalSeq {
    for {
      store <- store.get
    } yield store.keys.filter(_.name.startsWith(prefix)).toSeq
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
        store <- store.get
      } yield store.keys.filter(k => k.name >= start && k.name < end).toSeq
    }

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  override def createDataset(dataset: Dataset): IO[Unit] =
    for {
      _ <- store.evalUpdate { s =>
        IO(s.updated(dataset, DatasetStore(0L, Set())))
      } // TODO this needs to check if Dataset already exists
      s <- store.get
    } yield ()

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  override def deleteDataset(dataset: Dataset): IO[Unit] =
    for {
      _ <- store.evalUpdate(s => IO(s.removed(dataset)))
      s <- store.get
    } yield ()

  override def allStatements(dataset: Dataset): Stream[IO, Statement] = Stream.evalSeq {
    store.get.map { store =>
      store.get(dataset) match
        case None => ???
        case Some(datasetStore) =>
          datasetStore.statements.toSeq
    }
  }

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  override def query[T](dataset: Dataset)(fn: QueryTx => IO[T]): IO[T] =
    store.get
      .map { store =>
        store.get(dataset) match
          case None => ???
          case Some(datasetStore) =>
            InMemoryQueryTx(datasetStore)
      }
      .bracket(tx => fn(tx))(_ => IO.unit)

  override def addStatements(
      dataset: Dataset,
      statements: Stream[cats.effect.IO, Statement]
  ): IO[Unit] =
    store.evalUpdate { store =>
      store.get(dataset) match
        case None => ???
        case Some(datasetStore) =>
          statements.compile
            .fold(datasetStore) { (datasetStore, statement) =>
              datasetStore.copy(statements = datasetStore.statements + statement)
            }
            .map { datasetStore =>
              store.updated(dataset, datasetStore)
            }
    }

  override def removeStatements(
      dataset: Dataset,
      statements: Stream[cats.effect.IO, Statement]
  ): IO[Unit] =
    store.evalUpdate { store =>
      store.get(dataset) match
        case None => ???
        case Some(datasetStore) =>
          statements.compile
            .fold(datasetStore) { (datasetStore, statement) =>
              datasetStore.copy(statements = datasetStore.statements - statement)
            }
            .map { datasetStore =>
              store.updated(dataset, datasetStore)
            }
    }

  override def close(): IO[Unit] =
    IO.unit
}
