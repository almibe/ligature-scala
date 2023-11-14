/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

enum Expression:
  case NameExpression(value: Name)
  case IdentifierValue(value: Identifier)
  case IntegerValue(value: Long)
  case StringValue(value: String)
  case BooleanValue(value: Boolean)
  case Nothing
  case List(value: Seq[Expression])
  case LetExpression(decls: Seq[(Name, Expression)], body: Expression)
  case FunctionCall(name: Name, arguments: Seq[Expression])
  case WanderFunction(
    parameters: Seq[Name],
    body: Seq[Expression])
  case IfExpression(
    conditional: Expression,
    ifBody: Expression,
    elseBody: Expression)

def eval(expression: Expression, bindings: Bindings): Either[WanderError, WanderValue] = {
  expression match {
    case Expression.Nothing => Right(WanderValue.Nothing)
    case Expression.BooleanValue(value) => Right(WanderValue.BooleanValue(value))
    case Expression.IntegerValue(value) => Right(WanderValue.IntValue(value))
    case Expression.StringValue(value) => Right(WanderValue.StringValue(value))
    case Expression.IdentifierValue(value) => Right(WanderValue.Identifier(value))
    case _ => ???
  }
}
//   term match
//     case Term.BooleanLiteral(value) =>
//       IO.pure(EvalResult(WanderValue.BooleanValue(value), bindings))
//     case Term.IdentifierLiteral(value) =>
//       IO.pure(EvalResult(WanderValue.LigatureValue(value), bindings))
//     case Term.IntegerLiteral(value) =>
//       IO.pure(EvalResult(WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(value)), bindings))
//     case Term.StringLiteral(value) =>
//       IO.pure(EvalResult(WanderValue.LigatureValue(LigatureLiteral.StringLiteral(value)), bindings))
//     case Term.NameTerm(value) =>
//       bindings.read(value) match
//         case Left(value) => IO.raiseError(value)
//         case Right(value) => IO.pure(EvalResult(value, bindings))
//     case Term.LetExpression(name, term) =>
//       evalTerm(term, bindings).map { value =>
//         bindings.bindVariable(name, value.result) match
//           case Left(error) => throw error
//           case Right(newBindings) =>
//             EvalResult(WanderValue.Nothing, newBindings)
//       }
//     case Term.List(terms) =>
//       evalAll(terms, bindings).map { values =>
//         EvalResult(WanderValue.ListValue(values), bindings)
//       }
//     case Term.FunctionCall(name, arguments) =>
//       //TODO val evaldArgs = evalArguments(arguments)
//       bindings.read(name) match {
//         case Left(value) => IO.raiseError(value)
//         case Right(value) =>
//           value match {
//             case WanderValue.NativeFunction(body) => {
//               body(arguments, bindings).map { value => EvalResult(value, bindings) }
//             }
//             case WanderValue.WanderFunction(parameters, body) =>
//               if parameters.length == arguments.length then
//                 var newScope = bindings.newScope()
//                 arguments
//                   .map { term =>
//                     evalTerm(term, bindings)
//                   }.sequence.map { evalResults =>
//                     val args = evalResults.map(_.result)
//                     parameters.zip(args).foreach { (name, value) =>
//                       newScope.bindVariable(name, value) match
//                         case Left(value) => ???
//                         case Right(bindings) => 
//                           newScope = bindings
//                     }
//                   }.flatMap { _ =>
//                     eval(body, newScope).map { scriptResult => EvalResult(scriptResult.result, bindings) }
//                   }
//               else
//                 IO.raiseError(LigatureError("Argument and parameter size must be the same."))
//             case _ => ???
//           }
//       }
//     case Term.WanderFunction(parameters, body) => {
//       IO.pure(EvalResult(WanderValue.WanderFunction(parameters, body), bindings))
//     }
//     case Term.Scope(terms) =>
//       eval(terms, bindings.newScope()).map { x => EvalResult(x.result, bindings) }
//     case Term.IfExpression(ifConditional, ifBody, elseBody) =>
//       for {
//         cond <- evalTerm(ifConditional, bindings)
//         res <- cond match
//           case EvalResult(WanderValue.BooleanValue(true), bindings) => evalTerm(ifBody, bindings.newScope())
//           case EvalResult(WanderValue.BooleanValue(false), bindings) => evalTerm(elseBody, bindings.newScope())
//           case _ => IO.raiseError(LigatureError("If expressions require Boolean values for conditionals."))
//       } yield EvalResult(res.result, bindings)
//     case Term.NothingLiteral =>
//       IO.pure(EvalResult(WanderValue.Nothing, bindings))
//     case Term.QuestionMark =>
//       IO.pure(EvalResult(WanderValue.Nothing, bindings))
