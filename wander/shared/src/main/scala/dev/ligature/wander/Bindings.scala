/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.{Name, ScriptError, WanderValue}
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

class Bindings {
  private val scopes: ArrayBuffer[Map[Name, WanderValue]] = ArrayBuffer(
    HashMap()
  )

  def addScope() = {
    this.scopes.append(HashMap())
  }

  def removeScope(): Either[ScriptError, Unit] = {
    if (this.scopes.length <= 1) {
      Left(ScriptError("Can not remove scope."))
    } else {
      this.scopes.remove(this.scopes.length - 1)
      Right(())
    }
  }

  def bind(name: Name, wanderValue: WanderValue): Either[ScriptError, Unit] = {
    val currentScope = this.scopes.last
    if (currentScope.contains(name)) {
      Left(ScriptError(s"${name} is already bound in current scope."))
    } else {
      currentScope += (name -> wanderValue)
      Right(())
    }
  }

  def read(name: Name): Either[ScriptError, WanderValue] = {
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.contains(name)) {
        return Right(currentScope(name))
      }
      currentScopeOffset -= 1
    }
    Left(ScriptError(s"Could not find ${name} in scope."))
  }
}
