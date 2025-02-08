/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.util.concurrent.locks.ReentrantReadWriteLock
import cats.effect.IO
import fs2.Stream
import scala.collection.immutable.TreeMap
import scodec.bits.ByteVector
import scala.collection.immutable.SortedMap

enum Table:
  case Id
  case NetworkToId
  case IdToNetwork

// object TableOrdering extends Ordering[Table] {
//  def compare(a:Table, b:Table) = a.ordinal.compare(b.ordinal)
// }

def createStore(): Map[Table, SortedMap[ByteVector, ByteVector]] =
  Table.values.foldLeft(Map())((state, value) => {
    state + (value -> TreeMap[ByteVector, ByteVector]())
  })

class LigatureInMemory extends Ligature {
  var store: Map[Table, SortedMap[ByteVector, ByteVector]] = createStore()
  val lock = ReentrantReadWriteLock()

  def openTable(table: Table): SortedMap[ByteVector, ByteVector] =
    ???

  def byteVectorToNetworkName(input: ByteVector): String =
    ""

  override def networks(): Stream[IO, String] = {
    lock.readLock().lock()
    try
      Stream.emits(openTable(Table.NetworkToId).keySet.map(byteVectorToNetworkName).toSeq)
    finally
      lock.readLock().unlock()
  }

  override def addNetwork(name: String): IO[Unit] = {
    ???
    // lock.writeLock().lock()
    // try {
    //   store = store + (name -> TreeSet())
    //   Right(())
    // } finally lock.readLock().unlock()
  }

  def addEntries(name: String, entries: fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple]
    ): fs2.Stream[cats.effect.IO, Unit] = ???

  def query(name: String, query: dev.ligature.wander.LigatureValue.Pattern, template:
    dev.ligature.wander.LigatureValue.Pattern |
    dev.ligature.wander.LigatureValue.Quote):
    fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple] = ???

  def read(name: String): fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple] = ???

  def removeEntries
  (name: String, entries: fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple]
    ): fs2.Stream[cats.effect.IO, Unit] = ???
    
  def removeNetwork(name: String): cats.effect.IO[Unit] = ???

}
