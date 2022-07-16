/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.Option
import arrow.core.Some
import arrow.core.None
import arrow.core.none

data class Location(val line: Int, val lineOffset: Int)

class Gaze<I>(private val input: List<I>) {
  private var offset: Int = 0
  private var line: Int = 0
  private var lineOffset: Int = 0

  companion object {
    fun from(
      text: String
    ): Gaze<Char> = // TODO eventually handle unicode better and make this Gaze[String]
      Gaze(text.toCharArray().toList())
  }

  val isComplete: Boolean
    get() = this.offset >= this.input.size

  fun peek(): Option<I> =
    if (this.isComplete) {
      none()
    } else {
      Some(this.input[this.offset])
    }

  fun next(): Option<I> =
    if (this.isComplete) {
      none()
    } else {
      val next = Some(this.input[this.offset])
      this.offset += 1
      this.lineOffset += 1
      if (next.value == "\n") {
        this.line += 1
        this.lineOffset = 0
      }
      next
    }

  // TODO needs tests
  fun <O>check(nibbler: Nibbler<I, O>): Option<List<O>> {
    val startOfThisLoop = this.offset

    return when(val res = nibbler(this)) {
      is Some -> {
        this.offset = startOfThisLoop
        res
      }
      is None -> {
        this.offset = startOfThisLoop
        res
      }
    }
  }

  fun <O>attempt(nibbler: Nibbler<I, O>): Option<List<O>> {
    val startOfThisLoop = this.offset

    return when(val res = nibbler(this)) {
      is Some -> res
      is None -> {
        this.offset = startOfThisLoop
        res
      }
    }
  }
  
  val location: Location
    get() = Location(this.line, this.lineOffset)
}

typealias Nibbler<I, O> = (Gaze<I>) -> Option<List<O>>

fun <I, O, NO>Nibbler<I, O>.map(f: (List<O>) -> List<NO>): Nibbler<I, NO> = { gaze ->
  when(val results = this(gaze)) {
    is None -> none()
    is Some -> Some(f(results.value))
  }
}

//abstract class Nibbler<I, O>: (Gaze<I>) -> Option<List<O>> {
//  abstract fun apply(gaze: Gaze<I>): Option<List<O>>

//  fun <NO>map(f: (List<O>) -> List<NO>): Nibbler<I, NO> = { gaze ->
//    when(val results = this.apply(gaze)) {
//      is None -> none()
//      is Some -> Some(f(results.value))
//    }
//  }

//  final fun as[NO](value: NO): Nibbler[I, NO] = { (gaze: Gaze[I]) =>
//    this.apply(gaze) match {
//      case None    => None
//      case Some(_) => Some(List(value))
//    }
//  }
//}
