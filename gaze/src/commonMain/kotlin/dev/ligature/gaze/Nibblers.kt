/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

/** A Nibbler that takes a single item. */
fun <I> take(toMatch: I): Nibbler<I, I> = { gaze ->
  when (val next = gaze.next()) {
    null -> null
    else -> {
      if (toMatch == next) {
        listOf(toMatch)
      } else {
        null
      }
    }
  }
}

/** A Nibbler that takes a single item if the condition passed is true. */
fun <I> takeCond(cond: (I) -> Boolean): Nibbler<I, I> = { gaze ->
  when (val next = gaze.next()) {
    null -> null
    else ->
        if (cond(next)) {
          listOf(next)
        } else {
          null
        }
  }
}

fun <I, O> takeCondMap(cond: (I) -> O?): Nibbler<I, O> = { gaze ->
  when (val next = gaze.next()) {
    null -> null
    else -> {
      when (val res = cond(next)) {
        null -> null
        else -> listOf(res)
      }
    }
  }
}

/** A Nibbler that matches multiple Nibblers in order. */
fun <I, O> takeAll(vararg nibblers: Nibbler<I, O>): Nibbler<I, O> = { gaze: Gaze<I> ->
  val results = mutableListOf<O>()
  val res =
      nibblers.all { nibbler ->
        when (val res = gaze.attempt(nibbler)) {
          null -> false
          else -> {
            results.addAll(res)
            true
          }
        }
      }
  if (res) {
    results.toList()
  } else {
    null
  }
}

/** A Nibbler that matches multiple Nibblers in order but keeps each matching set in a List. */
fun <I, O> takeAllGrouped(vararg nibblers: Nibbler<I, O>): Nibbler<I, List<O>> = { gaze: Gaze<I> ->
  val results = mutableListOf<List<O>>()
  val res =
      nibblers.all { nibbler ->
        when (val res = gaze.attempt(nibbler)) {
          null -> false
          else -> {
            results.add(res)
            true
          }
        }
      }
  if (res) {
    results.toList()
  } else {
    null
  }
}

fun takeString(toMatch: String): Nibbler<Char, Char> {
  //    let graphemes = to_match.graphemes(true).collect::<Vec<&str>>();
  val chars = toMatch.toList()
  return { gaze ->
    var offset = 0
    var matched = true
    while (matched && offset < chars.size) {
      when (val nextChar = gaze.next()) {
        null -> matched = false
        else ->
            if (chars[offset] == nextChar) {
              offset += 1
            } else {
              matched = false
            }
      }
    }
    if (matched) {
      chars
    } else {
      null
    }
  }
}

/** A Nibbler that takes until a specific element is matched. */
fun <I> takeUntil(toMatch: I): Nibbler<I, I> = { gaze ->
  val result = mutableListOf<I>()
  var matched = false
  while (!matched) {
    when (val next = gaze.peek()) {
      null -> matched = true
      else ->
          if (next == toMatch) {
            matched = true
          } else {
            gaze.next()
            result.add(next)
          }
    }
  }
  result.toList()
}

// TODO needs tests
fun <I> takeUntil(toMatch: Nibbler<I, I>): Nibbler<I, I> = { gaze ->
  val result = mutableListOf<I>()
  var matched = false
  while (!matched) {
    when (val next = gaze.peek()) {
      null -> matched = true
      else -> {
        when (gaze.check(toMatch)) {
          null -> {
            gaze.next()
            result.add(next)
          }
          else -> matched = true
        }
      }
    }
  }
  result.toList()
}

fun <I> takeWhile(predicate: (toMatch: I) -> Boolean): Nibbler<I, I> = { gaze: Gaze<I> ->
  val res = mutableListOf<I>()
  var matched = true
  var proceed = true
  while (proceed) {
    when (val peek = gaze.peek()) {
      null ->
          if (res.size == 0) {
            matched = false
            proceed = false
          } else {
            proceed = false
          }
      else -> {
        if (predicate(peek)) {
          gaze.next()
          res += peek
        } else if (res.size == 0) {
          matched = false
          proceed = false
        } else {
          proceed = false
        }
      }
    }
  }
  if (matched) {
    res.toList()
  } else {
    null
  }
}

fun <I, O> optional(nibbler: Nibbler<I, O>): Nibbler<I, O> = { gaze: Gaze<I> ->
  when (val res = gaze.attempt(nibbler)) {
    null -> listOf()
    else -> res
  }
}

fun takeCharacters(vararg chars: Char): Nibbler<Char, Char> = takeWhile { chars.contains(it) }

fun <I, O> takeFirst(vararg nibblers: Nibbler<I, O>): Nibbler<I, O> = { gaze: Gaze<I> ->
  var finalRes: List<O>? = null
  nibblers.find { nibbler ->
    finalRes = gaze.attempt(nibbler)
    finalRes != null
  }
  finalRes
}

fun <I, O> repeat(nibbler: Nibbler<I, O>): Nibbler<I, O> = { gaze: Gaze<I> ->
  val allMatches = mutableListOf<O>()
  var proceed = true
  while (!gaze.isComplete && proceed) when (val v = gaze.attempt(nibbler)) {
    null -> proceed = false
    else -> allMatches.addAll(v)
  }
  if (allMatches.isEmpty()) {
    null
  } else {
    allMatches.toList()
  }
}

fun <I, O> between(wrapper: Nibbler<I, O>, content: Nibbler<I, O>) =
    takeAllGrouped(wrapper, content, wrapper).map { it[1] }

fun <I, O> between(open: Nibbler<I, O>, content: Nibbler<I, O>, close: Nibbler<I, O>) =
    takeAllGrouped(open, content, close).map { it[1] }
