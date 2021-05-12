/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.wander.lexer.WanderToken

@OptIn(ExperimentalUnsignedTypes::class)
class TokenScanner(private val tokens: List<WanderToken>) {
    private var offset = 0U

    fun peek(skip: UInt = 0U): WanderToken? {
        val position = (offset + skip).toInt()
        return if (position < tokens.size) {
            tokens[position]
        } else {
            null
        }
    }

    fun skip(skip: UInt = 1U) {
        offset += skip
    }

    fun isComplete(): Boolean {
        return offset.toInt() >= tokens.size
    }
}
