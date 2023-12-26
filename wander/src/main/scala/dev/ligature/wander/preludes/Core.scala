/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.preludes

import dev.ligature.wander.Environment
import dev.ligature.wander.HostFunction
import dev.ligature.wander.WanderValue
import dev.ligature.wander.TaggedName
import dev.ligature.wander.Name
import dev.ligature.wander.Tag

def bindCorePrelude(environment: Environment) =
  environment.addHostFunctions(
    Seq(
      HostFunction(
        "Core.eq",
        "Check if two values are equal.",
        Seq(
          TaggedName(Name("left"), Tag.Single(Name("Core.Any"))),
          TaggedName(Name("right"), Tag.Single(Name("Core.Any")))
        ),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(first, second) =>
              val res = first.asInstanceOf[WanderValue] == second.asInstanceOf[WanderValue]
              Right((WanderValue.Bool(res), environment))
            case _ => ???
      ),
      HostFunction(
        "Core.Any",
        "Checks if a value is an Any.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          Right((WanderValue.Bool(true), environment))
      ),
      HostFunction(
        "Core.Int",
        "Check if a value is an Int.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(WanderValue.Int(_)) => Right((WanderValue.Bool(true), environment))
            case Seq(_)                  => Right((WanderValue.Bool(false), environment))
            case _                       => ???
      ),
      HostFunction(
        "Core.Bool",
        "Check if a value is a Bool.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(WanderValue.Bool(_)) => Right((WanderValue.Bool(true), environment))
            case Seq(_)                   => Right((WanderValue.Bool(false), environment))
            case _                        => ???
      ),
      HostFunction(
        "Core.Record",
        "Check if a value is a Record.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(WanderValue.Record(_)) => Right((WanderValue.Bool(true), environment))
            case Seq(_)                     => Right((WanderValue.Bool(false), environment))
            case _                          => ???
      ),
      HostFunction(
        "Core.Array",
        "Check if a value is an Array.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(WanderValue.Array(_)) => Right((WanderValue.Bool(true), environment))
            case Seq(_)                    => Right((WanderValue.Bool(false), environment))
            case _                         => ???
      ),
      HostFunction(
        "Core.String",
        "Check if a value is a String.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(WanderValue.String(_)) => Right((WanderValue.Bool(true), environment))
            case Seq(_)                     => Right((WanderValue.Bool(false), environment))
            case _                          => ???
      ),
      HostFunction(
        "Core.Nothing",
        "Check if a value is Nothing.",
        Seq(TaggedName(Name("value"), Tag.Single(Name("Core.Any")))),
        Name("Core.Bool"),
        (args: Seq[WanderValue], environment: Environment) =>
          args match
            case Seq(WanderValue.Nothing) => Right((WanderValue.Bool(true), environment))
            case Seq(_)                     => Right((WanderValue.Bool(false), environment))
            case _                          => ???
      )
    )
  )
