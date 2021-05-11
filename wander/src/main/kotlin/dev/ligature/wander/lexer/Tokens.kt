/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.lexer

import dev.ligature.*
import dev.ligature.wander.writer.Writer

data class WanderToken(
    val offset: Int,
    val token: WanderTokenValue,
) {
    val debug: String get() = token.debug
}

sealed class WanderTokenValue {
    abstract val debug: String
}

object EndOfScriptToken: WanderTokenValue() { override val debug = "End Of Script Token" }
object NewLineToken: WanderTokenValue() { override val debug = "New Line Token" }

/**
 * Identifiers are used for user defined names like variables, traits, and functions.
 */
data class Identifier(val name: String): WanderTokenValue() { override val debug = "Identifier($name)" }

/**
 * Symbols are used to represent punctuation and operators.
 */
sealed class Symbol: WanderTokenValue()
object OpenParen: Symbol() { override val debug: String = "(" }
object CloseParen: Symbol() { override val debug: String = ")" }
object OpenBrace: Symbol() { override val debug: String = "{" }
object CloseBrace: Symbol() { override val debug: String = "}" }
object AssignmentOperator: Symbol() { override val debug: String = "=" }
object Dot: Symbol() { override val debug: String = "." }

/**
 * Keywords are defined by the language like let, trait, and when.
 */
sealed class Keyword: WanderTokenValue()
object LetKeyword: Keyword() { override val debug: String = "let" }
object TraitKeyword: Keyword() { override val debug: String = "trait" }
object WhenKeyword: Keyword() { override val debug: String = "when" }

private val writer = Writer()

sealed class Primitive: WanderTokenValue()
data class IntegerPrimitive(val value: IntegerLiteral): Primitive() { override val debug = value.value.toString() }
data class FloatPrimitive(val value: FloatLiteral): Primitive() { override val debug = value.value.toString() }
data class StringPrimitive(val value: StringLiteral): Primitive() { override val debug = value.value }
data class EntityPrimitive(val value: Entity): Primitive() { override val debug = "Entity(${value.id})" }
data class AttributePrimitive(val value: Attribute): Primitive() { override val debug = "Attribute(${value.name})" }
data class ValuePrimitive(val value: Value): Primitive() { override val debug = TODO() }
data class BooleanPrimitive(val value: Boolean): Primitive() { override val debug = value.toString() }
object UnitPrimitive: Primitive() { override val debug = "Unit Token" }
