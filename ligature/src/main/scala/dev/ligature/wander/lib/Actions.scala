/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lib

import dev.ligature.wander.LigatureValue
import dev.ligature.wander.HostAction
import cats.effect.IO

val stdActions = Map(
  LigatureValue.Element("nothing-doing") ->
    HostAction("Do nothing.", stack => IO.pure(stack)),
  LigatureValue.Element("clear") ->
    HostAction("Clear the stack.", _ => IO.pure(List()))
)
