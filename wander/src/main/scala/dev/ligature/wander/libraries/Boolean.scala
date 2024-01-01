/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.Environment
import dev.ligature.wander.Token
import dev.ligature.wander.WanderValue
import dev.ligature.wander.Term
import dev.ligature.wander.Name
import dev.ligature.wander.WanderError
import dev.ligature.wander.HostFunction
import dev.ligature.wander.TaggedName
import dev.ligature.wander.Tag

val boolLibrary = Seq(
  HostFunction(
    Name("Bool.not"),
    "Perform a not operation on a Bool value.",
    Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Bool")))),
    Tag.Single(Name("Core.Bool")),
    (args, environment) =>
      args match
        case Seq(WanderValue.Bool(value)) => Right((WanderValue.Bool(!value), environment))
        case _                            => ???
  )
)

//   stdLib = stdLib
//     .bindVariable(
//       Name("and"),
//       WanderValue.HostFunction(
//         (arguments: Seq[Expression], environment: Environment) => ???
//           // if arguments.length == 2 then
//           //   val res = for {
//           //     left <- evalTerm(arguments(0), environment)
//           //     right <- evalTerm(arguments(1), environment)
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
//         (arguments: Seq[Expression], environment: Environment) => ???
//           // if arguments.length == 2 then
//           //   val res = for {
//           //     left <- evalTerm(arguments(0), environment)
//           //     right <- evalTerm(arguments(1), environment)
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
