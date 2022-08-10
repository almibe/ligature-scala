/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.StringLiteral
import dev.ligature.Value

import arrow.core.Some
import arrow.core.none
import arrow.core.None
import dev.ligature.gaze.*
import dev.ligature.gaze.takeString

object LigNibblers {
  val whiteSpaceNibbler: Nibbler<Char, LigToken> = takeCharacters(' ', '\t').map { listOf(LigToken.WhiteSpace) }

  val newLineNibbler: Nibbler<Char, LigToken> = takeAll(takeFirst(takeString("\n"), takeString("\r\n"))).map { listOf(LigToken.NewLine) }

  val identifierNibbler = between(
    takeString("<"),
    takeWhile { c ->
      Regex("[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]").matches(c.toString())
    },
    takeString(">")
  ).map { listOf(LigToken.Identifier(it.joinToString(""))) }

  val integerNibbler = takeAll(
    optional(takeString("-")),
    takeWhile { it.isDigit() } )
    .map { listOf(LigToken.IntegerLiteral(it.joinToString(""))) }

  val bytesNibbler = takeAll(
    takeString("0"),
    takeString("x"),
    takeWhile { Regex("[a-fA-F0-9]").matches(it.toString()) } )
    .map { listOf(LigToken.BytesLiteral(it.joinToString(""))) }

  val stringContentNibbler: Nibbler<Char, Char> = //{ gaze: Gaze<Char> -> {
      // Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
    takeWhile { Regex("[a-zA-Z0-9 ,.!?]").matches(it.toString()) }
//      val commandChars = 0x00.toChar()..0x1f.toChar()
//      val validHexChar = { c: Char ->
//        (c in '0'..'9') || (c in 'a'..'f') || (c in 'A'..'F')
//      }
//      val hexNibbler = takeWhile(validHexChar) //TODO should probably only read pairs in
//
//      val sb = mutableListOf<Char>()
//      var fail = false
//      var complete = false
//      while (!complete && !fail && !gaze.isComplete) {
//        val c = gaze.next().get
//        if (commandChars.contains(c)) {
//          fail = true
//        } else if (c == '"') {
//          complete = true
//        } else if (c == '\\') {
//          sb.append(c)
//          when (val next = gaze.next()) {
//            is None -> fail = true
//            is Some -> {
//              when (c) {
//                '\\' || '"' || 'b' || 'f' || 'n' || 'r' || 't' -> sb.append(c)
//                'u' -> {
//                  sb.append(c)
//                  when (val res = gaze.attempt(hexNibbler)) {
//                    is None -> fail = true
//                    is Some -> {
//                      if (res.value.length == 4) {
//                        sb.addAll(res.value)
//                      } else {
//                        fail = true
//                      }
//                    }
//                  }
//                }
//                else -> fail = true
//              }
//            }
//          }
//        } else {
//          sb.add(c)
//        }
//      }
//      if (fail) {
//        none()
//      } else {
//        Some(sb.toList())
//      }
//    }
//  }

  val stringNibbler = between(
    takeString("\""),
    stringContentNibbler
  ).map { listOf(LigToken.StringLiteral(it.joinToString(""))) }

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
