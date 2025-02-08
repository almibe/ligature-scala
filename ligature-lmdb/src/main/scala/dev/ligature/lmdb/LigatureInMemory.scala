/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.util.concurrent.locks.ReentrantReadWriteLock
import cats.effect.IO
import fs2.Stream
// import scala.collection.immutable.TreeSet
import scodec.bits.ByteVector
import scala.collection.immutable.SortedMap

enum Tables:
  case Id
  case NetworkToId
  case IdToNetwork

// object TablesOrdering extends Ordering[Tables] {
//  def compare(a:Tables, b:Tables) = a.ordinal.compare(b.ordinal)
// }

class LigatureInMemory extends Ligature {
  var store: Map[Tables, SortedMap[ByteVector, ByteVector]] = Map()//(TablesOrdering)
  val lock = ReentrantReadWriteLock()

  override def networks(): Stream[IO, String] = {
    lock.readLock().lock()
    try

      Stream.emits(store.keySet.toSeq)
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
