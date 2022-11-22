/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.Dataset
import dev.ligature.Ligature
import dev.ligature.LigatureError

suspend fun Ligature.insertLig(dataset: Dataset, lig: String): Either<LigatureError, Unit> =
  when (val ligRes = read(lig)) {
    is Left -> ligRes
    is Right -> {
      this.write(dataset) {
        ligRes.value.forEach { statement ->
          it.addStatement(statement)
        }
      }
    }
  }


suspend fun Ligature.removeLig(dataset: Dataset, lig: String): Either<LigatureError, Unit>  =
  when (val ligRes = read(lig)) {
    is Left -> ligRes
    is Right -> {
      this.write(dataset) {
        ligRes.value.forEach { statement ->
          it.removeStatement(statement)
        }
      }
    }
  }
