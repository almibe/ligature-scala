/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import dev.ligature.*

sealed class Primitive
data class IntegerPrimitive(val value: IntegerLiteral): Primitive()
data class FloatPrimitive(val value: FloatLiteral): Primitive()
data class StringPrimitive(val value: StringLiteral): Primitive()
data class EntityPrimitive(val value: Entity): Primitive()
data class AttributePrimitive(val value: Attribute): Primitive()
data class StatementPrimitive(val value: Statement): Primitive()
data class ValuePrimitive(val value: Value): Primitive()
data class BooleanPrimitive(val value: Boolean): Primitive()
object UnitPrimitive: Primitive()

sealed class ArgumentType

data class WanderFunction(val arguments: List<ArgumentType>, val body: (List<Primitive>) -> Primitive): Primitive()
