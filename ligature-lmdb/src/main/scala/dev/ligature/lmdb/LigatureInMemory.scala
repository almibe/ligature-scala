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
//import java.nio.charset.StandardCharsets

enum Table:
  case Id
  case NetworkToId
  case IdToNetwork
  case ElementToId
  case IdToElement
  case LiteralToId
  case IdToLiteral

def encodeString(value: String): ByteVector = 
  Codec.encode(value).require.toByteVector

def decodeString(value: ByteVector): String = 
  Codec[String].decode(value.toBitVector).require.value

def encodeLong(value: Long): ByteVector =
  Codec.encode(value).require.toByteVector

def decodeLong(value: ByteVector): Long = ???

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
      case None =>
        val id = encodeLong(0L)
        tbl += ByteVector.empty -> id
        id
      case Some(prevId) => ???

  def openTable(table: Table): SortedMap[ByteVector, ByteVector] =
    store.get(table) match
      case None => ???
      case Some(value) => value

  def networks(): Stream[IO, String] = {
    lock.readLock().lock()
    try
      Stream.emits(openTable(Table.NetworkToId).keySet.map(decodeString).toSeq)
    finally
      lock.readLock().unlock()
  }

  def addNetwork(name: String): IO[Unit] =
    lock.writeLock().lock()
    try {
      val encodedName = encodeString(name)
      val networkToIdTable = openTable(Table.NetworkToId)
      if networkToIdTable.contains(encodedName) then
        IO.pure(())
      else
        val id = nextId()
        val idToNetworkTable = openTable(Table.IdToNetwork)
        networkToIdTable += encodedName -> id
        idToNetworkTable += id -> encodedName
        IO.pure(())
    } finally lock.writeLock().unlock()

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

  def readNetwork(name: String): IO[Network] = {
    lock.readLock().lock()
    try {
      val encodedName = encodeString(name)
      val networkToIdTable = openTable(Table.NetworkToId)
      networkToIdTable.get(encodedName) match
        case Some(id) => IO(InMemoryNetwork(Set())) //TODO update
        case None => IO.raiseError(WanderError(s"Could not read Network $name."))
    } finally lock.readLock().unlock()
  }

  def removeNetwork(name: String): cats.effect.IO[Unit] =
    lock.writeLock().lock()
    try {
      val encodedName = encodeString(name)
      val networkToIdTable = openTable(Table.NetworkToId)
      networkToIdTable.get(encodedName) match
        case Some(id) =>
          val idToNetworkTable = openTable(Table.IdToNetwork)
          networkToIdTable -= encodedName
          idToNetworkTable -= id
          IO.pure(())
        case None => IO.pure(())
    } finally lock.writeLock().unlock()
}
