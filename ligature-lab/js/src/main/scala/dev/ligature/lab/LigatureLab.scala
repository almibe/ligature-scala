/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lab

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

@main def main() = {
  val rootElement = div(
    button(

    ),
    textArea(
      onMountFocus,
    ),
    textArea(
      
    )
  )

  // In most other examples, containerNode will be set to this behind the scenes
  val containerNode = dom.document.querySelector("#main")

  render(containerNode, rootElement)
}
