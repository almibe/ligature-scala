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

def eval(expression: Expression, bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
  expression match {
    case Expression.Nothing => Right((WanderValue.Nothing, bindings))
    case Expression.BooleanValue(value) => Right((WanderValue.BooleanValue(value), bindings))
    case Expression.IntegerValue(value) => Right((WanderValue.IntValue(value), bindings))
    case Expression.StringValue(value) => Right((WanderValue.StringValue(value), bindings))
    case Expression.IdentifierValue(value) => Right((WanderValue.Identifier(value), bindings))
    case Expression.Array(value) => handleArray(value, bindings)
    case Expression.Set(value) => handleSet(value, bindings)
    case Expression.Record(entries) => handleRecord(entries, bindings)
    case Expression.Application(name, arguments) => handleApplication(name, arguments, bindings)
    case Expression.NameExpression(name) => bindings.read(name).map((_, bindings))
    case Expression.LetExpression(name, value) => handleLetExpression(name, value, bindings)
    case lambda: Expression.Lambda => Right((WanderValue.Lambda(lambda), bindings))
    case Expression.WhenExpression(conditionals) => handleWhenExpression(conditionals, bindings)
    case Expression.Grouping(expressions) => handleGrouping(expressions, bindings)
  }
}

def handleGrouping(expressions: Seq[Expression], bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
  var error: Option[WanderError] = None
  var res: (WanderValue, Bindings) = (WanderValue.Nothing, bindings)
  val itr = expressions.iterator
  while error.isEmpty && itr.hasNext do
    eval(itr.next(), res._2) match {
      case Left(err) => error = Some(err)
      case Right(value) => res = value
    }
  if error.isDefined then
    Left(error.get)
  else
    Right(res)
}

def handleLetExpression(name: Name, value: Expression, bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
  var newScope = bindings.newScope()
  eval(value, newScope) match {
    case Left(value) => ???
    case Right(value) => {
      newScope = newScope.bindVariable(name, value._1)
      Right((value._1, newScope))
    }
  }
}

def handleApplication(name: Name, arguments: Seq[Expression], bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
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
                fnScope = fnScope.bindVariable(param, value._1)
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

def handleWhenExpression(conditionals: Seq[(Expression, Expression)], bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
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

def handleRecord(entries: Seq[(Name, Expression)], bindings: Bindings): Either[WanderError, (WanderValue.Record, Bindings)] = {
  boundary:
    val record = entries.map((name, expression) => {
      eval(expression, bindings) match {
        case Left(value) => break(Left(value))
        case Right(value) => (name, value._1)
      }
    })
    Right((WanderValue.Record(record), bindings))
}

def handleArray(expressions: Seq[Expression], bindings: Bindings): Either[WanderError, (WanderValue.Array, Bindings)] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, bindings) match
      case Left(err) => return Left(err)
      case Right(value) => res += value._1
  Right((WanderValue.Array(res.toList), bindings))
}

def handleSet(expressions: Seq[Expression], bindings: Bindings): Either[WanderError, (WanderValue.Set, Bindings)] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, bindings) match
      case Left(err) => return Left(err)
      case Right(value) => res += value._1 
  Right((WanderValue.Set(res.toSet), bindings))
}
