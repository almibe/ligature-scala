/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.js

import arrow.core.getOrElse
import dev.ligature.Dataset
import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.lig.insertLig
import dev.ligature.lig.removeLig
import dev.ligature.wander.wanderQuery
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.js.Promise
import kotlinx.coroutines.promise
import kotlinx.coroutines.GlobalScope

@OptIn(ExperimentalJsExport::class, DelicateCoroutinesApi::class)
@JsExport
class Ligature private constructor() {
  private val store = InMemoryLigature()
  private val dataset = Dataset.create("instance").getOrElse { TODO() }

  companion object {
    fun create(): Promise<Ligature> =
        GlobalScope.promise {
          val instance = Ligature()
          instance.store.createDataset(instance.dataset)
          instance
        }
  }

  fun loadLig(lig: String): Promise<String> =
      GlobalScope.promise { store.insertLig(dataset, lig) }.then { "Added." }

  fun removeLig(lig: String): Promise<String> =
      GlobalScope.promise { store.removeLig(dataset, lig) }.then { "Remove" }

  fun runWander(wander: String): Promise<String> =
      GlobalScope.promise { store.wanderQuery(dataset, wander) }.then { "Query Results" }
}
