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
  def exists(key: ByteVector): Task[Boolean]
  def get(key: ByteVector): Task[Option[ByteVector]]
  def prefixScan(prefix: ByteVector): Task[Observable[(ByteVector, ByteVector)]]
  def rangeScan(from: ByteVector, to: ByteVector): Task[Observable[(ByteVector, ByteVector)]]
  def all(): Task[Observable[(ByteVector, ByteVector)]]
}

trait WriteTx {
  def exists(key: ByteVector): Task[Boolean]
  def get(key: ByteVector): Task[Option[ByteVector]]
  def put(key: ByteVector, value: ByteVector): Task[(ByteVector, ByteVector)]
  def remove(key: ByteVector): Task[(ByteVector, ByteVector)]
  def cancel(): Unit
}
