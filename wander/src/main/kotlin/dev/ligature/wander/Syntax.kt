/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.*

sealed class Expression

sealed class Primitive: Expression()
data class IntegerPrimitive(val integerLiteral: IntegerLiteral): Primitive()
data class FloatPrimitive(val floatLiteral: FloatLiteral): Primitive()
data class StringPrimitive(val stringLiteral: StringLiteral): Primitive()
data class EntityPrimitive(val entity: Entity): Primitive()
data class AttributePrimitive(val attribute: Attribute): Primitive()
data class ValuePrimitive(val value: Value): Primitive() //TODO is this needed?
data class BooleanPrimitive(val boolean: Boolean): Primitive()
