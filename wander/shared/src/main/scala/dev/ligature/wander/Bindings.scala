/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.{Name, ScriptError, WanderValue}
import dev.ligature.wander.parser.FunctionDefinition
import dev.ligature.wander.parser.NativeFunction

case class Scope(variables: Map[Name, WanderValue])

case class Bindings(scopes: List[Scope] = List(Scope(Map()))) {
  def newScope(): Bindings =
    Bindings(this.scopes.appended(Scope(Map())))

  def bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Either[ScriptError, Bindings] = {
    val currentScope = this.scopes.last
    if (currentScope.variables.contains(name)) {
      // TODO probably remove this to allow shadowing?
      Left(ScriptError(s"$name is already bound in current scope."))
    } else {
      val newVariables = currentScope.variables + (name -> wanderValue)
      val oldScope = this.scopes.dropRight(1)
      Right(
        Bindings(oldScope.appended(Scope(newVariables)))
      )
    }
  }

  def read(name: Name): Either[ScriptError, WanderValue] = {
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.variables.contains(name)) {
        return Right(currentScope.variables(name))
      }
      currentScopeOffset -= 1
    }
    Left(ScriptError(s"Could not find $name in scope."))
  }
}

//TODO this function will probably be used once I allow for ad-hoc polymorphism with functions.
def createFunctionDelegate(): NativeFunction =
  ???
