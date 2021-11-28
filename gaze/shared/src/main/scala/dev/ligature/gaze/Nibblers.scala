/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import dev.ligature.gaze.Gaze
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

/** A Nibbler that takes a single item.
  */
def take[I](toMatch: I): Nibbler[I, I] = { (gaze) =>
  {
    gaze.next() match {
      case Some(i) =>
        if (toMatch == i) { Some(List(toMatch)) }
        else { None }
      case None => None
    }
  }
}

def takeAll[I](
    nibblers: Nibbler[I, I]*
): Nibbler[I, I] = { (gaze: Gaze[I]) =>
  {
    val results = ArrayBuffer[I]()
    val res = nibblers.forall { nibbler =>
      val res = gaze.attempt(nibbler)
      res match {
        case Some(res) => {
          results.appendAll(res)
          true
        }
        case None => {
          false
        }
      }
    }
    if (res) {
      Some(results.toSeq)
    } else {
      None
    }
  }
}

def takeAllGrouped[I](
    nibblers: Nibbler[I, I]*
): Nibbler[I, Seq[I]] = { (gaze: Gaze[I]) =>
  {
    val results = ArrayBuffer[Seq[I]]()
    val res = nibblers.forall { nibbler =>
      val res = gaze.attempt(nibbler)
      res match {
        case Some(res) => {
          results.append(res)
          true
        }
        case None => {
          false
        }
      }
    }
    if (res) {
      Some(results.toList)
    } else {
      None
    }
  }
}

def takeString(toMatch: String): Nibbler[Char, Char] = {
  //    let graphemes = to_match.graphemes(true).collect::<Vec<&str>>();
  val chars = toMatch.toVector
  return (gaze) => {
    var offset = 0
    var matched = true
    while (matched && offset < chars.length) {
      val nextChar = gaze.next()
      nextChar match {
        case Some(c) => {
          if (chars(offset) == c) {
            offset += 1;
          } else {
            matched = false
          }
        }
        case None => {
          matched = false
        }
      }
    }
    if (matched) {
      Some(chars)
    } else {
      None
    }
  }
}

def takeUntil[I](toMatch: I): Nibbler[I, I] = {
  return (gaze) => {
    val result = ArrayBuffer[I]()
    var matched = false
    while (!matched) {
      val next = gaze.peek()
      next match {
        case Some(v) => {
          if (v == toMatch) {
            matched = true
          } else {
            gaze.next()
            result.append(v)
          }
        }
        case None => {
          matched = true
        }
      }
    }
    Some(result.toSeq)
  }
}

def filter[I](
    predicate: (item: I) => Boolean,
    nibbler: Nibbler[I, I]
): Nibbler[I, I] = { (gaze: Gaze[I]) =>
  {
    var matched = false
    var result: Option[Seq[I]] = None
    while (!matched) {
      gaze.peek() match {
        case None => {
          matched = true
          result = None
        }
        case Some(value) => {
          if (predicate(value)) {
            matched = true
            gaze.attempt(nibbler) match {
              case None        => result = None
              case Some(value) => result = Some(value)
            }
          } else {
            gaze.next()
          }
        }
      }
    }
    result
  }
}

def takeWhile[I](
    predicate: (toMatch: I) => Boolean
): Nibbler[I, I] = {
  return (gaze: Gaze[I]) => {
    val res = ArrayBuffer[I]()
    var matched = true
    var continue = true
    while (continue) {
      val peek = gaze.peek();

      peek match {
        case Some(c) => {
          if (predicate(c)) {
            gaze.next();
            res += c;
          } else if (res.length == 0) {
            matched = false
            continue = false
          } else {
            continue = false
          }
        }
        case None => {
          if (res.length == 0) {
            matched = false
            continue = false
          } else {
            continue = false
          }
        }
      }
    }
    if (matched) {
      Some(res.toSeq)
    } else {
      None
    }
  }
}

def optional[I](nibbler: Nibbler[I, I]): Nibbler[I, I] = { (gaze: Gaze[I]) =>
  {
    gaze.attempt(nibbler) match {
      case res: Some[_] => res
      case None         => Some(Seq())
    }
  }
}

def takeCharacters(chars: Char*): Nibbler[Char, Char] = takeWhile {
  chars.contains(_)
}

def takeFirst[I](
    nibblers: Nibbler[I, I]*
): Nibbler[I, I] = { (gaze: Gaze[I]) =>
  {
    var finalRes: Option[Seq[I]] = None
    val nibbler = nibblers.find { nibbler =>
      finalRes = gaze.attempt(nibbler)
      finalRes.isDefined
    }
    finalRes
  }
}

def repeat[I](
    nibbler: Nibbler[I, I]
): Nibbler[I, I] = { (gaze: Gaze[I]) =>
  {
    val allMatches = ArrayBuffer[I]()
    var continue = true
    while (!gaze.isComplete() && continue) {
      gaze.attempt(nibbler) match {
        case None    => continue = false
        case Some(v) => allMatches.appendAll(v)
      }
    }
    if (allMatches.isEmpty) {
      None
    } else {
      Some(allMatches.toSeq)
    }
  }
}

def between[I](
    wrapper: Nibbler[I, I],
    content: Nibbler[I, I]
) = takeAllGrouped(wrapper, content, wrapper).map(_(1))

def between[I](
    open: Nibbler[I, I],
    content: Nibbler[I, I],
    close: Nibbler[I, I]
) = takeAllGrouped(open, content, close).map(_(1))
