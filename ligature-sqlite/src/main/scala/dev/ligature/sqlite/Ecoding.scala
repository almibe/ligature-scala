/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import io.fury.Fury
import dev.ligature.LigatureValue
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.ArrayByteIterable

val fury = Fury
  .builder()
  .withScalaOptimizationEnabled(true)
  .requireClassRegistration(false)
  .withRefTracking(true)
  .build()

def encodeLigatureValue(value: LigatureValue): ByteIterable =
  ArrayByteIterable(fury.serialize(value))

def decodeLigatureValue(value: ByteIterable): LigatureValue =
  fury.deserialize(value.getBytesUnsafe()).asInstanceOf[LigatureValue]
