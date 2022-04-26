/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.idgen

import java.security.SecureRandom

private val ID_GENERATOR = SecureRandom()
private val ID_ALPHABET = "_-0123456789abcdefABCDEF".toCharArray()
private val ID_SIZE = 12

fun genId(): String = randomNanoId(ID_GENERATOR, ID_ALPHABET, ID_SIZE)
