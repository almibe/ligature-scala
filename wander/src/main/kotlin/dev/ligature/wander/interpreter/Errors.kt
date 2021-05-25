/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

sealed class WanderError
data class ParserError(val message: String, val position: Int): WanderError()
data class NotSupported(val message: String = "Error: Not Supported"): WanderError()
data class SymbolExits(val name: String): WanderError()
data class UnknownSymbol(val name: String): WanderError()
data class ArgumentError(val message: String): WanderError()
