/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.error

sealed class WanderError

sealed class InterpreterError: WanderError()
data class ParserError(val message: String, val position: Int): InterpreterError()
data class NotSupported(val message: String = "Error: Not Supported"): InterpreterError()
data class SymbolExits(val name: String): InterpreterError()
data class UnknownSymbol(val name: String): InterpreterError()
