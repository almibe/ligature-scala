/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.getOrElse
import dev.ligature.rakkoon.Rakkoon
import dev.ligature.rakkoon.Rule
import dev.ligature.rakkoon.stringPattern
import dev.ligature.rakkoon.toIntAction

class Wander {
    fun run(input: String): String { //TODO don't return String, should be an either
        val rakkoon = Rakkoon(input)
        return rakkoon.bite(Rule(stringPattern("5"), toIntAction)).getOrElse { "TODO()" }.toString()
    }
}
