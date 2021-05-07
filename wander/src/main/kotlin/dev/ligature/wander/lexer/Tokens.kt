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

object NewLineToken: WanderTokenValue()

/**
 * Identifiers are used for user defined names like variables, traits, and functions.
 */
data class Identifier(val name: String): WanderTokenValue()

/**
 * Symbols are used to represent punctuation and operators.
 */
sealed class Symbol: WanderTokenValue()
object OpenParen: Symbol()
object CloseParen: Symbol()
object OpenBrace: Symbol()
object CloseBrace: Symbol()
object AssignmentOperator: Symbol()
object Dot: Symbol()

/**
 * Keywords are defined by the language like let, trait, and when.
 */
sealed class Keyword: WanderTokenValue()
object LetKeyword: Keyword()
object TraitKeyword: Keyword()
object WhenKeyword: Keyword()

sealed class Primitive: WanderTokenValue()
data class IntegerPrimitive(val value: IntegerLiteral): Primitive()
data class FloatPrimitive(val value: FloatLiteral): Primitive()
data class StringPrimitive(val value: StringLiteral): Primitive()
data class EntityPrimitive(val value: Entity): Primitive()
data class AttributePrimitive(val value: Attribute): Primitive()
data class ValuePrimitive(val value: Value): Primitive()
data class BooleanPrimitive(val value: Boolean): Primitive()
object UnitPrimitive: Primitive()
