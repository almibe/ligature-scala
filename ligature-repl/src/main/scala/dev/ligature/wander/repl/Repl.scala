/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.repl

import scala.swing._
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import dev.ligature.wander.run
import dev.ligature.wander.INetwork
import dev.ligature.wander.InMemoryNetwork
import dev.ligature.wander.printNetwork

def content () = {
    new BorderPanel {
        val input = new TextField("")
        val button = new Button("Run")
        val output = new TextArea("")

        var network: INetwork = InMemoryNetwork(Set.empty)

        input.peer.addKeyListener(new KeyAdapter() {
            override def keyPressed(e: KeyEvent): Unit =
                if e.getKeyCode()==KeyEvent.VK_ENTER then
                    val script = input.text
                    input.text = ""
                    run(script, network) match
                      case Left(dev.ligature.wander.WanderError(value)) =>
                        output.text = value + "\n" + output.text                        
                      case Right(res) => 
                        val result = output.text
                        network = network.union(res)
                        output.text = printNetwork(res) + "\n" + result
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
