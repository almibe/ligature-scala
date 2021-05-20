/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.printer

import io.kotest.core.spec.style.FunSpec
import kotlin.io.path.ExperimentalPathApi

@OptIn(ExperimentalPathApi::class)
class PrinterSpec : FunSpec() {
    private val printer = Printer()

    init {
    }
}
