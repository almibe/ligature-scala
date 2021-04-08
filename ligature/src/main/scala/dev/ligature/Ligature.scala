/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import io.smallrye.mutiny.{Multi, Uni}

final case class Dataset private (val name: String) extends Ordered[Dataset] {
  private def copy(): Unit = ()

  override def compare(that: Dataset): Int = this.name.compare(that.name)
}

object Dataset {
  private def apply(name: String): Dataset = ???

  private val pattern = "^([a-zA-Z_]{1}[a-zA-Z0-9_]*)(/[a-zA-Z_]{1}[a-zA-Z0-9_]*)*$".r

  def fromString(name: String): Option[Dataset] = {
    if (pattern.matches(name)) {
      Some(new Dataset(name))
    } else {
      None
    }
  }
}

final case class Entity(val name: String) extends Value

final case class Attribute private (val name: String) {
  private def copy(): Unit = ()
}

object Attribute {
  private def apply(name: String): Attribute = ???

  private val pattern = "^[a-zA-Z_]{1}[a-zA-Z0-9_]*$".r

  def fromString(name: String): Option[Attribute] = {
    if (pattern.matches(name)) {
      Some(new Attribute(name))
    } else {
      None
    }
  }
}

final case class LigatureError(val message: String)

sealed trait Value //TODO make a sealed interface later
final case class StringLiteral(val value: String) extends Value
final case class IntegerLiteral(val value: Long) extends Value
final case class FloatLiteral(val value: Double) extends Value

sealed trait Range
final case class StringLiteralRange(val start: String, val end: String) extends Range
final case class IntegerLiteralRange(val start: Long, val end: Long) extends Range
final case class FloatLiteralRange(val start: Double, val end: Double) extends Range

final case class Statement(val entity: Entity, val attribute: Attribute, val value: Value)

final case class PersistedStatement(val statement: Statement, val context: Entity)

/** A trait that all Ligature implementations implement. */
trait Ligature {
  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Multi[Either[LigatureError, Dataset]]

  /** Check if a given Dataset exists. */
  def datasetExists(dataset: Dataset): Uni[Either[LigatureError, Boolean]]

  /** Returns all Datasets in a Ligature instance that start with the given prefix. */
  def matchDatasetsPrefix(
                           prefix: String,
                         ): Multi[Either[LigatureError, Dataset]]

  /** Returns all Datasets in a Ligature instance that are in a given range (inclusive, exclusive]. */
  def matchDatasetsRange(
                          start: String,
                          end: String,
                        ): Multi[Either[LigatureError, Dataset]]

  /** Creates a dataset with the given name.
   * TODO should probably return its own error type { InvalidDataset, DatasetExists, CouldNotCreateDataset } */
  def createDataset(dataset: Dataset): Uni[Either[LigatureError, Unit]]

  /** Deletes a dataset with the given name.
   * TODO should probably return its own error type { InvalidDataset, CouldNotDeleteDataset } */
  def deleteDataset(dataset: Dataset): Uni[Either[LigatureError, Unit]]

  /** Initiazes a QueryTx
   * TODO should probably return its own error type CouldNotInitializeQueryTx */
  def query[T](dataset: Dataset, fn: (queryTx: QueryTx) => T): Uni[T]

  /** Initiazes a WriteTx
   * TODO should probably return its own error type CouldNotInitializeWriteTx */
  def write[T](dataset: Dataset, fn: (writeTx: WriteTx) => T): Uni[T]
}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
trait QueryTx {
  /** Returns all PersistedStatements in this Dataset. */
  def allStatements(): Multi[Either[LigatureError, PersistedStatement]]

  /** Returns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
  def matchStatements(
                       entity: Option[Entity],
                       attribute: Option[Attribute],
                       value: Option[Value],
                     ): Multi[Either[LigatureError, PersistedStatement]]

  /** Retuns all PersistedStatements that match the given criteria.
   * If a parameter is None then it matches all. */
  def matchStatementsRange(
                            entity: Option[Entity],
                            attribute: Option[Attribute],
                            value: Range,
                          ): Multi[Either[LigatureError, PersistedStatement]]

  /** Returns the PersistedStatement for the given context. */
  def statementForContext(
                           context: Entity,
                         ): Uni[Either[LigatureError, Option[PersistedStatement]]]
}

/** Represents a WriteTx within the context of a Ligature instance and a single Dataset */
trait WriteTx {
  /** Creates a new, unique Entity within this Dataset by combining a UUID and an optional prefix.
   * Note: Entities are shared across named graphs in a given Dataset. */
  def newAnonymousEntity(prefix: String = ""): Uni[Either[LigatureError, Entity]]

  /** Adds a given Statement to this Dataset.
   * If the Statement already exists nothing happens (TODO maybe add it with a new context?).
   * Note: Potentally could trigger a ValidationError */
  def addStatement(statement: Statement, contextPrefix: String = ""): Uni[Either[LigatureError, PersistedStatement]]

  /** Removes a given PersistedStatement from this Dataset.
   * If the PersistedStatement doesn't exist nothing happens and returns Ok(false).
   * This function returns Ok(true) only if the given PersistedStatement was found and removed.
   * Note: Potentally could trigger a ValidationError. */
  def removeStatement(
                       persistedStatement: PersistedStatement,
                     ): Uni[Either[LigatureError, Boolean]]

  /** Cancels this transaction so that none of the changes made so far will be stored.
   * This also closes this transaction so no other methods can be called without returning a LigatureError. */
  def cancel(): Uni[Either[LigatureError, Unit]]
}
