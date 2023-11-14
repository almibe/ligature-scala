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

def process(terms: Seq[Term]): Either[WanderError, Expression] = {
  if terms.isEmpty then
    Right(Expression.Nothing)
  else
    process(terms(0))
}

def process(term: Term): Either[WanderError, Expression] =
  term match {
    case Term.NothingLiteral => Right(Expression.Nothing)
    case Term.Pipe => ???
    case Term.QuestionMark => Right(Expression.Nothing)
    case Term.IdentifierLiteral(value) => Right(Expression.IdentifierValue(value))
    case Term.Array(terms) => processArray(terms)
    case Term.Set(terms) => processSet(terms)
    case Term.BooleanLiteral(value) => Right(Expression.BooleanValue(value))
    case Term.Record(decls) => processRecord(decls)
    case Term.LetExpression(decls, body) => ???
    case Term.IntegerLiteral(value) => Right(Expression.IntegerValue(value))
    case Term.NameTerm(value) => Right(Expression.NameExpression(value))
    case Term.StringLiteral(value) => Right(Expression.StringValue(value))
    case Term.Application(terms) => ???
    case Term.Lambda(parameters, body) => ???
    case Term.IfExpression(conditional, ifBody, elseBody) => ???
  }

def processRecord(decls: Seq[(Name, Term)]): Either[WanderError, Expression.Record] = {
  val expressions = decls.map((name, term) => {
    process(term) match {
      case Left(value) => ???
      case Right(expression) => (name, expression)
    }
  })
  Right(Expression.Record(expressions))
}

def processArray(terms: Seq[Term]): Either[WanderError, Expression.Array] = {
  val expressions = terms.map(t => {
    process(t) match {
      case Left(value) => ???
      case Right(value) => value
    }
  })
  Right(Expression.Array(expressions))
}

def processSet(terms: Seq[Term]): Either[WanderError, Expression.Set] = {
  val expressions = terms.map(t => {
    process(t) match {
      case Left(value) => ???
      case Right(value) => value
    }
  })
  Right(Expression.Set(expressions))
}
