/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

final case class DatasetName(name: String) extends Ordered[DatasetName]:
  override def compare(that: DatasetName): Int = this.name.compare(that.name)

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

enum LigatureValue:
  case Identifier(value: String)
  case StringValue(value: String)
  case IntegerValue(value: Long)
  case BytesValue(value: Seq[Byte])
  case Record(values: Map[String, LigatureValue])

//sealed trait Range
//final case class StringValueRange(start: String, end: String) extends Range
//final case class IntegerValueRange(start: Long, end: Long) extends Range
final case class Statement(
    entity: LigatureValue.Identifier,
    attribute: LigatureValue.Identifier,
    value: LigatureValue
)

/** A trait that all Ligature implementations implement. */
trait Ligature:
  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Iterator[DatasetName]

  /** Check if a given Dataset exists. */
  def graphExists(graph: DatasetName): Boolean

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  def matchDatasetsPrefix(
      prefix: String
  ): Iterator[DatasetName]

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  def matchDatasetsRange(
      start: String,
      end: String
  ): Iterator[DatasetName]

  /** Creates a graph with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  def createDataset(graph: DatasetName): Unit

  /** Deletes a graph with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  def deleteDataset(graph: DatasetName): Unit

  def allStatements(graph: DatasetName): Iterator[Statement]

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  def query[T](graph: DatasetName)(fn: QueryTx => T): T

  def addStatements(graph: DatasetName, edges: Iterator[Statement]): Unit

  def removeStatements(graph: DatasetName, edges: Iterator[Statement]): Unit

  def close(): Unit

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
trait QueryTx:
  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  def matchStatements(
      source: Option[LigatureValue.Identifier] = None,
      label: Option[LigatureValue.Identifier] = None,
      target: Option[LigatureValue] = None
  ): Iterator[Statement]

//  /** Returns all PersistedStatements that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  def matchStatementsRange(
//      source: Option[Identifier] = None,
//      label: Option[Identifier] = None,
//      target: Range
//  ): Stream[IO, Statement]
