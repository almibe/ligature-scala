/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import scala.collection.mutable.Set

case class Statement(entity: Identifier, attribute: Identifier, value: WanderValue)

def statement(value: WanderValue): Statement = {
  value match {
    case WanderValue.Triple(entity, attribute, value) => Statement(entity, attribute, value)
    case WanderValue.Quad(entity, attribute, value, _) => Statement(entity, attribute, value)
    case _ => ???
  }
}

case class Environment(graphs: scala.collection.mutable.Map[String, Set[Statement]] = scala.collection.mutable.Map(), scopes: List[Map[Name, WanderValue]] = List(Map())) {
  def newScope(): Environment = Environment(this.graphs, this.scopes.appended(Map()))

  def bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Environment = {
    val currentScope = this.scopes.last
    val newVariables = currentScope + (name -> wanderValue)
    val oldScope = this.scopes.dropRight(1)
    Environment(this.graphs, oldScope.appended(newVariables))
  }

  def read(name: Name): Either[WanderError, WanderValue] = {
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.contains(name)) {
        return Right(currentScope(name))
      }
      currentScopeOffset -= 1
    }
    Left(WanderError(s"Could not find ${name} in scope."))
  }

  def addTriple(triple: WanderValue.Triple): Either[WanderError, Unit] = {
    if (this.graphs.contains("")) {
      val graph = this.graphs.get("").get
      graph.add(statement(triple))
      Right(())
    } else {
      this.graphs += ("" -> Set(statement(triple)))
      Right(())
    }
  }

  def name(quad: WanderValue.Quad): String = quad.graph.name

  def addQuad(quad: WanderValue.Quad): Either[WanderError, Unit] = {
    val graphName = name(quad)
    if (this.graphs.contains(graphName)) {
      val graph = this.graphs.get(graphName).get
      graph.add(statement(quad))
      Right(())
    } else {
      this.graphs += (graphName -> Set(statement(quad)))
      Right(())
    }
  }
}
