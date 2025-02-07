/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.immutable.SortedSet
import scala.collection.immutable.TreeMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import scala.collection.immutable.TreeSet
import scala.math.Ordering.comparatorToOrdering
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni

class LigatureInMemory extends Ligature {
  var store: Map[String, SortedSet[Triple]] = TreeMap()
  val lock = ReentrantReadWriteLock();

  override def networks(): Multi[String] = ??? //{
  //   lock.readLock().lock()
  //   try
  //     Right(store.keySet.toSeq)
  //   finally
  //     lock.readLock().unlock()
  // }

  override def addNetwork(name: String): Uni[Unit] = ??? //{
  //   lock.writeLock().lock()
  //   try {
  //     store = store + (name -> TreeSet())
  //     Right(())
  //   } finally lock.readLock().unlock()
  // }
  def addEntries(name: String, entries: Multi[dev.ligature.wander.Triple])
    : Uni[Unit] = ???
  def query
  (name: String, query: dev.ligature.wander.LigatureValue.Pattern, template:
    dev.ligature.wander.LigatureValue.Pattern |
    dev.ligature.wander.LigatureValue.Quote):
    Multi[dev.ligature.wander.Triple] = ???
  def read(name: String): Multi[dev.ligature.wander.Triple] = ???
  def removeEntries
  (name: String, entries: Multi[dev.ligature.wander.Triple])
    : Uni[Unit] = ???
  def removeNetwork(name: String): Uni[Unit] = ???

}
