/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue

case class Bindings(scopes: List[Map[Name, WanderValue]] = List((Map()))) {
  def newScope(): Bindings = Bindings(this.scopes.appended(Map()))

  def bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Bindings = {
    val currentScope = this.scopes.last
    val newVariables = currentScope + (name -> wanderValue)
    val oldScope = this.scopes.dropRight(1)
    Bindings(oldScope.appended(newVariables))
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
}
