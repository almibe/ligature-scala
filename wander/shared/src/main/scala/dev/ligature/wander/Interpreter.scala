/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.LigatureError
import dev.ligature.LigatureLiteral

/** Represents a full script that can be eval'd.
  */
def eval(script: Seq[Term], bindings: Bindings): Either[LigatureError, ScriptResult] = {
  var result: WanderValue = WanderValue.Nothing
  var currentBindings: Bindings = bindings
  script.foreach { term =>
    evalTerm(term,currentBindings) match {
      case Left(err) => return Left(err)
      case Right(res) =>
        result = res.result
        currentBindings = res.bindings
    }
  }
  Right(result)
}

def evalTerm(term: Term, bindings: Bindings): Either[LigatureError, EvalResult] =
  term match
    case Term.BooleanLiteral(value) => Right(EvalResult(WanderValue.BooleanValue(value), bindings))
    case Term.IdentifierLiteral(value) => Right(EvalResult(WanderValue.LigatureValue(value), bindings))
    case Term.IntegerLiteral(value) => Right(EvalResult(WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(value)), bindings))
    case Term.StringLiteral(value) => Right(EvalResult(WanderValue.LigatureValue(LigatureLiteral.StringLiteral(value)), bindings))
    case Term.Name(value) => ???
    case Term.FunctionCall(name, arguments) =>
      //TODO val evaldArgs = evalArguments(arguments)
      bindings.read(WanderValue.Name(name.value)) match {
        case Left(value) => Left(value)
        case Right(value) =>
          value match {
            case WanderValue.NativeFunction(parameters, body, output) => {
              body(arguments, bindings).map { value => EvalResult(value, bindings) }
            }
            case WanderValue.WanderFunction(parameters, body, output) => ???
            case _ => ???
          }
      }
    case Term.WanderFunction(parameters, body) => {
      ???
    }
    case Term.Scope(terms) => ???
