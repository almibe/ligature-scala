/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

trait Source[+T] {
  def apply(index: Int): Option[T]
  def length(): Int
}

class StringSource(private val source: String) extends Source[String] {
  override def apply(index: Int): Option[String] = source.lift(index).map(s => s.toString())
  override def length(): Int = source.length()
}

class SeqSource[T](private val source: Seq[T]) extends Source[T] {
  override def apply(index: Int): Option[T] = source.lift(index)
  override def length(): Int = source.length
}
