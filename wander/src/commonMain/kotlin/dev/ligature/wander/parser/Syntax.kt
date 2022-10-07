/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.Identifier

sealed interface Element {
  data class LetStatement(val name: String, val value: Expression): Element
  sealed interface Expression: Element
  sealed interface Value: Expression
  data class Name(val name: String): Expression
  data class Scope(val body: List<Element>): Expression
  data class BooleanLiteral(val value: Boolean): Value
  data class StringLiteral(val value: String): Value
  data class IntegerLiteral(val value: Long): Value
  data class IdentifierLiteral(val value: Identifier): Value
  data class FunctionCall(val name: String, val arguments: List<Expression>): Expression
  data class LambdaDefinition(val parameters: List<String>, val body: List<Element>): Value
  data class Conditional(val condition: Expression, val body: Expression)
  data class IfExpression(val ifConditional: Conditional,
                          val elsifConditional: List<Conditional> = listOf(),
                          val elseBody: Expression? = null): Expression
  data class Seq(val values: List<Expression>): Value
  object Nothing: Expression
}
