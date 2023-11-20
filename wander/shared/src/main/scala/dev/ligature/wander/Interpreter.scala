/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.mutable.ListBuffer
import scala.util.boundary
import scala.util.boundary, boundary.break

enum Expression:
  case NameExpression(value: Name)
  case IdentifierValue(value: Identifier)
  case IntegerValue(value: Long)
  case StringValue(value: String)
  case BooleanValue(value: Boolean)
  case Nothing
  case Array(value: Seq[Expression])
  case Set(value: Seq[Expression])
  case Record(entires: Seq[(Name, Expression)])
  case LetExpression(name: Name, value: Expression)
  case Application(name: Name, arguments: Seq[Expression])
  case Lambda(
    parameters: Seq[Name],
    body: Expression)
  case WhenExpression(conditionals: Seq[(Expression, Expression)])
  case Grouping(expressions: Seq[Expression])

def eval(expression: Expression, bindings: Bindings): Either[WanderError, WanderValue] = {
  expression match {
    case Expression.Nothing => Right(WanderValue.Nothing)
    case Expression.BooleanValue(value) => Right(WanderValue.BooleanValue(value))
    case Expression.IntegerValue(value) => Right(WanderValue.IntValue(value))
    case Expression.StringValue(value) => Right(WanderValue.StringValue(value))
    case Expression.IdentifierValue(value) => Right(WanderValue.Identifier(value))
    case Expression.Array(value) => handleArray(value, bindings)
    case Expression.Set(value) => handleSet(value, bindings)
    case Expression.Record(entries) => handleRecord(entries, bindings)
    case Expression.Application(name, arguments) => handleApplication(name, arguments, bindings)
    case Expression.NameExpression(name) => bindings.read(name)
    case Expression.LetExpression(name, value) => handleLetExpression(name, value, bindings)
    case lambda: Expression.Lambda => Right(WanderValue.Lambda(lambda))
    case Expression.WhenExpression(conditionals) => handleWhenExpression(conditionals, bindings)
    case Expression.Grouping(expressions) => handleGrouping(expressions, bindings)
  }
}

def handleGrouping(expressions: Seq[Expression], bindings: Bindings): Either[WanderError, WanderValue] = {
  var error: Option[WanderError] = None
  var res: Option[WanderValue] = None
  val itr = expressions.iterator
  while error.isEmpty && itr.hasNext do
    eval(itr.next(), bindings) match {
      case Left(err) => error = Some(err)
      case Right(value) => res = Some(value)
    }
  (error, res) match {
    case (Some(err), _) => Left(err)
    case (_, Some(res)) => Right(res)
    case (_, None) => Right(WanderValue.Nothing)
  }
}

def handleLetExpression(name: Name, value: Expression, bindings: Bindings): Either[WanderError, WanderValue] = {
  var newScope = bindings.newScope()
  eval(value, newScope) match {
    case Left(value) => ???
    case Right(value) => {
      newScope = newScope.bindVariable(name, value)
      Right(value)
    }
  }
}

def handleApplication(name: Name, arguments: Seq[Expression], bindings: Bindings): Either[WanderError, WanderValue] = {
  bindings.read(name) match {
    case Left(err) => Left(err)
    case Right(value) => {
      value match {
        case WanderValue.Lambda(Expression.Lambda(parameters, body)) => {
          var fnScope = bindings.newScope()
          assert(arguments.size == parameters.size)
          parameters.zipWithIndex.foreach { (param, index) =>
            val argument = eval(arguments(index), bindings) match {
              case Left(value) => ???
              case Right(value) => {
                fnScope = fnScope.bindVariable(param, value)
              }
            }
          }
          eval(body, fnScope)
        }
        case WanderValue.HostFunction(fn) => fn(arguments, bindings)
        case _ => Left(WanderError(s"Could not call function ${name.name}."))
      }
    }
  }
}

def handleWhenExpression(conditionals: Seq[(Expression, Expression)], bindings: Bindings): Either[WanderError, WanderValue] = {
  ???
  // eval(conditional, bindings) match {
  //   case Left(value) => ???
  //   case Right(value) => {
  //     value match
  //       case WanderValue.BooleanValue(true) => eval(ifBody, bindings)
  //       case WanderValue.BooleanValue(false) => eval(elseBody, bindings)
  //       case _ => ???
  //   }
  // }
}

def handleRecord(entries: Seq[(Name, Expression)], bindings: Bindings): Either[WanderError, WanderValue.Record] = {
  boundary:
    val record = entries.map((name, expression) => {
      eval(expression, bindings) match {
        case Left(value) => break(Left(value))
        case Right(value) => (name, value)
      }
    })
    Right(WanderValue.Record(record))
}

def handleArray(expressions: Seq[Expression], bindings: Bindings): Either[WanderError, WanderValue.Array] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, bindings) match
      case Left(err) => return Left(err)
      case Right(value) => res += value    
  Right(WanderValue.Array(res.toList))
}

def handleSet(expressions: Seq[Expression], bindings: Bindings): Either[WanderError, WanderValue.Set] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, bindings) match
      case Left(err) => return Left(err)
      case Right(value) => res += value    
  Right(WanderValue.Set(res.toSet))
}
