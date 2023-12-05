/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.ligature

import dev.ligature.wander.Environment
import dev.ligature.Ligature
import dev.ligature.wander.preludes.common

def ligatureEnvironment(ligature: Ligature): Environment = {
    common(LigatureInterpreter(ligature))
}
