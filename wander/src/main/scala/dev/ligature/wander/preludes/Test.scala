/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.preludes

import dev.ligature.wander.HostFunction
import dev.ligature.wander.Environment
import dev.ligature.wander.*

val testingHostFunctions: Seq[HostFunction] = Seq(
  HostFunction(
    "Test.assertEq",
    (arguments, environment) =>
      arguments match {
        case Seq(description: Expression.StringValue, left: Expression, right: Expression) => {
          val l = eval(left, environment)
          val r = eval(right, environment)
          if l != r then
            Left(WanderError(s"$description failed $l != $r"))
          else
            Right(WanderValue.Nothing, environment)
        }
        case _ => Left(WanderError(s"Invalid call to Test.assertEq: $arguments"))
      }
  )
)
