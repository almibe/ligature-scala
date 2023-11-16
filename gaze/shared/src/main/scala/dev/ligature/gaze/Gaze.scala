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

sealed class Gaze[+I](private val input: Seq[I]) {
  protected var offset: Int = 0

  def isComplete: Boolean =
    this.offset >= this.input.length

  def peek(): Result[I] =
    if (this.isComplete) {
      Result.NoMatch
    } else {
      Result.Match(this.input(this.offset))
    }

  def next(): Result[I] =
    if (this.isComplete) {
      Result.NoMatch
    } else {
      val next = Result.Match(this.input(this.offset))
      this.offset += 1
      next
    }

  // TODO needs tests
  def check[O](nibbler: Nibbler[I, O]): Result[O] = {
    val startOfThisLoop = this.offset
    val res = nibbler(this)

    res match {
      case Result.Match(_) | Result.EmptyMatch =>
        this.offset = startOfThisLoop
        res
      case Result.NoMatch =>
        this.offset = startOfThisLoop
        res
    }
  }

  def attempt[O](nibbler: Nibbler[I, O]): Result[O] = {
    val startOfThisLoop = this.offset
    val res = nibbler(this)

    res match {
      case Result.Match(_) | Result.EmptyMatch => res
      case Result.NoMatch =>
        this.offset = startOfThisLoop
        res
    }
  }
}

class StringGaze(input: String) extends Gaze[Char](input.toList) {
  //TODO location info might not update correctly when a Nibble fails?
  private var line: Int = 0
  private var lineOffset: Int = 0

  override def next(): Result[Char] =
    if (this.isComplete) {
      Result.NoMatch
    } else {
      val next: Result.Match[Char] = Result.Match(this.input(this.offset))
      this.offset += 1
      this.lineOffset += 1
      if (next.value == '\n') {
        this.line += 1
        this.lineOffset = 0
      }
      next
    }
  def location: Location = {
    Location(this.line, this.lineOffset)
  }
}

enum Result[+T]:
  def map[U](f: T => U): Result[U] =
    this match {
      case EmptyMatch => EmptyMatch
      case NoMatch => NoMatch
      case Match(value) => Match(f(value))
    }
    //if (isEmpty) None else Some(f(this.get))
  def flatten[U](implicit ev: T <:< Result[U]): Result[U] =
    this match {
      case EmptyMatch => EmptyMatch
      case NoMatch => NoMatch
      case Match(value) => ev(value)
    }
  def flatMap[U](f: T => Result[U]): Result[U] =
    this match {
      case EmptyMatch => EmptyMatch
      case NoMatch => NoMatch
      case Match(value) => f(value)
    }
  //  if (isEmpty) None else f(this.get)
  case Match(value: T)
  case EmptyMatch
  case NoMatch

abstract class Nibbler[-I, +O] {
  def apply(gaze: Gaze[I]): Result[O]

  final def map[NO](f: O => NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    this.apply(gaze) match {
      case Result.NoMatch => Result.NoMatch
      case Result.Match[O](value) => Result.Match(f(value))
      case Result.EmptyMatch => Result.EmptyMatch
    }
  }

  final def as[NO](value: NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    this.apply(gaze) match {
      case Result.NoMatch => Result.NoMatch
      case Result.Match(_) | Result.EmptyMatch => Result.Match(value)
    }
  }
}
