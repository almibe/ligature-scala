/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.LigatureError
import dev.ligature.LigatureLiteral
import cats.effect.IO

def eval(script: Seq[Term], bindings: Bindings): IO[ScriptResult] = {
  var result: WanderValue = WanderValue.Nothing
  var error: LigatureError | Unit = ()
  var currentBindings: Bindings = bindings
  if script.isEmpty then
    IO.pure(result)
  else
    evalTerm(script.last, bindings)
      .map(_.result)
  // var itr = script.iterator
  // while itr.hasNext do
  //   val term = itr.next()
  //   evalTerm(term,currentBindings).map { res =>
  //     res
  //     // res match
  //     //   case Left(value) => Left(value)
  //     //   case Right(value) =>
  //     //     result = value.result
  //     //     currentBindings = value.bindings
  //     //     Right(value)
  //   }
  //IO.pure(result)
}

def evalAll(terms: Seq[Term], bindings: Bindings): IO[Seq[WanderValue]] =
  import cats.implicits._
  terms.map { term => evalTerm(term, bindings) }.sequence.map { evalResult => evalResult.map { _.result } }

def evalTerm(term: Term, bindings: Bindings): IO[EvalResult] =
  term match
    case Term.BooleanLiteral(value) => IO.pure(EvalResult(WanderValue.BooleanValue(value), bindings))
    case Term.IdentifierLiteral(value) => IO.pure(EvalResult(WanderValue.LigatureValue(value), bindings))
    case Term.IntegerLiteral(value) => IO.pure(EvalResult(WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(value)), bindings))
    case Term.StringLiteral(value) => IO.pure(EvalResult(WanderValue.LigatureValue(LigatureLiteral.StringLiteral(value)), bindings))
    case Term.Name(value) => ???
    case Term.List(terms) =>
      val values = evalAll(terms, bindings)
      values.map { values =>
        EvalResult(WanderValue.ListValue(values), bindings)
      }
    case Term.FunctionCall(name, arguments) =>
      //TODO val evaldArgs = evalArguments(arguments)
      bindings.read(WanderValue.Name(name.value)) match {
        case Left(value) => ???///IO(Left(value))
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
