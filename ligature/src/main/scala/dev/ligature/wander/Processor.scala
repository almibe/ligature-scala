/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec

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
    case Term.Quote(terms)                    => Right(processQuote(terms))
    case Term.Int(value)             => Right(Expression.Int(value))
    case Term.StringValue(value) => Right(Expression.StringValue(value))
    case Term.Grouping(terms)                 => processGrouping(terms)
    case Term.Application(terms)              => processApplication(terms)
    case Term.Bytes(value)                    => Right(Expression.Bytes(value))
    case Term.Word(value)               => Right(Expression.Word(value))
    case Term.Network(triples)                    => processNetwork(triples, Array())
    case Term.Triple(_, _, _)                    => ???
  }

def processTriple(triple: Term.Triple): Either[WanderError, Expression.Triple] =
  triple match {
    case Term.Triple(Term.Word(entity), Term.Word(attribute), value) =>
      val ligatureValue: Expression = value match {
        case Term.Application(terms) => ???
        case Term.Word(word) => Expression.Word(word)
        case Term.Bytes(_) => ???
        case Term.Int(int) => Expression.Int(int)
        case Term.StringValue(value) => Expression.StringValue(value)
        case Term.Slot(slot) => Expression.Slot(slot)
        case Term.Quote(quote) => processQuote(quote)//Expression.Quote(quote)
        case Term.Triple(_, _, _) => ???
        case Term.Network(_) => ???
        case Term.Grouping(_) => ???
      }
      Right(
        Expression.Triple(
          Expression.Word(entity),
          Expression.Word(attribute),
          ligatureValue
        ))
    case _ => Left(WanderError("Could not parse triple."))
  }

@tailrec
def processNetwork(terms: Seq[Term.Triple], results: Array[Expression.Triple]): Either[WanderError, Expression.Network] =
  terms match
    case Nil => Right(Expression.Network(results.toIndexedSeq))
    case head :: next =>
      processTriple(head) match
        case Left(value) => Left(value)
        case Right(value) => processNetwork(next, results :+ value)  

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

def processQuote(terms: Seq[Term]): Expression.Quote = {
  val expressions = terms.map { t =>
    process(t) match {
      case Left(err)    => ???
      case Right(value) => value
    }
  }
  Expression.Quote(expressions)
}

// def processModule(values: Seq[(Field, Term)]): Either[WanderError, Expression] =
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
