/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import dev.ligature.*

sealed class WanderValue
data class IntegerWanderValue(val value: IntegerLiteral): WanderValue()
data class FloatWanderValue(val value: FloatLiteral): WanderValue()
data class StringWanderValue(val value: StringLiteral): WanderValue()
data class EntityWanderValue(val value: Entity): WanderValue()
data class AttributeWanderValue(val value: Attribute): WanderValue()
data class StatementWanderValue(val value: Statement): WanderValue()
data class ValueWanderValue(val value: Value): WanderValue()
data class BooleanWanderValue(val value: Boolean): WanderValue()
object UnitWanderValue: WanderValue()

sealed class ArgumentType

data class WanderFunction(val arguments: List<ArgumentType>, val body: (List<WanderValue>) -> WanderValue): WanderValue()
