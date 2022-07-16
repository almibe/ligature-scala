/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import arrow.core.Some
import arrow.core.none
import arrow.core.None
import arrow.core.Option

/** A Nibbler that takes a single item.
  */
fun <I>take(toMatch: I): Nibbler<I, I> = { gaze ->
  when(val next = gaze.next()) {
    is Some -> {
      if (toMatch == next.value) { Some(listOf(toMatch)) }
      else { none() }
    }
    is None -> none()
  }
}

/** A Nibbler that takes a single item if the condition passed is true.
  */
fun <I>takeCond(cond: (I) -> Boolean): Nibbler<I, I> = { gaze ->
  when(val next = gaze.next()) {
    is Some ->
      if (cond(next.value)) { Some(listOf(next.value)) }
      else { none() }
    is None -> none()
  }
}

/**
 * A Nibbler that matches multiple Nibblers in order.
 */
fun <I, O>takeAll(
    vararg nibblers: Nibbler<I, O>
): Nibbler<I, O> = { gaze: Gaze<I> ->
  val results = mutableListOf<O>()
  val res = nibblers.all { nibbler ->
    val res = gaze.attempt(nibbler)
    when(res) {
      is Some -> {
        results.addAll(res.value)
        true
      }
      is None -> false
    }
  }
  if (res) {
    Some(results.toList())
  } else {
    none()
  }
}

/**
 * A Nibbler that matches multiple Nibblers in order but
 * keeps each matching set in a List.
 */
fun <I, O>takeAllGrouped(
    vararg nibblers: Nibbler<I, O>
): Nibbler<I, List<O>> = { gaze: Gaze<I> ->
  val results = mutableListOf<List<O>>()
  val res = nibblers.all { nibbler ->
    when(val res = gaze.attempt(nibbler)) {
      is Some -> {
        results.add(res.value)
        true
      }
      is None -> false
    }
  }
  if (res) {
    Some(results.toList())
  } else {
    none()
  }
}

fun takeString(toMatch: String): Nibbler<Char, Char> {
  //    let graphemes = to_match.graphemes(true).collect::<Vec<&str>>();
  val chars = toMatch.toList()
  return { gaze ->
    var offset = 0
    var matched = true
    while (matched && offset < chars.size) {
      val nextChar = gaze.next()
      when(nextChar) {
        is Some ->
          if (chars[offset] == nextChar.value) {
            offset += 1
          } else {
            matched = false
          }
        is None -> matched = false
      }
    }
    if (matched) {
      Some(chars)
    } else {
      none()
    }
  }
}

/**
 * A Nibbler that takes until a specific element is matched.
 */
fun <I>takeUntil(toMatch: I): Nibbler<I, I> =
  { gaze ->
    val result = mutableListOf<I>()
    var matched = false
    while (!matched) {
      val next = gaze.peek()
      when(next) {
        is Some ->
          if (next.value == toMatch) {
            matched = true
          } else {
            gaze.next()
            result.add(next.value)
          }
        is None -> matched = true
      }
    }
    Some(result.toList())
  }

//TODO needs tests
fun <I>takeUntil(toMatch: Nibbler<I, I>): Nibbler<I, I> =
  { gaze ->
    val result = mutableListOf<I>()
    var matched = false
    while (!matched) {
      val next = gaze.peek()
      when(next) {
        is Some -> {
          val check = gaze.check(toMatch)
          when(check) {
            is Some -> matched = true
            is None -> {
              gaze.next()
              result.add(next.value)
            }
          }
        }
        is None -> matched = true
      }
    }
    Some(result.toList())
  }


fun <I>takeWhile(
    predicate: (toMatch: I) -> Boolean
): Nibbler<I, I> =
  { gaze: Gaze<I> ->
    val res = mutableListOf<I>()
    var matched = true
    var proceed = true
    while (proceed) {
      when(val peek = gaze.peek()) {
        is Some -> {
          if (predicate(peek.value)) {
            gaze.next()
            res += peek.value
          } else if (res.size == 0) {
            matched = false
            proceed = false
          } else {
            proceed = false
          }
        }
        is None ->
          if (res.size == 0) {
            matched = false
            proceed = false
          } else {
            proceed = false
          }
      }
    }
    if (matched) {
      Some(res.toList())
    } else {
      none()
    }
  }

fun <I, O>optional(nibbler: Nibbler<I, O>): Nibbler<I, O> = { gaze: Gaze<I> ->
  when(val res = gaze.attempt(nibbler)) {
    is Some -> res
    is None -> Some(listOf())
  }
}

fun takeCharacters(vararg chars: Char): Nibbler<Char, Char> = takeWhile {
  chars.contains(it)
}

fun <I, O>takeFirst(
    vararg nibblers: Nibbler<I, O>
): Nibbler<I, O> = { gaze: Gaze<I> ->
  var finalRes: Option<List<O>> = none()
  nibblers.find { nibbler ->
    finalRes = gaze.attempt(nibbler)
    finalRes is Some
  }
  finalRes
}

fun <I, O>repeat(
    nibbler: Nibbler<I, O>
): Nibbler<I, O> = { gaze: Gaze<I> ->
  val allMatches = mutableListOf<O>()
  var proceed = true
  while (!gaze.isComplete && proceed)
    when(val v = gaze.attempt(nibbler)) {
      is None -> proceed = false
      is Some -> allMatches.addAll(v.value)
    }
  if (allMatches.isEmpty()) {
    none()
  } else {
    Some(allMatches.toList())
  }
}

fun <I>between(
    wrapper: Nibbler<I, I>,
    content: Nibbler<I, I>
) = takeAllGrouped(wrapper, content, wrapper).map { it[1] }

fun <I>between(
    open: Nibbler<I, I>,
    content: Nibbler<I, I>,
    close: Nibbler<I, I>
) = takeAllGrouped(open, content, close).map { it[1] }
