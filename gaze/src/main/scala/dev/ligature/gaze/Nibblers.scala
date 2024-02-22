/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import dev.ligature.gaze.Gaze
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/** A Nibbler that takes a single item.
  */
def take[I](toMatch: I): Nibbler[I, I] = { gaze =>
  gaze.next() match {
    case Some(i) =>
      if (toMatch == i) { Result.Match(toMatch) }
      else { Result.NoMatch }
    case None => Result.NoMatch
  }
}

/** A Nibbler that takes a single item if the condition passed is true.
  */
def takeCond[I](cond: (I) => Boolean): Nibbler[I, I] = { gaze =>
  gaze.next() match {
    case Some(i) =>
      if (cond(i)) { Result.Match(i) }
      else { Result.NoMatch }
    case None => Result.NoMatch
  }
}

def takeWhile[I](predicate: I => Boolean) = repeat(takeCond[I](predicate))

//def takeWhile[I](predicate: (I) => Boolean) = repeat[I, Seq[I]](takeCond[I](predicate))

// def takeWhile[I, O](
//     predicate: (toMatch: I) => Boolean
// ): Nibbler[I, Seq[O]] =
//   return (gaze: Gaze[I]) => {
//     val res = ArrayBuffer[I]()
//     var matched = true
//     var continue = true
//     while (continue) {
//       val peek = gaze.peek();

//       peek match {
//         case Result.Match(c) =>
//           if (predicate(c)) {
//             gaze.next();
//             res += c;
//           } else if (res.length == 0) {
//             matched = false
//             continue = false
//           } else {
//             continue = false
//           }
//         case Result.NoMatch =>
//           if (res.length == 0) {
//             matched = false
//             continue = false
//           } else {
//             continue = false
//           }
//         case Result.EmptyMatch => ???
//       }
//     }
//     if (matched) {
//       Result.Match(res.toSeq)
//     } else {
//       Result.NoMatch
//     }
//   }

/** Matches any of the Nibblers passed.
  */
def takeAny[I, O](
    nibblers: Nibbler[I, O]*
): Nibbler[I, Seq[O]] = repeat(takeFirst(nibblers*))

/** Matches all of the given Nibbles in order or fails.
  */
def takeAll[I, O](
    nibblers: Nibbler[I, O]*
): Nibbler[I, Seq[O]] = { (gaze: Gaze[I]) =>
  val results = ListBuffer[O]()
  val res = nibblers.forall { nibbler =>
    val res = gaze.attempt(nibbler)
    res match {
      case Result.Match(res) =>
        results += res
        true
      case Result.NoMatch =>
        false
      case Result.EmptyMatch => true
    }
  }
  if (res) {
    Result.Match(results.toSeq)
  } else {
    Result.NoMatch
  }
}

def takeString(toMatch: String): Nibbler[String, String] = {
  val chars = toMatch.toVector
  return gaze => {
    var offset = 0
    var matched = true
    while (matched && offset < chars.length) {
      val nextChar = gaze.next()
      nextChar match {
        case Some(c) =>
          if (chars(offset).toString() == c) {
            offset += 1;
          } else {
            matched = false
          }
        case None =>
          matched = false
      }
    }
    if (matched) {
      Result.Match(chars.mkString)
    } else {
      Result.NoMatch
    }
  }
}

def takeUntil[I](toMatch: I): Nibbler[I, Seq[I]] =
  return gaze => {
    val result = ArrayBuffer[I]()
    var matched = false
    while (!matched) {
      val next = gaze.peek()
      next match {
        case Some(v) =>
          if (v == toMatch) {
            matched = true
          } else {
            gaze.next()
            result.append(v)
          }
        case None =>
          matched = true
      }
    }
    Result.Match(result.toSeq)
  }

//TODO needs tests
def takeUntil[I](toMatch: Nibbler[I, I]): Nibbler[I, Seq[I]] =
  return gaze => {
    val result = ArrayBuffer[I]()
    var matched = false
    while (!matched) {
      val next = gaze.peek()
      next match {
        case Some(v) =>
          val check = gaze.check(toMatch)
          check match {
            case Result.Match(_) => matched = true
            case Result.NoMatch =>
              gaze.next()
              result.append(v)
            case Result.EmptyMatch => ???
          }
        case None =>
          matched = true
      }
    }
    Result.Match(result.toSeq)
  }

def optional[I, O](nibbler: Nibbler[I, O]): Nibbler[I, O] = { (gaze: Gaze[I]) =>
  gaze.attempt(nibbler) match {
    case res: Result.Match[_]               => res
    case Result.NoMatch | Result.EmptyMatch => Result.EmptyMatch
  }
}

def optionalSeq[I, O](nibbler: Nibbler[I, Seq[O]]): Nibbler[I, Seq[O]] = { (gaze: Gaze[I]) =>
  gaze.attempt(nibbler) match {
    case res: Result.Match[_]               => res
    case Result.NoMatch | Result.EmptyMatch => Result.Match(Seq())
  }
}

def takeFirst[I, O](
    nibblers: Nibbler[I, O]*
): Nibbler[I, O] = { (gaze: Gaze[I]) =>
  var finalRes: Result[O] = Result.NoMatch
  val nibbler = nibblers.find { nibbler =>
    finalRes = gaze.attempt(nibbler)
    finalRes match {
      case Result.EmptyMatch | Result.Match(_) => true
      case Result.NoMatch                      => false
    }
  }
  finalRes
}

def repeat[I, O](
    nibbler: Nibbler[I, O]
): Nibbler[I, Seq[O]] = { (gaze: Gaze[I]) =>
  val allMatches = ArrayBuffer[O]()
  var continue = true
  while (!gaze.isComplete && continue)
    gaze.attempt(nibbler) match {
      case Result.NoMatch    => continue = false
      case Result.Match(v)   => allMatches += v
      case Result.EmptyMatch => continue = true
    }
  if (allMatches.isEmpty) {
    Result.NoMatch
  } else {
    Result.Match(allMatches.toSeq)
  }
}

def repeatSep[I, O](
    nibbler: Nibbler[I, O],
    seperator: I
): Nibbler[I, Seq[O]] = { (gaze: Gaze[I]) =>
  val allMatches = ArrayBuffer[O]()
  var continue = true
  while (!gaze.isComplete && continue) {
    gaze.attempt(nibbler) match {
      case Result.NoMatch    => continue = false
      case Result.Match(v)   => allMatches += v
      case Result.EmptyMatch => continue = true
    }
    if (gaze.peek() == Some(seperator)) {
      gaze.next()
    } else {
      continue = false
    }
  }
  if (allMatches.isEmpty) {
    Result.NoMatch
  } else {
    Result.Match(allMatches.toSeq)
  }
}

def between[I, O](
    wrapper: Nibbler[I, O],
    content: Nibbler[I, O]
) = takeAll(wrapper, content, wrapper).map(_(1))

def between[I, O](
    open: Nibbler[I, O],
    content: Nibbler[I, O],
    close: Nibbler[I, O]
) = takeAll(open, content, close).map(_(1))

// Concat a Seq of Strings together.
def concat(nibbler: Nibbler[String, Seq[String]]): Nibbler[String, String] = {
  (gaze: Gaze[String]) =>
    gaze.attempt(nibbler) match {
      case Result.EmptyMatch   => Result.EmptyMatch
      case Result.NoMatch      => Result.NoMatch
      case Result.Match(value) => Result.Match(value.mkString)
    }
}

// Wraps a value in a Seq.
def seq[I, O](nibbler: Nibbler[I, O]): Nibbler[I, Seq[O]] = { (gaze: Gaze[I]) =>
  gaze.attempt(nibbler) match {
    case Result.EmptyMatch   => Result.EmptyMatch
    case Result.NoMatch      => Result.NoMatch
    case Result.Match(value) => Result.Match(Seq(value))
  }
}

// Removes one layer of Seqs.
def flatten[I, O](nibbler: Nibbler[I, Seq[Seq[O]]]): Nibbler[I, Seq[O]] = { (gaze: Gaze[I]) =>
  gaze.attempt(nibbler) match {
    case Result.EmptyMatch => Result.EmptyMatch
    case Result.NoMatch    => Result.NoMatch
    case Result.Match(value) =>
      Result.Match(value.flatten)
  }
}
