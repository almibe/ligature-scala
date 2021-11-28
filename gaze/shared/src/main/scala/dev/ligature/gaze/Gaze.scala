/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import scala.collection.mutable.ArrayBuffer

object Gaze {
  def from(text: String): Gaze[Char] = { // TODO eventually handle unicode better and make this Gaze[String]
    return new Gaze(text.toVector)
  }
}

class Gaze[I](private val input: Vector[I]) {
  private var offset: Int = 0

  def isComplete(): Boolean = {
    return this.offset >= this.input.length
  }

  def peek(): Option[I] = {
    if (this.isComplete()) {
      return None
    } else {
      return Some(this.input(this.offset))
    }
  }

  def next(): Option[I] = {
    if (this.isComplete()) {
      return None
    } else {
      val next = Some(this.input(this.offset))
      this.offset += 1
      return next
    }
  }

  def attempt[O](nibbler: Nibbler[I, O]): Option[Seq[O]] = {
    val startOfThisLoop = this.offset
    val res = nibbler(this)

    res match {
      case Some(_) => return res
      case None => {
        this.offset = startOfThisLoop
        return res
      }
    }
  }
}

abstract class Nibbler[I, O] {
  def apply(gaze: Gaze[I]): Option[Seq[O]]

  final def map[NO](f: Seq[O] => Seq[NO]): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    {
      this.apply(gaze) match {
        case None    => None
        case Some(value) => Some(f(value))
      }
    }
  }

  final def as[NO](value: NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    {
      this.apply(gaze) match {
        case None => None
        case Some(_)  => Some(List(value))
      }
    }
  }
}
