/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

def process(terms: Seq[Term]): Either[WanderError, Expression] = {
    terms(0) match {
        case Term.NothingLiteral => Right(Expression.Nothing)
        case Term.Pipe => ???
        case Term.QuestionMark => Right(Expression.Nothing)
        case Term.IdentifierLiteral(value) => Right(Expression.IdentifierValue(value))
        case dev.ligature.wander.Term.List(value) => ???
        case Term.BooleanLiteral(value) => Right(Expression.BooleanValue(value))
        case Term.LetExpression(decls, body) => ???
        case Term.IntegerLiteral(value) => Right(Expression.IntegerValue(value))
        case Term.NameTerm(value) => Right(Expression.NameExpression(value))
        case Term.StringLiteral(value) => Right(Expression.StringValue(value))
        case Term.FunctionCall(name, arguments) => ???
        case Term.WanderFunction(parameters, body) => ???
        case Term.IfExpression(conditional, ifBody, elseBody) => ???
    }
}
