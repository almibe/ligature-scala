/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature

import scala.annotation.unused

final case class Graph(name: String) extends Ordered[Graph] {
  override def compare(that: Graph): Int = this.name.compare(that.name)
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

  /** Returns all Graphs in a Ligature instance. */
  def allGraphs(): Iterator[Graph]

  /** Check if a given Graph exists. */
  def graphExists(graph: Graph): Boolean

  /** Returns all Graphs in a Ligature instance that start with the given
    * prefix.
    */
  def matchGraphsPrefix(
      prefix: String
  ): Iterator[Graph]

  /** Returns all Graphs in a Ligature instance that are in a given range
    * (inclusive, exclusive].
    */
  def matchGraphsRange(
      start: String,
      end: String
  ): Iterator[Graph]

  /** Creates a graph with the given name. TODO should probably return its own
    * error type { InvalidGraph, GraphExists, CouldNotCreateGraph }
    */
  def createGraph(graph: Graph): Unit

  /** Deletes a graph with the given name. TODO should probably return its own
    * error type { InvalidGraph, CouldNotDeleteGraph }
    */
  def deleteGraph(graph: Graph): Unit

  def allEdges(graph: Graph): Iterator[Edge]

  /** Initializes a QueryTx TODO should probably return its own error type
    * CouldNotInitializeQueryTx
    */
  def query[T](graph: Graph)(fn: QueryTx => T): T

  def addEdges(graph: Graph, edges: Iterator[Edge]): Unit

  def removeEdges(graph: Graph, edges: Iterator[Edge]): Unit

  def close(): Unit
}

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Graph
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
