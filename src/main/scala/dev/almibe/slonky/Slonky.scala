/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.almibe.slonky

import cats.effect.{IO, Resource}
import scodec.bits.ByteVector
import fs2.Stream

trait Slonky {
  def instance: Resource[IO, SlonkyInstance]
}

trait SlonkyInstance {
  def read: Resource[IO, SlonkyReadTx]
  def write: Resource[IO, SlonkyWriteTx]
}

trait SlonkyReadTx {
  def keyExists(key: ByteVector): IO[Boolean]
  def prefixExists(prefix: ByteVector): IO[Boolean]
  def get(key: ByteVector): IO[Option[ByteVector]]
  def prefixScan(prefix: ByteVector): Stream[IO, (ByteVector, ByteVector)]
  def rangeScan(from: ByteVector, to: ByteVector): Stream[IO, (ByteVector, ByteVector)]
  def scanAll(): Stream[IO, (ByteVector, ByteVector)]
}

trait SlonkyWriteTx {
  def keyExists(key: ByteVector): IO[Boolean]
  def prefixExists(prefix: ByteVector): IO[Boolean]
  def get(key: ByteVector): IO[Option[ByteVector]]
  def put(key: ByteVector, value: ByteVector): IO[(ByteVector, ByteVector)]
  def remove(key: ByteVector): IO[(ByteVector, ByteVector)]
  def cancel(): IO[Unit]
}
