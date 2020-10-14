/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.almibe.slonky

import cats.effect.Resource
import monix.eval.Task
import monix.reactive.Observable
import scodec.bits.ByteVector

trait Slonky {
  def instance: Resource[Task, SlonkyInstance]
}

trait SlonkyInstance {
  def read: Resource[Task, ReadTx]
  def write: Resource[Task, WriteTx]
}

trait ReadTx {
  def keyExists(key: ByteVector): Boolean
  def prefixExists(prefix: ByteVector): Boolean
  def get(key: ByteVector): Option[ByteVector]
  def prefixScan(prefix: ByteVector): Observable[(ByteVector, ByteVector)]
  def rangeScan(from: ByteVector, to: ByteVector): Observable[(ByteVector, ByteVector)]
  def scanAll(): Observable[(ByteVector, ByteVector)]
}

trait WriteTx {
  def keyExists(key: ByteVector): Boolean
  def prefixExists(prefix: ByteVector): Boolean
  def get(key: ByteVector): Option[ByteVector]
  def put(key: ByteVector, value: ByteVector): (ByteVector, ByteVector)
  def remove(key: ByteVector): (ByteVector, ByteVector)
  def cancel(): Unit
}
