/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.memory

import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.http.testsuite.LigatureHttpSuite
import dev.ligature.http.LigatureHttp

class LigatureHttpMemorySuite extends LigatureHttpSuite {
  override def createInstance() = LigatureHttp(InMemoryLigature())
}
