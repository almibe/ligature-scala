/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.js

import dev.ligature.Dataset
import dev.ligature.wander.run as wanderRun

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("run")
def run(script: String, dataset: Dataset): String = {
  wanderRun(script, dataset).toString
}
