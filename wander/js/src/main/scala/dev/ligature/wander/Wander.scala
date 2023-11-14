/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.parse
import scala.annotation.unused
import dev.ligature.wander.preludes.common
import scala.scalajs.js.annotation._

@JSExportTopLevel("Wander")
object Wander {
  @JSExport
  def run(script: String) = {
    dev.ligature.wander.run(script, common())
  }
}
