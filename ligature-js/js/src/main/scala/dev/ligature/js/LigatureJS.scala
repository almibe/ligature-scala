/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.js

import dev.ligature.wander.{run => wanderRun}
import scala.scalajs.js.annotation.JSExportTopLevel
import dev.ligature.wander.ExecutionMode

@JSExportTopLevel("run")
def run(script: String): String = {
  wanderRun(script, ExecutionMode.StandAloneMode).toString
}
