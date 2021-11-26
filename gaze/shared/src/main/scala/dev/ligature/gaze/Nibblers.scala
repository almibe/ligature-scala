/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import dev.ligature.gaze.Gaze
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import dev.ligature.gaze.NoMatch

trait NoMatch
object NoMatch extends NoMatch

def takeString(toMatch: String): Nibbler[Char, NoMatch, String] = {
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
      Right(toMatch)
    } else {
      Left(NoMatch)
    }
  }
}

def takeUntil(toMatch: Char): Nibbler[Char, NoMatch, String] = {
  return (gaze) => {
    val result = mutable.StringBuilder()
    var matched = false
    while (!matched) {
      val nextChar = gaze.peek()
      nextChar match {
        case Some(c) => {
          if (c == toMatch) {
            matched = true
          } else {
            gaze.next()
            result.append(c)
          }
        }
        case None => {
          matched = true
        }
      }
    }
    Right(result.toString)
  }
}

def filter[I, O](
    predicate: (item: I) => Boolean,
    nibbler: Nibbler[I, NoMatch, O]
): Nibbler[I, NoMatch, Option[O]] = { (gaze: Gaze[I]) =>
  {
    var matched = false
    var result: Either[NoMatch, Option[O]] = Left(NoMatch)
    while (!matched) {
      gaze.peek() match {
        case None => {
          matched = true
          result = Right(None)
        }
        case Some(value) => {
          if (predicate(value)) {
            matched = true
            gaze.attempt(nibbler) match {
              case Left(_)      => result = Left(NoMatch)
              case Right(value) => result = Right(Some(value))
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

def takeWhile(
    predicate: (toMatch: Char) => Boolean
): Nibbler[Char, NoMatch, String] = {
  return (gaze: Gaze[Char]) => {
    val res = StringBuilder()
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
            // return Right(res);
          }
        }
        case None => {
          if (res.length == 0) {
            matched = false
            continue = false
          } else {
            continue = false
            // return Right(res);
          }
        }
      }
    }
    if (matched) {
      Right(res.toString())
    } else {
      Left(NoMatch)
    }
  }
}

def takeCharacters(chars: Char*): Nibbler[Char, NoMatch, String] = takeWhile {
  chars.contains(_)
}

def matchNext[I](predicate: I => Boolean): Nibbler[I, NoMatch, I] = {
  (gaze: Gaze[I]) =>
    {
      gaze.next() match {
        case None => Left(NoMatch)
        case Some(value) => {
          if (predicate(value)) {
            Right(value)
          } else {
            Left(NoMatch)
          }
        }
      }
    }
}

def takeFirst[I, O](
    nibblers: Nibbler[I, NoMatch, O]*
): Nibbler[I, NoMatch, O] = { (gaze: Gaze[I]) =>
  {
    var finalRes: Either[NoMatch, O] = Left(NoMatch)
    val nibbler = nibblers.find { nibbler =>
      finalRes = gaze.attempt(nibbler)
      finalRes.isRight
    }
    finalRes
  }
}

def takeAll[I, O](
    nibblers: Nibbler[I, NoMatch, O]*
): Nibbler[I, NoMatch, List[O]] = { (gaze: Gaze[I]) =>
  {
    val results = ArrayBuffer[O]()
    val res = nibblers.forall { nibbler =>
      val res = gaze.attempt(nibbler)
      res match {
        case Right(res) => {
          results.append(res)
          true
        }
        case Left(e) => {
          false
        }
      }
    }
    if (res) {
      Right(results.toList)
    } else {
      Left(NoMatch)
    }
  }
}

def repeat[I, O](
    nibbler: Nibbler[I, NoMatch, O]
): Nibbler[I, NoMatch, List[O]] = { (gaze: Gaze[I]) =>
  {
    val allMatches = ArrayBuffer[O]()
    var continue = true
    while (!gaze.isComplete() && continue) {
      gaze.attempt(nibbler) match {
        case Left(_)  => continue = false
        case Right(v) => allMatches.append(v)
      }
    }
    if (gaze.isComplete()) {
      Right(allMatches.toList)
    } else if (allMatches.isEmpty) {
      Left(NoMatch)
    } else {
      Right(allMatches.toList)
    }
  }
}

def between[I, O](
    wrapper: Nibbler[I, NoMatch, O],
    content: Nibbler[I, NoMatch, O]
) = takeAll(wrapper, content, wrapper).map(_(1))

def between[I, O](
    open: Nibbler[I, NoMatch, O],
    content: Nibbler[I, NoMatch, O],
    close: Nibbler[I, NoMatch, O]
) = takeAll(open, content, close).map(_(1))
