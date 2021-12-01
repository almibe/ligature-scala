/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import dev.ligature.*
import io.vertx.core.AbstractVerticle

class LigatureVerticle(private val ligature: LigatureInstance)
    extends AbstractVerticle {}
