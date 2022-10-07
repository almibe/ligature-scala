/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import arrow.core.getOrElse
import dev.ligature.Dataset

data class TestData(
    val category: String,
    val dataset: Dataset,
    val testInstances: List<TestInstance>
)

data class TestInstance(
  val description: String,
  val script: String,
  val result: Either<EvalError, Value>
)

//NOTE: New lines are hard coded as \n because sometimes on Windows
//the two types of new lines get mixed up in the codebase between the editor and Scalafmt.
//Not ideal, but it works consistently at least.
val newLine = "\n" //sys.props("line.separator")

val testData = listOf(
  TestData(
    category = "Primitives",
    dataset = Dataset.create("test").getOrElse { throw Error("Unexpected error.") },
    testInstances = primitivesTestData
  ),
  TestData(
    category = "Assignment",
    dataset = Dataset.create("test").getOrElse { throw Error("Unexpected error.") },
    testInstances = assignmentTestData
  ),
  TestData(
    category = "Closures",
    dataset = Dataset.create("test").getOrElse { throw Error("Unexpected error.") },
    testInstances = closureTestData
  ),
  TestData(
    category = "Boolean Functions",
    dataset = Dataset.create("test").getOrElse { throw Error("Unexpected error.") },
    testInstances = booleanExpression
  ),
  TestData(
    category = "If Expressions",
    dataset = Dataset.create("test").getOrElse { throw Error("Unexpected error.") },
    testInstances = ifExpression
  )
  // TODO add error cases
)
