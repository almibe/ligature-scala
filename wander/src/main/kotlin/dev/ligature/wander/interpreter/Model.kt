/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import dev.ligature.*
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

sealed interface WanderValue
data class IntegerWanderValue(val value: IntegerLiteral): WanderValue
data class StringWanderValue(val value: StringLiteral): WanderValue
data class IdentifierWanderValue(val value: Identifier): WanderValue
data class StatementWanderValue(val value: Statement): WanderValue
data class ValueWanderValue(val value: Value): WanderValue
data class BooleanWanderValue(val value: Boolean): WanderValue
data class StreamWanderValue(val stream: Flow<WanderValue>): WanderValue
object UnitWanderValue: WanderValue

//TODO I'm not sure if I want the body to return just a WanderValue or an either w/ an Error case
data class WanderFunction(val arguments: List<KClass<out WanderValue>>,
                          val body: suspend (List<WanderValue>) -> Either<WanderError, WanderValue>): WanderValue

data class StatementQueryValue(val entity: Identifier?, val attribute: Identifier?, val value: ValueQueryType, val context: Identifier?): WanderValue
sealed interface ValueQueryType
data class ValueQuery(val value: Value?): ValueQueryType
data class RangeQuery(val range: Range): ValueQueryType
