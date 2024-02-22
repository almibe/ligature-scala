/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.printWanderValue
import scala.collection.mutable.Set
import scala.util.boundary
import scala.util.boundary.break
import dev.ligature.wander.libraries.ModuleLibrary
import scala.collection.mutable.ListBuffer

case class Environment(
    libraries: Seq[ModuleLibrary] = Seq(),
    scopes: List[Map[Field, (Tag, WanderValue)]] = List(Map())
) {
  def readAllBindings(): WanderValue.Array = {
    val results = ListBuffer[WanderValue]()
    // TODO query libraries
    scopes.foreach((scope: Map[Field, (Tag, WanderValue)]) =>
      scope.foreach((k, v) =>
        results += WanderValue.Array(
          Seq(WanderValue.String(k.name), WanderValue.String(printWanderValue(v._2)))
        )
      )
    )
    WanderValue.Array(results.toSeq)
  }

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
      case (None, _)             => Right((WanderValue.Module(Map()), env))
      case (Some(value), _)      => Right((value, env))
    }
  }

  def newScope(): Environment =
    Environment(
      this.libraries,
      this.scopes.appended(Map())
    )

  def bindVariable(
      field: Field,
      wanderValue: WanderValue
  ): Environment =
    val currentScope = this.scopes.last
    val newVariables = currentScope + (field -> (Tag.Untagged, wanderValue))
    val oldScope = this.scopes.dropRight(1)
    Environment(this.libraries, oldScope.appended(newVariables))

  def bindVariable(
      taggedField: TaggedField,
      wanderValue: WanderValue
  ): Either[WanderError, Environment] =
    this.checkTag(taggedField.tag, wanderValue) match {
      case Left(value) => Left(value)
      case Right(value) =>
        val currentScope = this.scopes.last
        val newVariables = currentScope + (taggedField.field -> (taggedField.tag, wanderValue))
        val oldScope = this.scopes.dropRight(1)
        Right(
          Environment(
            this.libraries,
            oldScope.appended(newVariables)
          )
        )
    }

  def read(field: Field): Either[WanderError, Option[WanderValue]] =
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.contains(field)) {
        return Right(Some(currentScope(field)._2))
      }
      currentScopeOffset -= 1
    }
    Right(None)

  def read(fieldPath: FieldPath): Either[WanderError, Option[WanderValue]] =
    boundary:
      var result: Option[WanderValue] = None
      fieldPath.parts.foreach(field =>
        if result.isEmpty then
          this.read(field) match
            case Left(err)    => break(Left(err))
            case Right(value) => result = value
        else
          result match
            case None => throw RuntimeException(s"Error trying to read $fieldPath\n$this")
            case Some(WanderValue.Module(module)) =>
              if module.contains(field) then result = Some(module(field))
              else Left(WanderError(s"Could not read field path, $fieldPath."))
            case _ => ???
      )
      Right(result)

  def importModule(fieldPath: FieldPath): Either[WanderError, Environment] =
    var currentEnvironemnt = this
    boundary:
      this.read(fieldPath) match
        case Right(None) => ???
        case Left(value) => break(Left(value))
        case Right(Some(WanderValue.Module(module))) =>
          module.foreach((k, v) => currentEnvironemnt = currentEnvironemnt.bindVariable(k, v))
        case _ => ???
    Right(currentEnvironemnt)

  def checkTag(tag: Tag, value: WanderValue): Either[WanderError, WanderValue] =
    tag match {
      case Tag.Untagged    => Right(value)
      case Tag.Single(tag) => checkSingleTag(tag, value)
      case Tag.Chain(tags) => ??? /// checkFunctionTag(tags, value)
    }

  private def checkSingleTag(tag: Function, value: WanderValue): Either[WanderError, WanderValue] =
    tag match {
      case hf: HostFunction =>
        hf.fn(Seq(value), this) match {
          case Right((WanderValue.Bool(true), _)) => Right(value)
          case Right((WanderValue.Bool(false), _)) =>
            Left(WanderError("Value failed Tag Function."))
          case Left(err) => Left(err)
          case _         => Left(WanderError("Invalid Tag, Tag Functions must return a Bool."))
        }
      case lambda: Lambda =>
        assert(lambda.lambda.parameters.size == 1)
        var environment = this.newScope()
        // environment = environment
        //   .bindVariable(TaggedField(lambda.lambda.parameters.head, Tag.Untagged), value)
        //   .getOrElse(???)
        dev.ligature.wander.eval(lambda.lambda.body, environment) match {
          case Right((WanderValue.Bool(true), _)) => Right(value)
          case Right((WanderValue.Bool(false), _)) =>
            Left(WanderError("Value failed Tag Function."))
          case Left(err) => Left(err)
          case _         => Left(WanderError("Invalid Tag, Tag Functions must return a Bool."))
        }
      case _ => Left(WanderError(s"${tag} was not a valid tag."))
    }

  private def checkFunctionTag(
      tags: Seq[Function],
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
