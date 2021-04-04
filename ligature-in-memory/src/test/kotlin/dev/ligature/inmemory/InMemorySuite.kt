/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.Ligature
import dev.ligature.testsuite.LigatureTestSuite

class InMemorySuite: LigatureTestSuite() {
    override fun createLigature(): Ligature = InMemoryLigature()
}
