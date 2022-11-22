/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.js

import dev.ligature.inmemory.InMemoryLigature

@JsExport
class Ligature {
  private val store = InMemoryLigature()

  suspend fun loadLig(lig: String): Unit {

  }

  suspend fun removeLig(lig: String): Unit {

  }

  suspend fun runWander(wander: String): String {

  }
}
