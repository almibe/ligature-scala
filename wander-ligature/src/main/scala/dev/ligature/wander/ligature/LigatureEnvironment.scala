/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.ligature

import dev.ligature.wander.Environment
import dev.ligature.Ligature
import dev.ligature.wander.libraries.common
import dev.ligature.wander.*
import dev.ligature.Graph

def ligatureEnvironment(ligature: Ligature): Environment =
  common()
    .addHostProperties(
      Seq(
        HostProperty(
          "Ligature.graphs",
          "Get all graphs from this instance.",
          Tag.Single(Name("Core.Array")),
          (environment: Environment) => {
            val graphs = ligature.allGraphs().map(g => WanderValue.String(g.name))
            Right((WanderValue.Array(graphs.toSeq), environment))
          }
        )
      )
    )
    .addHostFunctions(
      Seq(
        // HostFunction(
        //   "Ligature.createGraph",
        //   (arguments, environment) =>
        //     arguments match
        //       case Seq(Expression.StringValue(graphName)) =>
        //         ligature.createGraph(Graph(graphName))
        //         Right(WanderValue.Nothing, environment)
        //       case _ => ???
        // ),
        // HostFunction(
        //   "Ligature.deleteGraph",
        //   (arguments, environment) =>
        //     arguments match
        //       case Seq(Expression.StringValue(graphName)) =>
        //         ligature.deleteGraph(Graph(graphName))
        //         Right(WanderValue.Nothing, environment)
        //       case _ => ???
        // )
      )
    )
