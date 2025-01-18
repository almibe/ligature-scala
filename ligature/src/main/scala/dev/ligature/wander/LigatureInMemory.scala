package dev.ligature.wander

import scala.collection.immutable.SortedSet
import scala.collection.immutable.TreeMap
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scala.collection.immutable.TreeSet

case class Error(msg: String)

class LigatureInMemory extends Ligature[Error] {
  var store: Map[String, SortedSet[Entry]] = TreeMap()
  val lock = ReentrantReadWriteLock();

  override def collections(): Either[Error, Seq[String]] = {
    lock.readLock().lock()
    try
      Right(store.keySet.toSeq)
    finally
      lock.readLock().unlock()
  }

  override def addCollection(name: String): Either[Error, Unit] = {
    lock.writeLock().lock()
    try {

      store = store + (name -> TreeSet())
      ???
    } finally lock.readLock().unlock()
  }

  override def removeCollection(name: String): Either[Error, Unit] = ???

  override def entries(name: String): Either[Error, Seq[Entry]] = ???

  override def addEntries(name: String, entries: Seq[Entry]): Either[Error, Unit] = ???

  override def removeEntries(name: String, entries: Seq[Entry]): Either[Error, Unit] = ???
  override def query(name: String, query: Set[Query]): Either[Error, Seq[Entry]] = ???

}
