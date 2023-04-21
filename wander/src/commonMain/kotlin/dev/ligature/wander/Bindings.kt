/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Some
import dev.ligature.wander.parser.Name
import dev.ligature.wander.interpreter.ScriptError
import dev.ligature.wander.parser.WanderValue
import dev.ligature.wander.parser.FunctionDefinition
import dev.ligature.wander.parser.NativeFunction
import kotlin.reflect.KClass

data class Scope(val variables: MutableMap<Name, WanderValue> = mutableMapOf())
//    val functions: Map<Name, List<FunctionDefinition>>
//)

class Bindings {
  private val scopes = mutableListOf<Scope>()

  fun addScope() = this.scopes.add(Scope())
  fun removeScope() = this.scopes.removeLast()

  fun bindVariable(
      name: Name,
      wanderValue: WanderValue
  ): Either<ScriptError, Unit> {
    val currentScope = this.scopes.last()
    return if (currentScope.variables.contains(name)) {
      Left(ScriptError("$name is already bound in current scope."))
    } else {
      currentScope.variables[name] = wanderValue
      Right(Unit)
    }
  }

//  fun bindFunction(
//      name: Name,
//      functionDefinition: FunctionDefinition
//  ): Either<ScriptError, Bindings> {
//    val currentScope = this.scopes.last()
//    return if (
//      currentScope.variables
//        .contains(name) || duplicateFunction(name, functionDefinition)
//    ) {
//      Either.Left(ScriptError("$name is already bound in current scope."))
//    } else {
//      if (currentScope.functions.contains(name)) {
//        val newFunctionList =
//          currentScope.functions[name]!!.plus((functionDefinition))
//        val newFunctions = currentScope.functions + (name to newFunctionList)
//        val oldScope = this.scopes.dropLast(1)
//        Either.Right(
//          Bindings(
//            oldScope + (Scope(currentScope.variables, newFunctions))
//          )
//        )
//      } else {
//        val newFunctions =
//          currentScope.functions + (name to listOf(functionDefinition))
//        val oldScope = this.scopes.dropLast(1)
//        Either.Right(
//          Bindings(
//            oldScope + (Scope(currentScope.variables, newFunctions))
//          )
//        )
//      }
//    }
//  }

//  private fun duplicateFunction(
//      name: Name,
//      functionDefinition: FunctionDefinition
//  ): Boolean {
//    val currentScope = this.scopes.last()
//    return if (currentScope.functions.contains(name)) {
//      val functions = currentScope.functions[name]!!
//      val dupe = functions.find { f ->
//        functionDefinition.parameters == f.parameters
//      }
//      dupe != null
//    } else {
//      false
//    }
//  }

  inline fun <reified T: WanderValue>readExperiment(name: Name): Either<ScriptError, T> =
    when (val value = read(name)) {
      is Right -> {
        if (value.value is T) Right(value as T)
        else Left(ScriptError("Could not read $name with correct type, found ${value.value}."))
      }
      is Left -> value
    }

  fun read(name: Name): Either<ScriptError, WanderValue> {
    var currentScopeOffset = this.scopes.size - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes[currentScopeOffset]
      if (currentScope.variables.contains(name)) {
        return Right(currentScope.variables[name]!!)
      }
      //else if (currentScope.functions.contains(name)) {
      //  TODO()
      //}
      currentScopeOffset -= 1
    }
    return Left(ScriptError("Could not find $name in scope."))
  }
}

//TODO this function will probably be used once I allow for ad-hoc polymorphism with functions.
fun createFunctionDelegate(): NativeFunction =
  TODO()
