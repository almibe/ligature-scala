/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.js

//import kotlinx.coroutines.runBlocking
import arrow.core.getOrElse
import dev.ligature.Dataset
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.lig.insertLig

@JsExport
class Ligature {
  private val store = InMemoryLigature()
  private val dataset = Dataset.create("instance").getOrElse { TODO() }

  init {

  }

  fun loadLig(lig: String): Unit {

    TODO()
//    runBlocking {
//      store.insertLig(dataset, lig)
//
//    }
  }

  fun removeLig(lig: String): Unit {
    TODO()
  }

  fun runWander(wander: String): String {
    TODO()
  }
}
