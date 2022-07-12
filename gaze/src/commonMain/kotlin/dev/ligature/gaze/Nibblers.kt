/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import dev.ligature.gaze.Gaze
import arrow.core.Some
import arrow.core.none
import arrow.core.None

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

//def takeString(toMatch: String): Nibbler[Char, Char] = {
//  //    let graphemes = to_match.graphemes(true).collect::<Vec<&str>>();
//  val chars = toMatch.toVector
//  return gaze => {
//    var offset = 0
//    var matched = true
//    while (matched && offset < chars.length) {
//      val nextChar = gaze.next()
//      nextChar match {
//        case Some(c) =>
//          if (chars(offset) == c) {
//            offset += 1;
//          } else {
//            matched = false
//          }
//        case None =>
//          matched = false
//      }
//    }
//    if (matched) {
//      Some(chars)
//    } else {
//      None
//    }
//  }
//}
//
//def takeUntil[I](toMatch: I): Nibbler[I, I] =
//  return gaze => {
//    val result = ArrayBuffer[I]()
//    var matched = false
//    while (!matched) {
//      val next = gaze.peek()
//      next match {
//        case Some(v) =>
//          if (v == toMatch) {
//            matched = true
//          } else {
//            gaze.next()
//            result.append(v)
//          }
//        case None =>
//          matched = true
//      }
//    }
//    Some(result.toSeq)
//  }
//
////TODO needs tests
//def takeUntil[I](toMatch: Nibbler[I, I]): Nibbler[I, I] =
//  return gaze => {
//    val result = ArrayBuffer[I]()
//    var matched = false
//    while (!matched) {
//      val next = gaze.peek()
//      next match {
//        case Some(v) =>
//          val check = gaze.check(toMatch)
//          check match {
//            case Some(_) => matched = true
//            case None =>
//              gaze.next()
//              result.append(v)
//          }
//        case None =>
//          matched = true
//      }
//    }
//    Some(result.toSeq)
//  }
//
//def takeWhile[I](
//    predicate: (toMatch: I) => Boolean
//): Nibbler[I, I] =
//  return (gaze: Gaze[I]) => {
//    val res = ArrayBuffer[I]()
//    var matched = true
//    var continue = true
//    while (continue) {
//      val peek = gaze.peek();
//
//      peek match {
//        case Some(c) =>
//          if (predicate(c)) {
//            gaze.next();
//            res += c;
//          } else if (res.length == 0) {
//            matched = false
//            continue = false
//          } else {
//            continue = false
//          }
//        case None =>
//          if (res.length == 0) {
//            matched = false
//            continue = false
//          } else {
//            continue = false
//          }
//      }
//    }
//    if (matched) {
//      Some(res.toSeq)
//    } else {
//      None
//    }
//  }
//
//def optional[I, O](nibbler: Nibbler[I, O]): Nibbler[I, O] = { (gaze: Gaze[I]) =>
//  gaze.attempt(nibbler) match {
//    case res: Some[_] => res
//    case None         => Some(Seq())
//  }
//}
//
//def takeCharacters(chars: Char*): Nibbler[Char, Char] = takeWhile {
//  chars.contains(_)
//}
//
//def takeFirst[I, O](
//    nibblers: Nibbler[I, O]*
//): Nibbler[I, O] = { (gaze: Gaze[I]) =>
//  var finalRes: Option[Seq[O]] = None
//  val nibbler = nibblers.find { nibbler =>
//    finalRes = gaze.attempt(nibbler)
//    finalRes.isDefined
//  }
//  finalRes
//}
//
//def repeat[I, O](
//    nibbler: Nibbler[I, O]
//): Nibbler[I, O] = { (gaze: Gaze[I]) =>
//  val allMatches = ArrayBuffer[O]()
//  var continue = true
//  while (!gaze.isComplete && continue)
//    gaze.attempt(nibbler) match {
//      case None    => continue = false
//      case Some(v) => allMatches.appendAll(v)
//    }
//  if (allMatches.isEmpty) {
//    None
//  } else {
//    Some(allMatches.toSeq)
//  }
//}
//
//def between[I](
//    wrapper: Nibbler[I, I],
//    content: Nibbler[I, I]
//) = takeAllGrouped(wrapper, content, wrapper).map(_(1))
//
//def between[I](
//    open: Nibbler[I, I],
//    content: Nibbler[I, I],
//    close: Nibbler[I, I]
//) = takeAllGrouped(open, content, close).map(_(1))
