/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.library.common
import dev.ligature.wander.model.Element
import dev.ligature.wander.model.write

object WanderRepl {
  /**
   * Create a set of bindings that contains all common bindings and an additional binding that
   * returns a string of all Names that are bound.
   */
  private fun createBindings(): Bindings {
    val bindings = common()
    bindings.bindVariable(
        "bindings",
        Element.NativeFunction(listOf()) { _, _ ->
          Right(Element.StringLiteral(bindings.names().joinToString { it }))
        })
    return bindings
  }

  private var bindings = createBindings()

  fun restart() {
    bindings = createBindings()
  }

  suspend fun eval(input: String): String =
      when (val result = dev.ligature.wander.run(input, bindings)) {
        is Right -> "- ${write(result.value)}"
        is Left -> "X ${result.value.message}"
      }
}
