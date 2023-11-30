/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.parse
import scala.annotation.unused
import dev.ligature.wander.preludes.common
import scala.scalajs.js.annotation._

@JSExportTopLevel("default")
object Wander {
  // def inMemoryRun(script: String): Either[WanderError, (WanderValue, Environment)] = {
  //     createLigatureInMemory().use { instance => 
  //       IO.pure(dev.ligature.wander.run(script, common(instance)))
  //     }.unsafeRunSync()
  // }
  // @JSExport
  // def run(script: String): String = {
  //   printResult(inMemoryRun(script))
  // }
  // @JSExport
  // def introspect(script: String): String =
  //   dev.ligature.wander.introspect(script).toString()
}
