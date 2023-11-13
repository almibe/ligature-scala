/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  between,
  take,
  takeAll,
  takeAllGrouped,
  takeCharacters,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  optional
}

object LigNibblers {
  val whiteSpaceNibbler = takeCharacters(' ', '\t')
  val whiteSpaceAndNewLineNibbler = takeAll(takeFirst(takeString(" "), takeString("\n"), takeString("\r\n"), takeString("\t")))
  val numberNibbler =
    takeAll(optional(take('-')), takeCharacters(('0' to '9').toSeq*))

  val identifierNibbler = between(
    takeString("<"),
    takeWhile { c =>
      "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
    },
    takeString(">")
  )

  val stringContentNibbler: Nibbler[Char, Char] =
    (gaze: Gaze[Char]) => {
      // Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
      val commandChars = 0x00.toChar to 0x1f.toChar
      val validHexChar = (c: Char) =>
        ('0' to '9' contains c) || ('a' to 'f' contains c) || ('A' to 'F' contains c)
      val hexNibbler = takeWhile(validHexChar)

      var sb = ArrayBuffer[Char]()
      var offset = 0 // TODO delete
      var fail = false
      var complete = false
      while (!complete && !fail && gaze.peek().isDefined) {
        val c = gaze.next().get
        if (commandChars.contains(c)) {
          fail = true
        } else if (c == '"') {
          complete = true
        } else if (c == '\\') {
          sb.append(c)
          gaze.next() match {
            case None => fail = true
            case Some(c) =>
              c match {
                case '\\' | '"' | 'b' | 'f' | 'n' | 'r' | 't' => sb.append(c)
                case 'u' =>
                  sb.append(c)
                  val res = gaze.attempt(hexNibbler)
                  res match {
                    case None => fail = true
                    case Some(res) =>
                      if (res.length == 4) {
                        sb.appendAll(res)
                      } else {
                        fail = true
                      }
                  }
                case _ =>
                  fail = true
              }
          }
        } else {
          sb.append(c)
        }
      }
      if (fail) {
        None
      } else {
        Some(sb.toSeq)
      }
    }

  val stringNibbler = takeAllGrouped(
    takeString("\""),
    stringContentNibbler
  ) // TODO should be a between but stringContentNibbler consumes the last " currently

  private val validPrefixName =
    (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList.appended('_')

  // val identifierIdGenNibbler = ??? //matches <{}> <prefix:{}> <{}:postfix> <pre:{}:post> etc
  // val prefixedIdentifierNibbler = ??? //matches prefix:value:after:prefix
  // val prefixedIdGenNibbler = ??? // matches prefix:value:after:prefix:{}
}
