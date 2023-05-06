/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.Token
import dev.ligature.wander.{
  BooleanValue,
  Else,
  ElseIf,
  FunctionCall,
  IfExpression,
  LetStatement,
  LigatureValue,
  Name,
  Nothing,
  Parameter,
  Scope,
  Script,
  ScriptError,
  ScriptResult,
  WanderFunction,
  WanderValue
}
import dev.ligature.{Dataset, Identifier, IntegerLiteral, StringLiteral}

case class TestData(
    category: String,
    dataset: Dataset,
    testInstances: List[TestInstance]
)

case class TestInstance(
    description: String,
    script: String,
    tokens: List[Token],
    ast: Script,
    result: Either[ScriptError, ScriptResult]
)

val errorsExpression = List()

//NOTE: New lines are hard coded as \n because sometimes on Windows
//the two types of new lines get mixed up in the codebase between the editor and Scalafmt.
//Not ideal, but it works consistently at least.
val newLine = "\n" //sys.props("line.separator")

val testData = List(
  // TestData(
  //   category = "Primitives",
  //   dataset = Dataset.fromString("test").getOrElse(???),
  //   testInstances = primitivesTestData
  // ),
  // TestData(
  //   category = "Assignment",
  //   dataset = Dataset.fromString("test").getOrElse(???),
  //   testInstances = assignmentTestData
  // ),
  // TestData(
  //   category = "Closures",
  //   dataset = Dataset.fromString("test").getOrElse(???),
  //   testInstances = closureTestData
  // ),
  // TestData(
  //   category = "Boolean Functions",
  //   dataset = Dataset.fromString("test").getOrElse(???),
  //   testInstances = booleanExpression
  // ),
  // TestData(
  //   category = "If Expressions",
  //   dataset = Dataset.fromString("test").getOrElse(???),
  //   testInstances = ifExpression
  // )
  // TODO add error cases
)
