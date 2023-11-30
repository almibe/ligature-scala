/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import scala.annotation.unused

final case class Dataset(name: String) extends Ordered[Dataset] {
    override def compare(that: Dataset): Int = this.name.compare(that.name)
}
final case class Label(text: String)

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

enum LigatureLiteral:
  case StringLiteral(value: String)
  case IntegerLiteral(value: Long)

type Value = LigatureLiteral | Label

//sealed trait Range
//final case class StringLiteralRange(start: String, end: String) extends Range
//final case class IntegerLiteralRange(start: Long, end: Long) extends Range
final case class Edge(
    source: Label,
    label: Label,
    target: Value
)

/** A trait that all Ligature implementations implement. */
trait Ligature {

  /** Returns all Datasets in a Ligature instance. */
  def allDatasets(): Iterator[Dataset]

  /** Check if a given Dataset exists. */
  def datasetExists(dataset: Dataset): Boolean

  /** Returns all Datasets in a Ligature instance that start with the given
    * prefix.
    */
  def matchDatasetsPrefix(
      prefix: String
  ): Iterator[Dataset]

  /** Returns all Datasets in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  def matchDatasetsRange(
      start: String,
      end: String
  ): Iterator[Dataset]

  /** Creates a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, DatasetExists, CouldNotCreateDataset }
    */
  def createDataset(dataset: Dataset): Unit

  /** Deletes a dataset with the given name. TODO should probably return its own
    * error type { InvalidDataset, CouldNotDeleteDataset }
    */
  def deleteDataset(dataset: Dataset): Unit

  def allEdges(dataset: Dataset): Iterator[Edge]

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  def query[T](dataset: Dataset)(fn: QueryTx => T): T

  def addEdges(dataset: Dataset, edges: Iterator[Edge]): Unit

  def removeEdges(dataset: Dataset, edges: Iterator[Edge]): Unit

  def close(): Unit
}

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
trait QueryTx {

  /** Returns all PersistedEdges that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allEdges.
    */
  def matchEdges(
      source: Option[Label] = None,
      label: Option[Label] = None,
      target: Option[Value] = None
  ): Iterator[Edge]

//  /** Returns all PersistedEdges that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  def matchEdgesRange(
//      source: Option[Identifier] = None,
//      label: Option[Identifier] = None,
//      target: Range
//  ): Stream[IO, Edge]
}
