/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import scala.collection.mutable.Set
import scala.util.boundary
import scala.util.boundary.break

case class Environment(
    functions: List[HostFunction] = List(),
    properties: List[HostProperty] = List(),
    scopes: List[Map[Name, (Tag, WanderValue)]] = List(Map())
) {
  def eval(expressions: Seq[Expression]): Either[WanderError, (WanderValue, Environment)] = {
    var env = this
    var lastResult: Option[WanderValue] = None
    val err =
      boundary:
        expressions.foreach { expression =>
          dev.ligature.wander.eval(expression, env) match {
            case Left(value) => boundary.break(value)
            case Right((value, environment)) =>
              env = environment
              lastResult = Some(value)
          }
        }
    (lastResult, err) match {
      case (_, err: WanderError) => Left(err)
      case (None, _)             => Right((WanderValue.Nothing, env))
      case (Some(value), _)      => Right((value, env))
    }
  }

  def newScope(): Environment =
    Environment(
      this.functions,
      this.properties,
      this.scopes.appended(Map())
    )

  def bindVariable(
      taggedName: TaggedName,
      wanderValue: WanderValue
  ): Either[WanderError, Environment] =
    this.checkTag(taggedName.tag, wanderValue) match {
      case Left(value) => Left(value)
      case Right(value) =>
        val currentScope = this.scopes.last
        val newVariables = currentScope + (taggedName.name -> (taggedName.tag, wanderValue))
        val oldScope = this.scopes.dropRight(1)
        Right(
          Environment(
            this.functions,
            this.properties,
            oldScope.appended(newVariables)
          )
        )
    }

  def read(name: Name): Either[WanderError, WanderValue] = {
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.contains(name)) {
        return Right(currentScope(name)._2)
      }
      currentScopeOffset -= 1
    }
    this.functions.find(_.name == name) match {
      case None           => ()
      case Some(function) => return Right(WanderValue.Function(function))
    }
    this.properties.find(_.name == name.name) match {
      case None                                  => ()
      case Some(HostProperty(_, _, _, property)) => return property(this).map(value => value._1)
    }

    Left(WanderError(s"Could not find ${name} in scope."))
  }

  def addHostFunctions(functions: Seq[HostFunction]): Environment =
    this.copy(functions = this.functions ++ functions)

  def addHostProperties(properties: Seq[HostProperty]): Environment =
    this.copy(properties = this.properties ++ properties)

  def checkTag(tag: Tag, value: WanderValue): Either[WanderError, WanderValue] =
    tag match {
      case Tag.Untagged       => Right(value)
      case Tag.Single(tag)    => checkSingleTag(tag, value)
      case Tag.Function(tags) => checkFunctionTag(tags, value)
    }

  private def checkSingleTag(tag: Name, value: WanderValue): Either[WanderError, WanderValue] =
    this.read(tag) match {
      case Right(WanderValue.Function(hf: HostFunction)) =>
        hf.fn(Seq(value), this) match {
          case Right((WanderValue.Bool(true), _)) => Right(value)
          case Right((WanderValue.Bool(false), _)) =>
            Left(WanderError("Value failed Tag Function."))
          case Left(err) => Left(err)
          case _         => Left(WanderError("Invalid Tag, Tag Functions must return a Bool."))
        }
      case Right(WanderValue.Function(lambda: Lambda)) =>
        assert(lambda.lambda.parameters.size == 1)
        var environment = this.newScope()
        environment = environment
          .bindVariable(TaggedName(lambda.lambda.parameters.head, Tag.Untagged), value)
          .getOrElse(???)
        dev.ligature.wander.eval(lambda.lambda.body, environment) match {
          case Right((WanderValue.Bool(true), _)) => Right(value)
          case Right((WanderValue.Bool(false), _)) =>
            Left(WanderError("Value failed Tag Function."))
          case Left(err) => Left(err)
          case _         => Left(WanderError("Invalid Tag, Tag Functions must return a Bool."))
        }
      case Left(err) => Left(err)
      case _         => Left(WanderError(s"${tag} was not a valid tag."))
    }

  private def checkFunctionTag(
      tags: Seq[Name],
      value: WanderValue
  ): Either[WanderError, WanderValue] =
    Right(value)
    // this.read(tag) match {
    //   case Right(WanderValue.HostFunction(hf)) =>
    //     hf.fn(Seq(value), this) match {
    //       case Right((WanderValue.Bool(true), _))  => Right(value)
    //       case Right((WanderValue.Bool(false), _)) => Left(WanderError("Value failed Tag Function."))
    //       case Left(err)                           => Left(err)
    //       case _ => Left(WanderError("Invalid Tag, Tag Functions must return a Bool."))
    //     }
    //   case Right(WanderValue.Lambda(lambda)) =>
    //     ???
    //   case Left(err) => Left(err)
    //   case _         => Left(WanderError(s"${tag.name} was not a valid tag."))
    // }
}
