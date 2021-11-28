/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import dev.ligature.gaze.Gaze
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

/**
 * A Nibbler that takes a single item.
 */
def take[I](toMatch: I): Nibbler[I, I] = {
  (gaze) => {
    gaze.next() match {
      case Some(i) => if (toMatch == i) { Some(List(toMatch)) } else { None }
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

// def takeUntil(toMatch: Char): Nibbler[Char] = {
//   return (gaze) => {
//     val result = mutable.StringBuilder()
//     var matched = false
//     while (!matched) {
//       val nextChar = gaze.peek()
//       nextChar match {
//         case Some(c) => {
//           if (c == toMatch) {
//             matched = true
//           } else {
//             gaze.next()
//             result.append(c)
//           }
//         }
//         case None => {
//           matched = true
//         }
//       }
//     }
//     Some(result.toString)
//   }
// }

// def filter[I](
//     predicate: (item: I) => Boolean,
//     nibbler: Nibbler[I]
// ): Nibbler[I] = { (gaze: Gaze[I]) =>
//   {
//     var matched = false
//     var result: Option[I] = None
//     while (!matched) {
//       gaze.peek() match {
//         case None => {
//           matched = true
//           result = None
//         }
//         case Some(value) => {
//           if (predicate(value)) {
//             matched = true
//             gaze.attempt(nibbler) match {
//               case None      => result = None
//               case Some(value) => result = Some(value)
//             }
//           } else {
//             gaze.next()
//           }
//         }
//       }
//     }
//     result
//   }
// }

// def takeWhile(
//     predicate: (toMatch: Char) => Boolean
// ): Nibbler[Char, String] = {
//   return (gaze: Gaze[Char]) => {
//     val res = StringBuilder()
//     var matched = true
//     var continue = true
//     while (continue) {
//       val peek = gaze.peek();

//       peek match {
//         case Some(c) => {
//           if (predicate(c)) {
//             gaze.next();
//             res += c;
//           } else if (res.length == 0) {
//             matched = false
//             continue = false
//           } else {
//             continue = false
//             // return Some(res);
//           }
//         }
//         case None => {
//           if (res.length == 0) {
//             matched = false
//             continue = false
//           } else {
//             continue = false
//             // return Some(res);
//           }
//         }
//       }
//     }
//     if (matched) {
//       Some(res.toString())
//     } else {
//       None
//     }
//   }
// }

// def optional[I](nibbler: Nibbler[I, I]): Nibbler[I, Option[I]] = {
//   (gaze: Gaze[I]) => {
//     ???
//   }
// }

// def takeCharacters(chars: Char*): Nibbler[Char, String] = takeWhile {
//   chars.contains(_)
// }

// def matchNext[I](predicate: I => Boolean): Nibbler[I, I] = {
//   (gaze: Gaze[I]) =>
//     {
//       gaze.next() match {
//         case None => None
//         case Some(value) => {
//           if (predicate(value)) {
//             Some(value)
//           } else {
//             None
//           }
//         }
//       }
//     }
// }

// def takeFirst[I](
//     nibblers: Nibbler[I]*
// ): Nibbler[I] = { (gaze: Gaze[I]) =>
//   {
//     var finalRes: Option[O] = None
//     val nibbler = nibblers.find { nibbler =>
//       finalRes = gaze.attempt(nibbler)
//       finalRes.isDefined
//     }
//     finalRes
//   }
// }

// def repeat[I, O](
//     nibbler: Nibbler[I, O]
// ): Nibbler[I, List[O]] = { (gaze: Gaze[I]) =>
//   {
//     val allMatches = ArrayBuffer[O]()
//     var continue = true
//     while (!gaze.isComplete() && continue) {
//       gaze.attempt(nibbler) match {
//         case None  => continue = false
//         case Some(v) => allMatches.append(v)
//       }
//     }
//     if (gaze.isComplete()) {
//       Some(allMatches.toList)
//     } else if (allMatches.isEmpty) {
//       None
//     } else {
//       Some(allMatches.toList)
//     }
//   }
// }

// def between[I, O](
//     wrapper: Nibbler[I, O],
//     content: Nibbler[I, O]
// ) = takeAll(wrapper, content, wrapper).map(_(1))

// def between[I, O](
//     open: Nibbler[I, O],
//     content: Nibbler[I, O],
//     close: Nibbler[I, O]
// ) = takeAll(open, content, close).map(_(1))
