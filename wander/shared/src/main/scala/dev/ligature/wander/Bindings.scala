/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.{Name, ScriptError, WanderValue}
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import dev.ligature.wander.parser.FunctionDefinition

case class Scope(
    val variables: Map[Name, WanderValue],
    val functions: Map[Name, ArrayBuffer[FunctionDefinition]]
)

class Bindings {
  private def createScope(): Scope = Scope(Map(), Map())

  private val scopes: ArrayBuffer[Scope] = ArrayBuffer(
    createScope()
  )

  def addScope() = {
    this.scopes.append(createScope())
  }

  def removeScope(): Either[ScriptError, Unit] = {
    if (this.scopes.length <= 1) {
      Left(ScriptError("Can not remove scope."))
    } else {
      this.scopes.remove(this.scopes.length - 1)
      Right(())
    }
  }

  def bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Either[ScriptError, Unit] = {
    val currentScope = this.scopes.last
    if (
      currentScope.variables
        .contains(name) || currentScope.functions.contains(name)
    ) {
      Left(ScriptError(s"${name} is already bound in current scope."))
    } else {
      currentScope.variables += (name -> wanderValue)
      Right(())
    }
  }

  def bindFunction(
      name: Name,
      functionDefinition: FunctionDefinition
  ): Either[ScriptError, Unit] = {
    val currentScope = this.scopes.last
    if (
      currentScope.variables
        .contains(name) || duplicateFunction(name, functionDefinition)
    ) {
      Left(ScriptError(s"${name} is already bound in current scope."))
    } else {
      if (currentScope.functions.contains(name)) {
        currentScope.functions.get(name).get.append(functionDefinition)
        Right(())
      } else {
        currentScope.functions += (name -> ArrayBuffer(functionDefinition))
        Right(())
      }
    }
  }

  private def duplicateFunction(
      name: Name,
      functionDefinition: FunctionDefinition
  ): Boolean = {
    ???
  }

  def read(name: Name): Either[ScriptError, WanderValue] = {
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.variables.contains(name)) {
        return Right(currentScope.variables(name))
      } else if (currentScope.functions.contains(name)) {
        ???
      }
      currentScopeOffset -= 1
    }
    Left(ScriptError(s"Could not find ${name} in scope."))
  }
}
