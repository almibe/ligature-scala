/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import dev.ligature.wander.library.common
import dev.ligature.wander.library.datasetQueryBindings
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.withClue
import dev.ligature.wander.run

class WanderSuite: FunSpec() {
  init {
    val testOnly: Set<String> = setOf() // if set is empty all tests will run

    fun runTest(description: String): Boolean =
      testOnly.isEmpty() || testOnly.contains(description)

    testData.forEach { testGroup ->
      testGroup.testInstances.forEach { instance ->
        if (runTest(instance.description)) {
          test(
            "Interpreting -- ${testGroup.category} -- ${instance.description}"
          ) {
            val result = run(instance.script, common())//datasetQueryBindings(testGroup.dataset))
            withClue("results are not the same for ${instance.description}") {
              result shouldBe instance.result
            }
          }
        }
      }
    }
  }
}
