/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import scala.collection.mutable.Set
import scala.util.boundary

case class Environment(
    functions: List[HostFunction] = List(),
    properties: List[HostProperty] = List(),
    scopes: List[Map[Name, WanderValue]] = List(Map()),
) {
  def eval(expressions: Seq[Expression]): Either[WanderError, (WanderValue, Environment)] = {
    var env = this
    var lastResult: Option[WanderValue] = None
    val err =
      boundary:
        expressions.foreach { expression =>
          dev.ligature.wander.eval(expression, env) match {
            case Left(value) => boundary.break(value)
            case Right((value, environment)) =>
              env = environment
              lastResult = Some(value)
          }
        }
    (lastResult, err) match {
      case (_, err: WanderError) => Left(err)
      case (None, _)             => Right((WanderValue.Nothing, env))
      case (Some(value), _)      => Right((value, env))
    }
  }

  def newScope(): Environment =
    Environment(
      this.functions,
      this.properties,
      this.scopes.appended(Map())
    )

  def bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Environment = {
    val currentScope = this.scopes.last
    val newVariables = currentScope + (name -> wanderValue)
    val oldScope = this.scopes.dropRight(1)
    Environment(
      this.functions,
      this.properties,
      oldScope.appended(newVariables)
    )
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
    this.functions.find(_.name == name.name) match {
      case None           => ()
      case Some(function) => return Right(WanderValue.HostFunction(function))
    }
    this.properties.find(_.name == name.name) match {
      case None           => ()
      case Some(HostProperty(_, property)) => return property(this).map(value => value._1)
    }

    Left(WanderError(s"Could not find ${name} in scope."))
  }

  def addHostFunctions(functions: Seq[HostFunction]): Environment =
    this.copy(functions = this.functions ++ functions)

  def addHostProperties(properties: Seq[HostProperty]): Environment =
    this.copy(properties = this.properties ++ properties)
}
