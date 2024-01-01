/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  optional,
  take,
  takeAll,
  takeCond,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  repeat
}
import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break

def process(terms: Seq[Term]): Either[WanderError, Seq[Expression]] =
  val expressions = terms.map(term =>
    process(term) match {
      case Left(err)    => ???
      case Right(value) => value
    }
  )
  Right(expressions)
  // if terms.isEmpty then Right(Expression.Nothing)
  // else process(terms(0))

def process(term: Term): Either[WanderError, Expression] =
  term match {
    case Term.Import(name)                      => Right(Expression.Import(name))
    case Term.NothingLiteral                    => Right(Expression.Nothing)
    case Term.Pipe                              => ???
    case Term.QuestionMark                      => Right(Expression.QuestionMark)
    case Term.Array(terms)                      => processArray(terms)
    case Term.BooleanLiteral(value)             => Right(Expression.BooleanValue(value))
    case Term.Binding(name, value, exportName)  => processBinding(name, value)
    case Term.IntegerLiteral(value)             => Right(Expression.IntegerValue(value))
    case Term.NameTerm(value)                   => Right(Expression.NameExpression(value))
    case Term.StringLiteral(value, interpolate) => Right(Expression.StringValue(value, interpolate))
    case Term.Lambda(parameters, body)          => processLambda(parameters, body)
    case Term.Grouping(terms)                   => processGrouping(terms)
    case Term.WhenExpression(conditionals)      => processWhenExpression(conditionals)
    case Term.Application(terms)                => processApplication(terms)
    case Term.Record(values)                    => processRecord(values)
  }

def processGrouping(terms: Seq[Term]): Either[WanderError, Expression.Grouping] = {
  var error: Option[WanderError] = None
  val res = ListBuffer[Expression]()
  val itr = terms.iterator
  while error.isEmpty && itr.hasNext do
    process(itr.next()) match {
      case Left(err)    => error = Some(err)
      case Right(value) => res += value
    }
  if error.isDefined then Left(error.get)
  else Right(Expression.Grouping(res.toSeq))
}

def processApplication(terms: Seq[Term]): Either[WanderError, Expression.Application] = {
  var error: Option[WanderError] = None
  val res = ListBuffer[Expression]()
  val itr = terms.iterator
  while error.isEmpty && itr.hasNext do
    process(itr.next()) match {
      case Left(err)    => error = Some(err)
      case Right(value) => res += value
    }
  if error.isDefined then Left(error.get)
  else Right(Expression.Application(res.toSeq))
}

def processWhenExpression(
    conditionals: Seq[(Term, Term)]
): Either[WanderError, Expression.WhenExpression] =
  boundary:
    val expressionConditionals = conditionals.map { (c, b) =>
      val conditional = process(c) match {
        case Left(err)    => break(Left(err))
        case Right(value) => value
      }
      val body = process(b) match {
        case Left(err)    => break(Left(err))
        case Right(value) => value
      }
      (conditional, body)
    }
    Right(Expression.WhenExpression(expressionConditionals))

def processRecord(values: Seq[(Name, Term)]): Either[WanderError, Expression] =
  boundary:
    val results = ListBuffer[(Name, Expression)]()
    values.foreach((name, value) =>
      process(value) match
        case Left(err)    => break(Left(err))
        case Right(value) => results.append((name, value))
    )
    Right(Expression.Record(results.toSeq))

def processLambda(parameters: Seq[Name], body: Term): Either[WanderError, Expression.Lambda] =
  process(body) match {
    case Left(err)    => Left(err)
    case Right(value) => Right(Expression.Lambda(parameters, value))
  }

def processBinding(name: TaggedName, value: Term): Either[WanderError, Expression.Binding] =
  process(value) match {
    case Left(err)         => ???
    case Right(expression) => Right(Expression.Binding(name, expression))
  }

def processArray(terms: Seq[Term]): Either[WanderError, Expression.Array] = {
  val expressions = terms.map { t =>
    process(t) match {
      case Left(err)    => ???
      case Right(value) => value
    }
  }
  Right(Expression.Array(expressions))
}
