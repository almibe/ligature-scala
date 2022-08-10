/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.None
import arrow.core.Some
import dev.ligature.gaze.Gaze
import dev.ligature.lig.LigNibblers.identifierNibbler

sealed interface LigToken {
//  data class Term(val value: String): LigToken
//  object Equals: LigToken
  data class Identifier(val name: String): LigToken
//  object Copy: LigToken
  object NewLine: LigToken
  object WhiteSpace: LigToken
  data class IntegerLiteral(val value: String): LigToken
  data class StringLiteral(val value: String): LigToken
  data class BytesLiteral(val value: String): LigToken
}

fun tokenize(input: String): List<LigToken> {
  val gaze = Gaze.from(input)
  val tokens = mutableListOf<LigToken>()
  while (!gaze.isComplete) {
    val res = gaze.attempt<Char>(identifierNibbler).map { LigToken.Identifier(it.joinToString("")) }
    when(res) {
      is None -> { TODO() }
      is Some -> { tokens.add(res.value) }
    }
  }
  //Bytes nibbler
  //Integer nibbler
  //String nibbler
  //new line nibbler
  //white space nibbler
  return tokens
}
