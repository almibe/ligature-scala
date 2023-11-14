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
    terms(0) match {
      case Term.NothingLiteral => Right(Expression.Nothing)
      case Term.Pipe => ???
      case Term.QuestionMark => Right(Expression.Nothing)
      case Term.IdentifierLiteral(value) => Right(Expression.IdentifierValue(value))
      case Term.Array(value) => ???
      case Term.Set(value) => ???
      case Term.BooleanLiteral(value) => Right(Expression.BooleanValue(value))
      case Term.Record(decls) => ???
      case Term.LetExpression(decls, body) => ???
      case Term.IntegerLiteral(value) => Right(Expression.IntegerValue(value))
      case Term.NameTerm(value) => Right(Expression.NameExpression(value))
      case Term.StringLiteral(value) => Right(Expression.StringValue(value))
      case Term.Application(terms) => ???
      case Term.Lambda(parameters, body) => ???
      case Term.IfExpression(conditional, ifBody, elseBody) => ???
  }
}
