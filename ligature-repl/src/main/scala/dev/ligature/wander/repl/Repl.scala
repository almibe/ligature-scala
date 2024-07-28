/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.repl

import scala.swing._
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import dev.ligature.wander.run
import dev.ligature.wander.printResult

def content () = {
    new BorderPanel {
        val input = new TextField("")
        val button = new Button("Run")
        val output = new TextArea("")

        input.peer.addKeyListener(new KeyAdapter() {
            override def keyPressed(e: KeyEvent): Unit =
                if e.getKeyCode()==KeyEvent.VK_ENTER then
                    val script = input.text
                    input.text = ""
                    val res = printResult(run(script))
                    val result = output.text
                    output.text = res + "\n" + result
        })

        import BorderPanel.Position._

        layout(input) = North
        layout(output) = Center
    }
}

class UI extends MainFrame {
  title = "LigatureREPL"
  preferredSize = new Dimension(800, 600)
  contents = content()
}

object Repl {
  def main(args: Array[String]) = {
    val ui = new UI
    ui.visible = true
  }
}
