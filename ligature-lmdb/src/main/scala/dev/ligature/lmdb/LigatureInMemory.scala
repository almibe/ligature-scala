/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.util.concurrent.locks.ReentrantReadWriteLock
import cats.effect.IO
import fs2.Stream
import scala.collection.mutable.TreeMap
import scodec.bits.ByteVector
import scala.collection.mutable.SortedMap
import scodec.Codec
// import scodec.codecs.implicits._
import scodec.Codec.given_Codec_String

enum Table:
  case Id
  case NetworkToId
  case IdToNetwork

object TableOrdering extends Ordering[Table] {
 def compare(a:Table, b:Table) = a.ordinal.compare(b.ordinal)
}

def createStore(): SortedMap[Table, SortedMap[ByteVector, ByteVector]] =
  val store: SortedMap[Table, SortedMap[ByteVector, ByteVector]] = TreeMap[Table, SortedMap[ByteVector, ByteVector]]()(TableOrdering)
  Table.values.foreach { value =>
    store += (value -> TreeMap[ByteVector, ByteVector]())
  }
  store

class InMemoryStore extends Store {
  val store: SortedMap[Table, SortedMap[ByteVector, ByteVector]] = createStore()
  val lock = ReentrantReadWriteLock()

  def nextId(): ByteVector =
    val tbl = openTable(Table.Id)
    tbl.get(ByteVector.empty) match
      case None => ???
      case Some(prevId) => ???

  def openTable(table: Table): SortedMap[ByteVector, ByteVector] =
    store.get(table) match
      case None => ???
      case Some(value) => value

  def byteVectorToNetworkName(input: ByteVector): String =
    ""

  def networks(): Stream[IO, String] = {
    lock.readLock().lock()
    try
      Stream.emits(openTable(Table.NetworkToId).keySet.map(byteVectorToNetworkName).toSeq)
    finally
      lock.readLock().unlock()
  }

  def addNetwork(name: String): IO[Unit] =
    lock.writeLock().lock()
    try {
      val encodedName = Codec.encode(name).require
      val networkToIdTable = openTable(Table.NetworkToId)
      if networkToIdTable.contains(encodedName.toByteVector) then
        IO.pure(())
      else

        ???
    } finally lock.writeLock().unlock()

  def merge(
      name: String,
      network: Network
  ): IO[Unit] = ???

  def readNetwork(name: String): IO[Network] = {
    ???
  }

  def remove(
      name: String,
      network: Network
  ): IO[Unit] = ???

  def removeNetwork(name: String): cats.effect.IO[Unit] = ???
}
