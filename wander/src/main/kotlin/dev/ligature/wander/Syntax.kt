/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.*

data class Script(val lines: List<WanderStatement>)

sealed class WanderStatement
data class LetStatement(val symbol: Symbol, val assignment: Expression): WanderStatement()

sealed class Expression: WanderStatement()

data class Symbol(val name: String): Expression()

sealed class Primitive: Expression()
data class IntegerPrimitive(val value: IntegerLiteral): Primitive()
data class FloatPrimitive(val value: FloatLiteral): Primitive()
data class StringPrimitive(val value: StringLiteral): Primitive()
data class EntityPrimitive(val value: Entity): Primitive()
data class AttributePrimitive(val value: Attribute): Primitive()
data class ValuePrimitive(val value: Value): Primitive() //TODO is this needed?
data class BooleanPrimitive(val value: Boolean): Primitive()
object UnitPrimitive: Primitive()
