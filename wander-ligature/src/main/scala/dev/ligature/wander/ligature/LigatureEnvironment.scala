/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.ligature

import dev.ligature.wander.Environment
import dev.ligature.Ligature
import dev.ligature.wander.preludes.common
import dev.ligature.wander.HostProperty
import dev.ligature.wander.WanderValue
import dev.ligature.wander.HostFunction
import dev.ligature.wander.Expression
import dev.ligature.Graph

def ligatureEnvironment(ligature: Ligature): Environment = {
  val interpreter = LigatureInterpreter(ligature)
  common()
    .addHostProperties(
      Seq(
        HostProperty(
          "graphs",
          environment => {
            val graphs = ligature.allGraphs().map(g => WanderValue.StringValue(g.name))
            Right((WanderValue.Array(graphs.toSeq), environment))
          }
        )
      )
    )
    .addHostFunctions(
      Seq(
        HostFunction(
          "use",
          (arguments, environment) =>
            arguments match {
              case Seq(Expression.StringValue(graphName)) =>
                //interpreter.use(Graph(graphName))
                Right((WanderValue.Nothing, environment))
              case _ => ???
            }
        )
      )
    )
}
