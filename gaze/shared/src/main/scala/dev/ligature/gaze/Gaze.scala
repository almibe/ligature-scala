/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import scala.collection.mutable.ArrayBuffer

object Gaze {
  def from(
      text: String
  ): Gaze[Char] = // TODO eventually handle unicode better and make this Gaze[String]
    new Gaze(text.toSeq)
}

case class Location(line: Int, lineOffset: Int)

class Gaze[+I](private val input: Seq[I]) {
  private var offset: Int = 0
  private var line: Int = 0
  private var lineOffset: Int = 0

  def isComplete: Boolean =
    this.offset >= this.input.length

  def peek(): Option[I] =
    if (this.isComplete) {
      None
    } else {
      Some(this.input(this.offset))
    }

  def next(): Option[I] =
    if (this.isComplete) {
      None
    } else {
      val next = Some(this.input(this.offset))
      this.offset += 1
      this.lineOffset += 1
      if (next.value == "\n") {
        this.line += 1
        this.lineOffset = 0
      }
      next
    }

  // TODO needs tests
  def check[O](nibbler: Nibbler[I, O]): Option[O] = {
    val startOfThisLoop = this.offset
    val res = nibbler(this)

    res match {
      case Some(_) =>
        this.offset = startOfThisLoop
        res
      case None =>
        this.offset = startOfThisLoop
        res
    }
  }

  def attempt[O](nibbler: Nibbler[I, O]): Option[O] = {
    val startOfThisLoop = this.offset
    val res = nibbler(this)

    res match {
      case Some(_) => res
      case None =>
        this.offset = startOfThisLoop
        res
    }
  }
  
  def location: Location = {
    Location(this.line, this.lineOffset)
  }
}

abstract class Nibbler[-I, +O] {
  def apply(gaze: Gaze[I]): Option[O]

  final def map[NO](f: O => NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    this.apply(gaze) match {
      case None        => None
      case Some(value) => Some(f(value))
    }
  }

  final def as[NO](value: NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    this.apply(gaze) match {
      case None    => None
      case Some(_) => Some(value)
    }
  }
}
