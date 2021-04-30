/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.rakkoon.RakkoonError

sealed class WanderError

data class ParsingError(val error: RakkoonError): WanderError()

data class NotSupported(val message: String = "Error: Not Supported"): WanderError()

sealed class SymbolError: WanderError()
data class SymbolExits(val name: String): SymbolError()
data class UnknownSymbol(val name: String): SymbolError()
