/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import dev.ligature.*

data class WanderToken(
    val offset: Int,
    val token: WanderTokenValue
)

sealed class WanderTokenValue

/**
 * Symbols are used for user defined names like variables, traits, and functions.
 */
data class Symbol(val name: String): WanderTokenValue()

sealed class Punctuation: WanderTokenValue()
object OpenParen: Punctuation()
object CloseParen: Punctuation()
object OpenBrace: Punctuation()
object CloseBrace: Punctuation()

/**
 * Keywords are defined by the language like let, trait, and when.
 */
sealed class Keyword: WanderTokenValue()
object LetKeyword: Keyword()
object TraitKeyword: Keyword()
object WhenKeyword: Keyword()

/**
 * Operators are like keywords but are symbols instead of text.
 */
sealed class Operator: WanderTokenValue()
object AssignmentOperator: Operator()
object Dot: Operator()

sealed class Primitive: WanderTokenValue()
data class IntegerPrimitive(val value: IntegerLiteral): Primitive()
data class FloatPrimitive(val value: FloatLiteral): Primitive()
data class StringPrimitive(val value: StringLiteral): Primitive()
data class EntityPrimitive(val value: Entity): Primitive()
data class AttributePrimitive(val value: Attribute): Primitive()
data class ValuePrimitive(val value: Value): Primitive()
data class BooleanPrimitive(val value: Boolean): Primitive()
object UnitPrimitive: Primitive()
