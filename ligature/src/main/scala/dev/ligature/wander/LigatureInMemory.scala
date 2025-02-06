/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.immutable.SortedSet
import scala.collection.immutable.TreeMap
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scala.collection.immutable.TreeSet

class LigatureInMemory extends Ligature {
  var store: Map[String, SortedSet[Entry]] = TreeMap()
  val lock = ReentrantReadWriteLock();

  override def collections(): Either[LigatureError, Seq[String]] = {
    lock.readLock().lock()
    try
      Right(store.keySet.toSeq)
    finally
      lock.readLock().unlock()
  }

  override def addCollection(name: String): Either[LigatureError, Unit] = {
    lock.writeLock().lock()
    try {
      store = store + (name -> TreeSet())
      Right(())
    } finally lock.readLock().unlock()
  }

  override def removeCollection(name: String): Either[LigatureError, Unit] = ???

  override def entries(name: String): Either[LigatureError, Seq[Entry]] = ???

  override def addEntries(name: String, entries: Seq[Entry]): Either[LigatureError, Unit] = ???

  override def removeEntries(name: String, entries: Seq[Entry]): Either[LigatureError, Unit] = ???
//  override def query(name: String, query: Set[Query]): Either[LigatureError, Seq[Entry]] = ???
  override def filter(name: String, pattern: Query): Either[LigatureError, Seq[Entry]] = ???
}
