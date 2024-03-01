/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import scala.annotation.unused

final case class GraphName(name: String) extends Ordered[GraphName]:
  override def compare(that: GraphName): Int = this.name.compare(that.name)

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

enum LigatureValue:
  case Label(value: String)
  case StringValue(value: String)
  case IntegerValue(value: Long)
  case BytesValue(value: Array[Byte])

//sealed trait Range
//final case class StringValueRange(start: String, end: String) extends Range
//final case class IntegerValueRange(start: Long, end: Long) extends Range
final case class Edge(
    source: LigatureValue.Label,
    label: LigatureValue.Label,
    target: LigatureValue
)

/** A trait that all Ligature implementations implement. */
trait Ligature:
  /** Returns all Graphs in a Ligature instance. */
  def allGraphs(): Iterator[GraphName]

  /** Check if a given Graph exists. */
  def graphExists(graph: GraphName): Boolean

  /** Returns all Graphs in a Ligature instance that start with the given
    * prefix.
    */
  def matchGraphsPrefix(
      prefix: String
  ): Iterator[GraphName]

  /** Returns all Graphs in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  def matchGraphsRange(
      start: String,
      end: String
  ): Iterator[GraphName]

  /** Creates a graph with the given name. TODO should probably return its own
    * error type { InvalidGraph, GraphExists, CouldNotCreateGraph }
    */
  def createGraph(graph: GraphName): Unit

  /** Deletes a graph with the given name. TODO should probably return its own
    * error type { InvalidGraph, CouldNotDeleteGraph }
    */
  def deleteGraph(graph: GraphName): Unit

  def allEdges(graph: GraphName): Iterator[Edge]

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  def query[T](graph: GraphName)(fn: QueryTx => T): T

  def addEdges(graph: GraphName, edges: Iterator[Edge]): Unit

  def removeEdges(graph: GraphName, edges: Iterator[Edge]): Unit

  def close(): Unit

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Graph
  */
trait QueryTx:
  /** Returns all PersistedEdges that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allEdges.
    */
  def matchEdges(
      source: Option[LigatureValue.Label] = None,
      label: Option[LigatureValue.Label] = None,
      target: Option[LigatureValue] = None
  ): Iterator[Edge]

//  /** Returns all PersistedEdges that match the given criteria. If a
//    * parameter is None then it matches all.
//    */
//  def matchEdgesRange(
//      source: Option[Identifier] = None,
//      label: Option[Identifier] = None,
//      target: Range
//  ): Stream[IO, Edge]
