/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.memory

import dev.ligature.http.*
import dev.ligature.http.testsuite.LigatureHttpSuite
import dev.ligature.inmemory.InMemoryLigature
import io.ktor.server.application.*

class LigatureHttpMemorySuite : LigatureHttpSuite() {
  override fun Application.instanceModule() {
    routes(InMemoryLigature())
  }
}
