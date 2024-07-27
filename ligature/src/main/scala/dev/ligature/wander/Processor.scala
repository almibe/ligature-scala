/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.mutable.ListBuffer

//import scala.collection.mutable.ListBuffer
//import scala.util.boundary, boundary.break

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
    case Term.Slot(name)                      => Right(Expression.Slot(name))
    case Term.Array(terms)                    => processArray(terms)
    case Term.IntegerValue(value)             => Right(Expression.IntegerValue(value))
    case Term.FieldPathTerm(value)            => Right(Expression.FieldPathExpression(value))
    case Term.FieldTerm(value)                => Right(Expression.FieldExpression(value))
    case Term.StringValue(value, interpolate) => Right(Expression.StringValue(value, interpolate))
    case Term.Grouping(terms)                 => processGrouping(terms)
    case Term.Application(terms)              => processApplication(terms)
    case Term.Bytes(value)                    => Right(Expression.Bytes(value))
    case Term.Word(value)               => Right(Expression.Word(value))
    case Term.Network(roots)                    => processNetwork(roots)
    case Term.NetworkRoot(_)                    => ??? // TODO probably return error?
  }

def processNetwork(terms: Seq[Term.NetworkRoot]): Either[WanderError, Expression.Network] =
  
  terms match {
    case Seq(
          Term.NetworkRoot(
            Seq(Term.Word(entity), Term.Word(attribute), Term.Word(value))
          )
        ) =>
      Right(
        Expression.Network(
          Seq(
            Expression.Word(entity),
            Expression.Word(attribute),
            Expression.Word(value)
          )
        )
      )
    case _ => ???
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

// def processModule(values: Seq[(dev.ligature.wander.Field, Term)]): Either[WanderError, Expression] =
//   boundary:
//     val results = ListBuffer[(Field, Expression)]()
//     values.foreach((name, value) =>
//       process(value) match
//         case Left(err)    => break(Left(err))
//         case Right(value) => results.append((name, value))
//     )
//     Right(Expression.Module(results.toSeq))

// def processLambda(parameters: Seq[Field], body: Term): Either[WanderError, Expression.Lambda] =
//   process(body) match {
//     case Left(err)    => Left(err)
//     case Right(value) => Right(Expression.Lambda(parameters, value))
//   }

// def processBinding(
//     name: Field,
//     tag: Option[FieldPath],
//     value: Term
// ): Either[WanderError, Expression.Binding] =
//   process(value) match {
//     case Left(err)         => ???
//     case Right(expression) => Right(Expression.Binding(name, tag, expression))
//   }

def processArray(terms: Seq[Term]): Either[WanderError, Expression.Array] = {
  val expressions = terms.map { t =>
    process(t) match {
      case Left(err)    => ???
      case Right(value) => value
    }
  }
  Right(Expression.Array(expressions))
}
