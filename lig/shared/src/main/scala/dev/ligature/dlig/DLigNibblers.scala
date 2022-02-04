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
import dev.ligature.{
  Identifier,
  IntegerLiteral,
  Statement,
  StringLiteral,
  Value
}

object DLigNibblers {
  val identifierNibbler = {
    //todo handle plain id gen
    //todo handle <> wrapped id gen
    //todo handle prefixed ids
    //todo handle prefixed ids with id gens
    ???
  }
  val copyNibbler = ???
  val prefixNibbler = ???
}
