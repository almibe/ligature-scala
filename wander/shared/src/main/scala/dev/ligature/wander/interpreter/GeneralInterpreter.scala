/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import dev.ligature.wander.*
import dev.ligature.wander.interpreter.*
import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break

class GeneralInterpreter extends Interpreter {
  def eval(
      expression: Expression,
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] =
    expression match {
      case Expression.Nothing             => Right((WanderValue.Nothing, environment))
      case Expression.BooleanValue(value) => Right((WanderValue.BooleanValue(value), environment))
      case Expression.IntegerValue(value) => Right((WanderValue.IntValue(value), environment))
      case Expression.StringValue(value)  => Right((WanderValue.StringValue(value), environment))
      case Expression.IdentifierValue(value) => Right((WanderValue.Identifier(value), environment))
      case Expression.Array(value)           => handleArray(value, environment)
      case Expression.NameExpression(name)   => environment.read(name).map((_, environment))
      case Expression.LetExpression(name, value) => handleLetExpression(name, value, environment)
      case lambda: Expression.Lambda             => Right((WanderValue.Lambda(lambda), environment))
      case Expression.WhenExpression(conditionals) =>
        handleWhenExpression(conditionals, environment)
      case Expression.Grouping(expressions) => handleGrouping(expressions, environment)
      case Expression.QuestionMark          => Right((WanderValue.QuestionMark, environment))
    }

  def handleQuery(
      entity: WanderValue,
      attribute: WanderValue,
      value: WanderValue,
      graphName: String,
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] = {
    val e = entity match {
      case WanderValue.QuestionMark      => None
      case WanderValue.Identifier(value) => Some(value)
      case _                             => ???
    }
    val a = attribute match {
      case WanderValue.QuestionMark      => None
      case WanderValue.Identifier(value) => Some(value)
      case _                             => ???
    }
    val v = value match {
      case WanderValue.QuestionMark => None
      case value                    => Some(value)
    }
    if (environment.graphs.contains(graphName)) {
      val graph = environment.graphs(graphName)
      val filtered = graph
        .filter { statement =>
          val ee = e match {
            case None        => true
            case Some(value) => statement.entity == value
          }
          val aa = a match {
            case None        => true
            case Some(value) => statement.attribute == value
          }
          val vv = v match {
            case None        => true
            case Some(value) => statement.value == value
          }
          ee && aa && vv
        }
        .map { statement =>
          WanderValue.Triple(statement.entity, statement.attribute, statement.value)
        }
      Right((WanderValue.Array(filtered.toSeq), environment))
    } else {
      Right((WanderValue.Array(Seq()), environment))
    }
  }

  def handleTriple(
      entity: Expression,
      attribute: Expression,
      value: Expression,
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] = {
    val res = for {
      entityRes <- eval(entity, environment)
      attributeRes <- eval(attribute, environment)
      valueRes <- eval(value, environment)
    } yield (entityRes._1, attributeRes._1, valueRes._1)
    res match {
      case Left(value) => Left(value)
      case Right(value) =>
        value match {
          case (
                WanderValue.Identifier(entity),
                WanderValue.Identifier(attribute),
                value: WanderValue
              ) =>
            val triple: WanderValue.Triple = WanderValue.Triple(entity, attribute, value)
            environment.addTriple(triple)
            Right(triple, environment)
          case (e: WanderValue, a: WanderValue, v: WanderValue) =>
            handleQuery(e, a, v, "", environment)
        }
    }
  }

  def handleQuad(
      entity: Expression,
      attribute: Expression,
      value: Expression,
      graph: Expression,
      environment: Environment
  ): Either[WanderError, (WanderValue.Quad, Environment)] =
    for {
      entityRes <- eval(entity, environment)
      attributeRes <- eval(attribute, environment)
      valueRes <- eval(value, environment)
      graphRes <- eval(graph, environment)
    } yield (entityRes._1, attributeRes._1, valueRes._1, graphRes._1) match {
      case (
            WanderValue.Identifier(entity),
            WanderValue.Identifier(attribute),
            value,
            WanderValue.Identifier(graph)
          ) =>
        val quad: WanderValue.Quad = WanderValue.Quad(entity, attribute, value, graph)
        environment.addQuad(quad)
        (quad, environment)
      case _ => ???
    }

  def handleGrouping(
      expressions: Seq[Expression],
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] = {
    var error: Option[WanderError] = None
    var res: (WanderValue, Environment) = (WanderValue.Nothing, environment)
    val itr = expressions.iterator
    while error.isEmpty && itr.hasNext do
      eval(itr.next(), res._2) match {
        case Left(err)    => error = Some(err)
        case Right(value) => res = value
      }
    if error.isDefined then Left(error.get)
    else Right(res)
  }

  def handleLetExpression(
      name: Name,
      value: Expression,
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] = {
    var newScope = environment.newScope()
    eval(value, newScope) match {
      case Left(value) => ???
      case Right(value) =>
        newScope = newScope.bindVariable(name, value._1)
        Right((value._1, newScope))
    }
  }

  def handleApplication(
      name: Name,
      arguments: Seq[Expression],
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] =
    environment.read(name) match {
      case Left(err) => Left(err)
      case Right(value) =>
        value match {
          case WanderValue.Lambda(Expression.Lambda(parameters, body)) =>
            var fnScope = environment.newScope()
            assert(arguments.size == parameters.size)
            parameters.zipWithIndex.foreach { (param, index) =>
              val argument = eval(arguments(index), environment) match {
                case Left(value) => ???
                case Right(value) =>
                  fnScope = fnScope.bindVariable(param, value._1)
              }
            }
            eval(body, fnScope)
          case WanderValue.HostFunction(fn) => fn.fn(arguments, environment)
          case _ => Left(WanderError(s"Could not call function ${name.name}."))
        }
    }

  def handleWhenExpression(
      conditionals: Seq[(Expression, Expression)],
      environment: Environment
  ): Either[WanderError, (WanderValue, Environment)] =
    boundary:
      conditionals.find { (conditional, _) =>
        eval(conditional, environment) match {
          case Right((value, _)) =>
            value match {
              case WanderValue.BooleanValue(value) => value
              case _ => break(Left(WanderError("Conditionals must evaluate to Bool.")))
            }
          case Left(err) => break(Left(err))
        }
      } match {
        case None            => Left(WanderError("No matching cases."))
        case Some((_, body)) => eval(body, environment)
      }

  def handleArray(
      expressions: Seq[Expression],
      environment: Environment
  ): Either[WanderError, (WanderValue.Array, Environment)] = {
    val res = ListBuffer[WanderValue]()
    val itre = expressions.iterator
    var continue = true
    while continue && itre.hasNext
    do
      val expression = itre.next()
      eval(expression, environment) match
        case Left(err)    => return Left(err)
        case Right(value) => res += value._1
    Right((WanderValue.Array(res.toList), environment))
  }

  def handleSet(
      expressions: Seq[Expression],
      environment: Environment
  ): Either[WanderError, (WanderValue.Set, Environment)] = {
    val res = ListBuffer[WanderValue]()
    val itre = expressions.iterator
    var continue = true
    while continue && itre.hasNext
    do
      val expression = itre.next()
      eval(expression, environment) match
        case Left(err)    => return Left(err)
        case Right(value) => res += value._1
    Right((WanderValue.Set(res.toSet), environment))
  }
}
