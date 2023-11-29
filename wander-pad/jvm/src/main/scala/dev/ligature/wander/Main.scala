/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.control.TextArea
import scalafx.scene.layout.VBox
import scalafx.scene.control.Button
import scalafx.stage.StageStyle
import dev.ligature.wander.preludes.common
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.SplitPane
import scalafx.geometry.Orientation

object ScalaFXHelloWorld extends JFXApp3 {
  override def start(): Unit = {
    val editorInput = TextArea("")
    val resultOutput = TextArea("")
    val runButton = Button("Run")
    val introButton = Button("Intro")
    runButton.onAction = { e =>
      val script = editorInput.getText()
      val result = run(script, common())
      resultOutput.text = printResult(result)
    }
    introButton.onAction = { e =>
      val script = editorInput.getText()
      val intro = introspect(script)
      val result = run(script, common())

      resultOutput.text = "Tokens      :" + intro.tokens.toString() + "\n" +
        "Terms       :" + intro.terms.toString() + "\n" +
        "Expressions :" + intro.expression.toString() + "\n" +
        "Result      :" + result.toString() + "\n"
    }

    val sp = new SplitPane {
      items ++= Seq(editorInput, resultOutput)
      orientation = Orientation.Vertical
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Wander Pad"
      scene = new Scene {
        fill = Color.rgb(255, 255, 255)
        root = new BorderPane {
          top = new HBox {
            children = Seq(
              runButton,
              introButton
            )
          }
          center = sp
        }
      }
    }
  }
}
