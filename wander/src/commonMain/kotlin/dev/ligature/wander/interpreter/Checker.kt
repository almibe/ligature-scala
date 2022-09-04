/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import dev.ligature.wander.parser.Script
import arrow.core.Either

data class TypeError(val message: String)

fun typeCheck(script: Script): Either<List<TypeError>, Unit> = TODO()
