/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.bend.printBendValue
import scala.util.boundary
import scala.util.boundary.break
import dev.ligature.bend.libraries.ModuleLibrary
import scala.collection.mutable.ListBuffer

case class Environment(
    libraries: Seq[ModuleLibrary] = Seq(),
    scopes: List[Map[Field, (Tag, BendValue)]] = List(Map())
) {
  def readAllBindings(): BendValue.Array = {
    val results = ListBuffer[BendValue]()
    // TODO query libraries
    scopes.foreach((scope: Map[Field, (Tag, BendValue)]) =>
      scope.foreach((k, v) =>
        results += BendValue.Array(
          Seq(BendValue.String(k.name), BendValue.String(printBendValue(v._2)))
        )
      )
    )
    BendValue.Array(results.toSeq)
  }

  def eval(expressions: Seq[Expression]): Either[BendError, (BendValue, Environment)] = {
    var env = this
    var lastResult: Option[BendValue] = None
    val err =
      boundary:
        expressions.foreach { expression =>
          dev.ligature.bend.eval(expression, env) match {
            case Left(value) => boundary.break(value)
            case Right((value, environment)) =>
              env = environment
              lastResult = Some(value)
          }
        }
    (lastResult, err) match {
      case (_, err: BendError) => Left(err)
      case (None, _)           => Right((BendValue.Module(Map()), env))
      case (Some(value), _)    => Right((value, env))
    }
  }

  def newScope(): Environment =
    Environment(
      this.libraries,
      this.scopes.appended(Map())
    )

  def bindVariable(
      field: Field,
      bendValue: BendValue
  ): Environment =
    val currentScope = this.scopes.last
    val newVariables = currentScope + (field -> (Tag.Untagged, bendValue))
    val oldScope = this.scopes.dropRight(1)
    Environment(this.libraries, oldScope.appended(newVariables))

  def bindVariable(
      taggedField: TaggedField,
      bendValue: BendValue
  ): Either[BendError, Environment] =
    this.checkTag(taggedField.tag, bendValue) match {
      case Left(value) => Left(value)
      case Right(value) =>
        val currentScope = this.scopes.last
        val newVariables = currentScope + (taggedField.field -> (taggedField.tag, bendValue))
        val oldScope = this.scopes.dropRight(1)
        Right(
          Environment(
            this.libraries,
            oldScope.appended(newVariables)
          )
        )
    }

  def read(field: Field): Either[BendError, Option[BendValue]] =
    var currentScopeOffset = this.scopes.length - 1
    while (currentScopeOffset >= 0) {
      val currentScope = this.scopes(currentScopeOffset)
      if (currentScope.contains(field)) {
        return Right(Some(currentScope(field)._2))
      }
      currentScopeOffset -= 1
    }
    Right(None)

  def read(fieldPath: FieldPath): Either[BendError, Option[BendValue]] =
    boundary:
      var result: Option[BendValue] = None
      fieldPath.parts.foreach(field =>
        if result.isEmpty then
          this.read(field) match
            case Left(err)    => break(Left(err))
            case Right(value) => result = value
        else
          result match
            case None => throw RuntimeException(s"Error trying to read $fieldPath\n$this")
            case Some(BendValue.Module(module)) =>
              if module.contains(field) then result = Some(module(field))
              else Left(BendError(s"Could not read field path, $fieldPath."))
            case _ => ???
      )
      Right(result)

  def importModule(fieldPath: FieldPath): Either[BendError, Environment] =
    var currentEnvironemnt = this
    boundary:
      this.read(fieldPath) match
        case Right(None) => ???
        case Left(value) => break(Left(value))
        case Right(Some(BendValue.Module(module))) =>
          module.foreach((k, v) => currentEnvironemnt = currentEnvironemnt.bindVariable(k, v))
        case _ => ???
    Right(currentEnvironemnt)

  def checkTag(tag: Tag, value: BendValue): Either[BendError, BendValue] =
    tag match {
      case Tag.Untagged    => Right(value)
      case Tag.Single(tag) => checkSingleTag(tag, value)
      case Tag.Chain(tags) => ??? /// checkFunctionTag(tags, value)
    }

  private def checkSingleTag(tag: Function, value: BendValue): Either[BendError, BendValue] =
    tag match {
      case hf: HostFunction =>
        hf.fn(Seq(value), this) match {
          case Right((BendValue.Bool(true), _)) => Right(value)
          case Right((BendValue.Bool(false), _)) =>
            Left(BendError("Value failed Tag Function."))
          case Left(err) => Left(err)
          case _         => Left(BendError("Invalid Tag, Tag Functions must return a Bool."))
        }
      case lambda: Lambda =>
        assert(lambda.lambda.parameters.size == 1)
        val environment = this.newScope()
        // environment = environment
        //   .bindVariable(TaggedField(lambda.lambda.parameters.head, Tag.Untagged), value)
        //   .getOrElse(???)
        dev.ligature.bend.eval(lambda.lambda.body, environment) match {
          case Right((BendValue.Bool(true), _)) => Right(value)
          case Right((BendValue.Bool(false), _)) =>
            Left(BendError("Value failed Tag Function."))
          case Left(err) => Left(err)
          case _         => Left(BendError("Invalid Tag, Tag Functions must return a Bool."))
        }
      case _ => Left(BendError(s"${tag} was not a valid tag."))
    }

  // private def checkFunctionTag(
  //     tags: Seq[Function],
  //     value: BendValue
  // ): Either[BendError, BendValue] =
  //   Right(value)
  //   ???
  // this.read(tag) match {
  //   case Right(BendValue.HostFunction(hf)) =>
  //     hf.fn(Seq(value), this) match {
  //       case Right((BendValue.Bool(true), _))  => Right(value)
  //       case Right((BendValue.Bool(false), _)) => Left(BendError("Value failed Tag Function."))
  //       case Left(err)                           => Left(err)
  //       case _ => Left(BendError("Invalid Tag, Tag Functions must return a Bool."))
  //     }
  //   case Right(BendValue.Lambda(lambda)) =>
  //     ???
  //   case Left(err) => Left(err)
  //   case _         => Left(BendError(s"${tag.name} was not a valid tag."))
  // }
}
