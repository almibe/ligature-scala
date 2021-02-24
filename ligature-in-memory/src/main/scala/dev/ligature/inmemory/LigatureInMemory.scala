/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not diStringibuted with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.{Attribute, Dataset, Entity, Ligature, LigatureError, LigatureInstance, PersistedStatement, QueryTx, Statement, Value, WriteTx}
import cats.effect.Resource
import monix.reactive._
import monix.eval.Task
import scala.collection.immutable.TreeMap

import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

/** A trait that all Ligature implementations implement. */
final class InMemoryLigature extends Ligature {
  private val acquire: Task[LigatureInstance] = Task(new InMemoryLigatureInstance())
  private val release: LigatureInstance => Task[Unit] = _ => Task.unit
  def instance: Resource[Task, LigatureInstance] = {
    Resource.make(acquire)(release)
  }
}

private final class InMemoryLigatureInstance extends LigatureInstance {
  private case class DatasetStore(counter: Long, statements: Set[PersistedStatement])
  private var store = TreeMap[Dataset, DatasetStore]()
  private val lock: ReadWriteLock = new ReentrantReadWriteLock()

  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Observable[Either[LigatureError, Dataset]] = {
    val l = lock.readLock()
    try {
      l.lock()
      Observable.fromIterable(store.keys.toList.map(Right(_)))
    } finally  {
      l.unlock()
    }
  }

  /** Check if a given Dataset exists. */
  def datasetExists(dataset: Dataset): Task[Either[LigatureError, Boolean]] = Task {
    Right(store.contains(dataset)) //todo should lock before reading
  }

  /** Returns all Datasets in a Ligature instance that start with the given prefix. */
  def matchDatasetsPrefix(
    prefix: String,
  ): Observable[Either[LigatureError, Dataset]] = {
    val l = lock.readLock()
    try {
      l.lock()
      Observable.fromIterable(store.filter(_._1.name.startsWith(prefix)).keys.map(Right(_)))
    } finally {
      l.unlock()
    }
  }

  /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
  def matchDatasetsRange(
    start: String,
    end: String,
  ): Observable[Either[LigatureError, Dataset]] = {
    val l = lock.readLock()
    try {
      l.lock()
      Observable.fromIterable(store.filter { case (k, v) => k.name >= start && k.name < end }.keys.map(Right(_)))
    } finally {
      l.unlock()
    }
  }

  /** Creates a dataset with the given name.
    * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
  def createDataset(dataset: Dataset): Task[Either[LigatureError, Unit]] = Task {
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
  def deleteDataset(dataset: Dataset): Task[Either[LigatureError, Unit]] = {
    val l = lock.writeLock()
    try {
      l.lock()
      if (store.contains(dataset)) {
        val newStore = store - dataset
        store = newStore
        Task(Right(()))
      } else {
        Task(Right(()))
      }
    } finally {
      l.unlock()
    }
  }

  /** Initiazes a QueryTx
   * TODO should probably return its own error type CouldNotInitializeQueryTx */
  def query(dataset: Dataset): Resource[Task, QueryTx] = {
    ???
  }

  /** Initiazes a WriteTx
   * TODO should probably return its own error type CouldNotInitializeWriteTx */
  def write(dataset: Dataset): Resource[Task, WriteTx] = {
    ???
  }
}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
trait InMemoryQueryTx {
  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Observable[Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
  def matchStatements(
    source: Option[Entity],
    arrow: Option[Attribute],
    target: Option[Value],
  ): Observable[Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Retuns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all. */
  def matchStatementsRange(
    source: Option[Entity],
    arrow: Option[Attribute],
    target: Range,
  ): Observable[Either[LigatureError, PersistedStatement]] = {
    ???
  }

  /** Returns the PersistedStatement for the given context. */
  def statementForContext(
    context: Entity,
  ): Task[Either[LigatureError, Option[PersistedStatement]]] = {
    ???
  }
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
trait InMemoryWriteTx {
  /** Creates a new, unique Entity within this Dataset.
   * Note: Entities are shared across named graphs in a given Dataset. */
  def newEntity(): Either[LigatureError, Entity] = {
    ???
  }

  /** Adds a given Statement to this Dataset.
   * If the Statement already exists nothing happens (TODO maybe add it with a new context?).
   * Note: Potentally could trigger a ValidationError */
  def addStatement(statement: Statement): Either[LigatureError, PersistedStatement] = {
    ???
  }

  /** Removes a given PersistedStatement from this Dataset.
   * If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
   * This function returns Ok(true) only if the given PersistedStatement was found and removed.
   * Note: Potentally could trigger a ValidationError. */
  def removeStatement(
    persistedStatement: PersistedStatement,
  ): Either[LigatureError, Boolean] = {
    ???
  }

  /** Cancels this transaction so that none of the changes made so far will be stored.
   * This also closes this transaction so no other methods can be called without returning a LigatureError. */
  def cancel(): Either[LigatureError, Unit] = {
    ???
  }
}
