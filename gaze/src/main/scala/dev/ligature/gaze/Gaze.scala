/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import scala.collection.mutable.ArrayBuffer

object Gaze {
  def from(
      text: String
  ): Gaze[String] =
    new Gaze(StringSource(text))
}

case class Location(line: Int, lineOffset: Int)

sealed class Gaze[+I](private val input: Source[I]) {
  protected var offset: Int = 0

  def isComplete: Boolean =
    this.offset >= this.input.length()

  def peek(): Option[I] =
    if (this.isComplete) {
      None
    } else {
      Some(this.input(this.offset).get)
    }

  def peek(offset: Int): Option[I] =
    if (this.isComplete) {
      None
    } else {
      if this.input.length() >= (this.offset + offset) then
        Some(this.input(this.offset + offset).get)
      else None
    }

  def next(): Option[I] =
    if (this.isComplete) {
      None
    } else {
      val next = Some(this.input(this.offset).get)
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

class StringGaze(input: StringSource) extends Gaze[String](input) {
  // TODO location info might not update correctly when a Nibble fails?
  private var line: Int = 0
  private var lineOffset: Int = 0

  override def next(): Option[String] =
    if (this.isComplete) {
      None
    } else {
      val next = Some(this.input(this.offset).get)
      this.offset += 1
      this.lineOffset += 1
      if (next.value == "\n") {
        this.line += 1
        this.lineOffset = 0
      }
      next
    }
  def location: Location =
    Location(this.line, this.lineOffset)
}

enum Result[+T]:
  def map[U](f: T => U): Result[U] =
    this match {
      case EmptyMatch   => EmptyMatch
      case NoMatch      => NoMatch
      case Match(value) => Match(f(value))
    }
    // if (isEmpty) None else Some(f(this.get))
  def flatten[U](implicit ev: T <:< Result[U]): Result[U] =
    this match {
      case EmptyMatch   => EmptyMatch
      case NoMatch      => NoMatch
      case Match(value) => ev(value)
    }
  def flatMap[U](f: T => Result[U]): Result[U] =
    this match {
      case EmptyMatch   => EmptyMatch
      case NoMatch      => NoMatch
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
      case Result.NoMatch         => Result.NoMatch
      case Result.Match[O](value) => Result.Match(f(value))
      case Result.EmptyMatch      => Result.EmptyMatch
    }
  }

  final def as[NO](value: NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
    this.apply(gaze) match {
      case Result.NoMatch                      => Result.NoMatch
      case Result.Match(_) | Result.EmptyMatch => Result.Match(value)
    }
  }
}
