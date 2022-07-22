/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.ScriptError
import dev.ligature.wander.parser.WanderValue
import dev.ligature.wander.parser.FunctionDefinition
import dev.ligature.wander.parser.NativeFunction

import arrow.core.Either

data class Scope(
    val variables: Map<Name, WanderValue>,
    val functions: Map<Name, List<FunctionDefinition>>
)

data class Bindings(val scopes: List<Scope> = listOf(Scope(mapOf(), mapOf()))) {
  fun newScope(): Bindings =
    Bindings(this.scopes + (Scope(mapOf(), mapOf())))

  fun bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Either<ScriptError, Bindings> {
    val currentScope = this.scopes.last()
    if (
      currentScope.variables
        .contains(name) || currentScope.functions.contains(name)
    ) {
      Either.Left(ScriptError("$name is already bound in current scope."))
    } else {
      val newVariables = currentScope.variables + (name to wanderValue)
      val oldScope = this.scopes.dropLast()
      Either.Right(
        Bindings(oldScope + (Scope(newVariables, currentScope.functions)))
      )
    }
  }

  fun bindFunction(
      name: Name,
      functionDefinition: FunctionDefinition
  ): Either<ScriptError, Bindings> {
    val currentScope = this.scopes.last
    if (
      currentScope.variables
        .contains(name) || duplicateFunction(name, functionDefinition)
    ) {
      Either.Left(ScriptError("$name is already bound in current scope."))
    } else {
      if (currentScope.functions.contains(name)) {
        val newFunctionList =
          currentScope.functions(name).appended(functionDefinition)
        val newFunctions = currentScope.functions.updated(name, newFunctionList)
        val oldScope = this.scopes.dropLast()
        Either.Right(
          Bindings(
            oldScope + (Scope(currentScope.variables, newFunctions))
          )
        )
      } else {
        val newFunctions =
          currentScope.functions.updated(name, List(functionDefinition))
        val oldScope = this.scopes.dropRight(1)
        Either.Right(
          Bindings(
            oldScope.appended(Scope(currentScope.variables, newFunctions))
          )
        )
      }
    }
  }

  private fun duplicateFunction(
      name: Name,
      functionDefinition: FunctionDefinition
  ): Boolean {
    val currentScope = this.scopes.last()
    if (currentScope.functions.contains(name)) {
      val functions = currentScope.functions(name)
      val dupe = functions.find { f ->
        functionDefinition.parameters == f.parameters
      }
      dupe.isDefined
    } else {
      false
    }
  }

  fun read(name: Name): Either<ScriptError, WanderValue> {
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.variables.contains(name)) {
        return Either.Right(currentScope.variables(name))
      } else if (currentScope.functions.contains(name)) {
        TODO()
      }
      currentScopeOffset -= 1
    }
    Either.Left(ScriptError("Could not find $name in scope."))
  }
}

//TODO this function will probably be used once I allow for ad-hoc polymorphism with functions.
fun createFunctionDelegate(): NativeFunction =
  TODO()
