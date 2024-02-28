/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.gaze.{Nibbler, repeat}
import dev.ligature.gaze.{
  Gaze,
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

def process(terms: Seq[Term]): Either[BendError, Seq[Expression]] =
  val expressions = terms.map(term =>
    process(term) match {
      case Left(err)    => ???
      case Right(value) => value
    }
  )
  Right(expressions)
  // if terms.isEmpty then Right(Expression.Nothing)
  // else process(terms(0))

def process(term: Term): Either[BendError, Expression] =
  term match {
    case Term.Pipe                            => ???
    case Term.QuestionMark                    => Right(Expression.QuestionMark)
    case Term.Array(terms)                    => processArray(terms)
    case Term.BooleanLiteral(value)           => Right(Expression.BooleanValue(value))
    case Term.Binding(name, tag, value)       => processBinding(name, tag, value)
    case Term.IntegerValue(value)             => Right(Expression.IntegerValue(value))
    case Term.FieldPathTerm(value)            => Right(Expression.FieldPathExpression(value))
    case Term.FieldTerm(value)                => Right(Expression.FieldExpression(value))
    case Term.StringValue(value, interpolate) => Right(Expression.StringValue(value, interpolate))
    case Term.Lambda(parameters, body)        => processLambda(parameters, body)
    case Term.Grouping(terms)                 => processGrouping(terms)
    case Term.WhenExpression(conditionals)    => processWhenExpression(conditionals)
    case Term.Application(terms)              => processApplication(terms)
    case Term.Module(values)                  => processModule(values)
    case Term.Bytes(value)                    => Right(Expression.Bytes(value))
  }

def processGrouping(terms: Seq[Term]): Either[BendError, Expression.Grouping] = {
  var error: Option[BendError] = None
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

def processApplication(terms: Seq[Term]): Either[BendError, Expression.Application] = {
  var error: Option[BendError] = None
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
): Either[BendError, Expression.WhenExpression] =
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

def processModule(values: Seq[(dev.ligature.bend.Field, Term)]): Either[BendError, Expression] =
  boundary:
    val results = ListBuffer[(Field, Expression)]()
    values.foreach((name, value) =>
      process(value) match
        case Left(err)    => break(Left(err))
        case Right(value) => results.append((name, value))
    )
    Right(Expression.Module(results.toSeq))

def processLambda(parameters: Seq[Field], body: Term): Either[BendError, Expression.Lambda] =
  process(body) match {
    case Left(err)    => Left(err)
    case Right(value) => Right(Expression.Lambda(parameters, value))
  }

def processBinding(
    name: Field,
    tag: Option[FieldPath],
    value: Term
): Either[BendError, Expression.Binding] =
  process(value) match {
    case Left(err)         => ???
    case Right(expression) => Right(Expression.Binding(name, tag, expression))
  }

def processArray(terms: Seq[Term]): Either[BendError, Expression.Array] = {
  val expressions = terms.map { t =>
    process(t) match {
      case Left(err)    => ???
      case Right(value) => value
    }
  }
  Right(Expression.Array(expressions))
}
