/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.preludes

import dev.ligature.wander.Bindings
import dev.ligature.wander.Token
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Term
import dev.ligature.wander.Name
import dev.ligature.wander.WanderError
import dev.ligature.wander.eval
import dev.ligature.wander.Expression

def bindBooleanPrelude(bindings: Bindings): Bindings = {
  var stdLib = bindings
    .bindVariable(
      Name("Bool.not"),
      WanderValue.HostFunction(
        (arguments, bindings) =>
          if arguments.size != 1 then
            Left(WanderError("`not` function requires 1 argument."))
          else
            eval(arguments.head, bindings).map { _ match
                case WanderValue.BooleanValue(b) => WanderValue.BooleanValue(!b)
                case _ => throw WanderError("`not` function requires 1 boolean argument.")
            }
      )
    )

//   stdLib = stdLib
//     .bindVariable(
//       Name("and"),
//       WanderValue.HostFunction(
//         (arguments: Seq[Expression], bindings: Bindings) => ???
//           // if arguments.length == 2 then
//           //   val res = for {
//           //     left <- evalTerm(arguments(0), bindings)
//           //     right <- evalTerm(arguments(1), bindings)
//           //   } yield (left, right)
//           //   res.map { r =>
//           //     (r._1.result, r._2.result) match
//           //       case (WanderValue.BooleanValue(left), WanderValue.BooleanValue(right)) => WanderValue.BooleanValue(left && right)
//           //       case _ => throw LigatureError("`and` function requires two booleans")
//           //   }
//           // else
//           //   IO.raiseError(LigatureError("`and` function requires two booleans"))
//       )
//     )
//     .getOrElse(???)

//   stdLib = stdLib
//     .bindVariable(
//       Name("or"),
//       WanderValue.HostFunction(
//         (arguments: Seq[Expression], bindings: Bindings) => ???
//           // if arguments.length == 2 then
//           //   val res = for {
//           //     left <- evalTerm(arguments(0), bindings)
//           //     right <- evalTerm(arguments(1), bindings)
//           //   } yield (left, right)
//           //   res.map { r =>
//           //     (r._1.result, r._2.result) match
//           //       case (WanderValue.BooleanValue(left), WanderValue.BooleanValue(right)) => WanderValue.BooleanValue(left || right)
//           //       case _ => throw LigatureError("`or` function requires two booleans")
//           //   }
//           // else
//           //   Left(WanderError("`or` function requires two booleans")))
//       )
//     )
//     .getOrElse(???)
  stdLib
}