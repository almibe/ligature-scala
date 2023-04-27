/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

def eval(input: Seq[Expression]): Either[WanderError, WanderValue] =
  if input.isEmpty then Right(WanderValue.Nothing)
  else
    input.last match
      case Expression.Integer(i)        => Right(WanderValue.Integer(i))
      case Expression.StringLiteral(s)  => Right(WanderValue.StringLiteral(s))
      case Expression.Nothing           => Right(WanderValue.Nothing)
      case Expression.BooleanLiteral(b) => Right(WanderValue.BooleanLiteral(b))
      case Expression.Identifier(i)     => Right(WanderValue.Identifier(i))
      case _                            => Left(WanderError("Could not eval."))
