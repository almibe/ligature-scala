/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import kotlin.reflect.KClass

class Bindings {
  data class BindingScope(val variables: MutableMap<String, Value> = mutableMapOf())
  private val scopes = mutableListOf<BindingScope>()

  init {
    addScope()
  }

  fun addScope() = this.scopes.add(BindingScope())
  fun removeScope() = this.scopes.removeLast()

  fun bindVariable(
      name: String,
      bindValue: Value
  ): Either<EvalError, Unit> {
    val currentScope = this.scopes.last()
    return if (currentScope.variables.contains(name)) {
      Left(EvalError("$name is already bound in current scope."))
    } else {
      currentScope.variables[name] = bindValue
      Right(Unit)
    }
  }

  inline fun <reified T: Value>read(name: String,
                                    @Suppress("UNUSED_PARAMETER") clazz: KClass<T>? = null
  ): Either<EvalError, T> =
    when (val value = readValue(name)) {
      is Right -> {
        if (value.value is T) Right(value as T)
        else Left(EvalError("Could not read $name with correct type, found ${value.value}."))
      }
      is Left -> value
    }

  fun readValue(name: String): Either<EvalError, Value> {
    var currentScopeOffset = this.scopes.size - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes[currentScopeOffset]
      if (currentScope.variables.contains(name)) {
        return Right(currentScope.variables[name]!!)
      }
      currentScopeOffset -= 1
    }
    return Left(EvalError("Could not find $name in scope."))
  }

  fun names(): Set<String> {
    val names = mutableSetOf<String>()
    scopes.forEach {
      names.addAll(it.variables.keys)
    }
    return names
  }
}
