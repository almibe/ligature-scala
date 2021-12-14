/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package npm

import sbt._
import Keys._
import scalajscrossproject.JSPlatform
import org.scalajs.sbtplugin.ScalaJSPlugin
import bloop.integrations.sbt.ScalaJsKeys

object NpmSupport extends AutoPlugin {
  override def requires: Plugins = ScalaJSPlugin
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    val npm = taskKey[Unit]("generate npm project for publishing")
  }
  import autoImport._

  override lazy val projectSettings = Seq(
    npm := {
      if (name.value == "ligature-js") {
//        val foj = ScalaJSPlugin.autoImport.fullOptJS.value

      }
    }
  )
}
