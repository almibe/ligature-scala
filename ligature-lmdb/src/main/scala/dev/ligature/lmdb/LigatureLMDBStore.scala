/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import java.util.concurrent.locks.ReentrantReadWriteLock
import cats.effect.IO
import fs2.Stream
import scala.collection.mutable.TreeMap
import scodec.bits.ByteVector
import scodec.Codec
import scala.collection.mutable.SortedMap
import scodec.Codec.given_Codec_Long
import scodec.Codec.given_Codec_String

enum Table:
  case Id
  case NetworkToId
  case IdToNetwork
  case ElementToId
  case IdToElement
  case EAV
  case EVA
  case AEV
  case AVE
  case VEA
  case VAE

def encodeString(value: String): ByteVector = 
  Codec.encode(value).require.toByteVector

def decodeString(value: ByteVector): String = 
  Codec[String].decode(value.toBitVector).require.value

def encodeLong(value: Long): ByteVector =
  Codec.encode(value).require.toByteVector

def decodeLong(value: ByteVector): Long =
  Codec[Long].decode(value.toBitVector).require.value

object TableOrdering extends Ordering[Table] {
 def compare(a:Table, b:Table) = a.ordinal.compare(b.ordinal)
}

def createInMemoryStore(): SortedMap[Table, SortedMap[ByteVector, ByteVector]] =
  val store: SortedMap[Table, SortedMap[ByteVector, ByteVector]] = TreeMap[Table, SortedMap[ByteVector, ByteVector]]()(TableOrdering)
  Table.values.foreach { value =>
    store += (value -> TreeMap[ByteVector, ByteVector]())
  }
  store

trait KVStoreRead {
  def get(table: Table, key: ByteVector): IO[Option[ByteVector]]
  def scan(table: Table): Stream[IO, (ByteVector, ByteVector)]
}

trait KVStoreWrite extends KVStoreRead {
  def set(table: Table, key: ByteVector, value: ByteVector): IO[Unit]
}

trait KVStore {
  def readTx[T](fn: (KVStoreRead) => T): T
  def writeTx(fn: (KVStoreWrite) => Unit): Unit
}

class InMemoryStoreWrite(val store: SortedMap[Table, SortedMap[ByteVector, ByteVector]]) extends KVStoreWrite {
  override def get(table: Table, key: ByteVector): IO[Option[ByteVector]] =
    ???

  def scan(table: Table): Stream[IO, (ByteVector, ByteVector)] = ???

  override def set(table: Table, key: ByteVector, value: ByteVector): IO[Unit] = ???
}

class InMemoryStoreRead(val store: SortedMap[Table, SortedMap[ByteVector, ByteVector]]) extends KVStoreRead {
  def openTable(table: Table): SortedMap[ByteVector, ByteVector] =
    store.get(table) match
      case None => ???
      case Some(value) => value

  override def scan(table: Table): Stream[IO, (ByteVector, ByteVector)] =
    Stream.emits(openTable(Table.NetworkToId).toSeq)

  override def get(table: Table, key: ByteVector): IO[Option[ByteVector]] = {
    ???
  }
}

class InMemoryStore extends KVStore {
  val store: SortedMap[Table, SortedMap[ByteVector, ByteVector]] = createInMemoryStore()
  val lock = ReentrantReadWriteLock()

  override def readTx[T](fn: (KVStoreRead) => T) =
    lock.readLock().lock()
    try {
      val tx = InMemoryStoreRead(store)
      fn(tx)
    } finally {
      lock.readLock().unlock()
    }
  override def writeTx(fn: (KVStoreWrite) => Unit) = 
    lock.writeLock().lock()
    try {
      val tx = InMemoryStoreWrite(store)
      fn(tx)
    } finally {
      lock.writeLock().unlock()
    }
}

// class LMDBStoreRead extends KVStoreRead {
//   override def get(table: Table, key: ByteVector): IO[Option[ByteVector]] =
//     ???
// }

// class LMDBStoreWrite extends KVStoreWrite {
//   def set(table: Table, key: ByteVector, value: ByteVector): IO[Unit] = ???
// }

// class LMDBStore extends KVStore {
//   override def readTx[T](fn: (KVStoreRead) => IO[T]) = ???
//   override def writeTx(fn: (KVStoreWrite) => IO[Unit]) = ???
// }

class KVBasedStore(val store: KVStore) extends Store {
  def nextId(): ByteVector = 
    // store.writeTx(tx =>
    //   for {
    //     t <- tx.get(Table.Id, ByteVector.empty)
    //   } yield t
      //  match {
      //   case None =>
      //     val id = encodeLong(0L)
      //     tx.set(Table.Id, ByteVector.empty, id)
      //     id
      //   case Some(prevId) => ???
      // })
    ???

  def networks(): Stream[IO, String] =
    store.readTx((tx) =>
      tx.scan(Table.NetworkToId).map((value) => decodeString(value._1))
//      Stream.emits(openTable(Table.NetworkToId).keySet.map(decodeString).toSeq)
      )

  def addNetwork(name: String): IO[Unit] =
    IO(store.writeTx((_) => 
      ()))
    // lock.writeLock().lock()
    // try {
    //   val encodedName = encodeString(name)
    //   val networkToIdTable = openTable(Table.NetworkToId)
    //   if networkToIdTable.contains(encodedName) then
    //     IO.pure(())
    //   else
    //     val id = nextId()
    //     val idToNetworkTable = openTable(Table.IdToNetwork)
    //     networkToIdTable += encodedName -> id
    //     idToNetworkTable += id -> encodedName
    //     IO.pure(())
    // } finally lock.writeLock().unlock()

  def merge(
      name: String,
      network: Network
  ): IO[Unit] =
    ???

  def remove(
      name: String,
      network: Network
  ): IO[Unit] = 
    ???

  def readNetwork(name: String): IO[Network] =
    ???
  //   lock.readLock().lock()
  //   try {
  //     val encodedName = encodeString(name)
  //     val networkToIdTable = openTable(Table.NetworkToId)
  //     networkToIdTable.get(encodedName) match
  //       case Some(id) => IO(InMemoryNetwork(Set())) //TODO update
  //       case None => IO.raiseError(WanderError(s"Could not read Network $name."))
  //   } finally lock.readLock().unlock()

  def removeNetwork(name: String): cats.effect.IO[Unit] =
    ???
    // lock.writeLock().lock()
    // try {
    //   val encodedName = encodeString(name)
    //   val networkToIdTable = openTable(Table.NetworkToId)
    //   networkToIdTable.get(encodedName) match
    //     case Some(id) =>
    //       val idToNetworkTable = openTable(Table.IdToNetwork)
    //       networkToIdTable -= encodedName
    //       idToNetworkTable -= id
    //       IO.pure(())
    //     case None => IO.pure(())
    // } finally lock.writeLock().unlock()
}
