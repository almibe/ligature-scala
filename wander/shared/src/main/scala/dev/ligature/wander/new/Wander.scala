/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

def parse(tokens: List[Token]): List[Element] =
    ???

def interpret(elements: List[Element]): WanderValue =
    ???

def run(script: String): String = {
    script
}

enum Element:
    case Integer(value: Long)

enum WanderValue:
    case Integer(value: Long)
