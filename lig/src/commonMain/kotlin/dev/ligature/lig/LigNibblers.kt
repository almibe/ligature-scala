/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.gaze.Gaze
import dev.ligature.gaze.Nibbler
import dev.ligature.gaze.between
import dev.ligature.gaze.takeAll
import dev.ligature.gaze.takeAllGrouped
import dev.ligature.gaze.takeCharacters
import dev.ligature.gaze.takeFirst
import dev.ligature.gaze.takeString
import dev.ligature.gaze.takeUntil
import dev.ligature.gaze.takeWhile

import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.StringLiteral
import dev.ligature.Value

import arrow.core.Some
import arrow.core.none
import arrow.core.None

object LigNibblers {
  val whiteSpaceNibbler = takeCharacters(' ', '\t')
  val whiteSpaceAndNewLineNibbler = takeAll(takeFirst(takeString(" "), takeString("\n"), takeString("\r\n"), takeString("\t")))
  val numberNibbler = takeCharacters(('0' to '9').toList().add('-'))

  val identifierNibbler = between(
    takeString("<"),
    takeWhile { c =>
      "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
    },
    takeString(">")
  )

  val stringContentNibbler: Nibbler<Char, Char> = {
    (gaze: Gaze[Char]) -> {
    // Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
    val commandChars = 0x00.toChar to 0x1f.toChar
    val validHexChar = (c: Char) =>
    ('0' to '9' contains c) || ('a' to 'f' contains c) || ('A' to 'F' contains c)
    val hexNibbler = takeWhile(validHexChar)

    val sb = mutableListOf<Char>()
    var offset = 0 // TODO delete
    var fail = false
    var complete = false
    while (!complete && !fail && gaze.peek() is Some) {
      val c = gaze.next().get
      if (commandChars.contains(c)) {
        fail = true
      } else if (c == '"') {
        complete = true
      } else if (c == '\\') {
        sb.append(c)
        when (val next = gaze.next()) {
          is None -> fail = true
          is Some -> {
            when (c) {
              '\\' || '"' || 'b' || 'f' || 'n' || 'r' || 't' -> sb.append(c)
              'u' -> {
                sb.append(c)
                when (val res = gaze.attempt(hexNibbler)) {
                  is None -> fail = true
                  is Some -> {
                    if (res.value.length == 4) {
                      sb.addAll(res.value)
                    } else {
                      fail = true
                    }
                  }
                }
              }
              else -> fail = true
            }
          }
        }
      } else {
        sb.add(c)
      }
    }
    if (fail) {
      none()
    } else {
      Some(sb.toList())
    }
  }

  val stringNibbler = takeAllGrouped(
    takeString("\""),
    stringContentNibbler
  ) // TODO should be a between but stringContentNibbler consumes the last " currently

  //TODO this needs cleaned up
  private val validPrefixName: CharArray =
    (('a'..'z').toMutableList() + ('A'..'Z').toMutableList() + ('0'..'9').toMutableList() + listOf('_')).joinToString("").toCharArray()

  val prefixNameNibbler = takeCharacters(
      *validPrefixName
    ) // matches _a-zA-Z0-9, TODO probably shouldn't make names that start with numbers
  val copyNibbler = takeString("^") // matches ^

  val idGenNibbler = takeString("{}") // matches {}
  // val identifierIdGenNibbler = ??? //matches <{}> <prefix:{}> <{}:postfix> <pre:{}:post> etc
  // val prefixedIdentifierNibbler = ??? //matches prefix:value:after:prefix
  // val prefixedIdGenNibbler = ??? // matches prefix:value:after:prefix:{}
}
