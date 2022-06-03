/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  between,
  takeAll,
  takeAllGrouped,
  takeCharacters,
  takeString,
  takeUntil,
  takeWhile
}
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral, Value}
import dev.ligature.lig.LigNibblers

private val validPrefixName =
  (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList.appended('_')

object DLigNibblers {
  val prefixNameNibbler = takeCharacters(
    validPrefixName: _*
  ) // matches _a-zA-Z0-9, TODO probably shouldn't make names that start with numbers
  val copyNibbler = takeString("^") // matches ^

  val identifierNibbler =
    LigNibblers.identifierNibbler // NOTE: use LigNibblers.identifier for matching regular identifier
  val idGenNibbler = takeString("{}") // matches {}
  // val identifierIdGenNibbler = ??? //matches <{}> <prefix:{}> <{}:postfix> <pre:{}:post> etc
  // val prefixedIdentifierNibbler = ??? //matches prefix:value:after:prefix
  // val prefixedIdGenNibbler = ??? // matches prefix:value:after:prefix:{}
}
