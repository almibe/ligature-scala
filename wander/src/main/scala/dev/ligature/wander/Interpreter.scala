/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.*
import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break

enum Expression:
  case NameExpression(value: Name)
  case IdentifierValue(value: Identifier)
  case IntegerValue(value: Long)
  case StringValue(value: String)
  case BooleanValue(value: Boolean)
  case Nothing
  case Array(value: Seq[Expression])
  case Binding(name: TaggedName, value: Expression)
  case Record(values: Seq[(Name, Expression)])
  case Lambda(parameters: Seq[Name], body: Expression)
  case WhenExpression(conditionals: Seq[(Expression, Expression)])
  case Application(expressions: Seq[Expression])
  case Grouping(expressions: Seq[Expression])
  case QuestionMark

def eval(
    expressions: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  var lastResult = WanderValue.Nothing
  boundary:
    expressions.foreach(expression =>
      eval(expression, environment) match {
        case Right(value) => lastResult = value._1
        case Left(err)    => break(Left(err))
      }
    )
  Right((lastResult, environment))

def eval(
    expression: Expression,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  expression match {
    case Expression.Nothing                => Right((WanderValue.Nothing, environment))
    case Expression.BooleanValue(value)    => Right((WanderValue.Bool(value), environment))
    case Expression.IntegerValue(value)    => Right((WanderValue.Int(value), environment))
    case Expression.StringValue(value)     => Right((WanderValue.String(value), environment))
    case Expression.IdentifierValue(value) => Right((WanderValue.Identifier(value), environment))
    case Expression.Array(value)           => handleArray(value, environment)
    case Expression.NameExpression(name)   => environment.read(name).map((_, environment))
    case Expression.Binding(name, value)   => handleBinding(name, value, environment)
    case lambda: Expression.Lambda         => Right((WanderValue.Function(Lambda(lambda)), environment))
    case Expression.WhenExpression(conditionals) =>
      handleWhenExpression(conditionals, environment)
    case Expression.Grouping(expressions)    => handleGrouping(expressions, environment)
    case Expression.Application(expressions) => handleApplication(expressions, environment)
    case Expression.QuestionMark             => Right((WanderValue.QuestionMark, environment))
    case Expression.Record(values)           => handleRecord(values, environment)
  }

def handleGrouping(
    expressions: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] = {
  var error: Option[WanderError] = None
  var res: (WanderValue, Environment) = (WanderValue.Nothing, environment)
  val itr = expressions.iterator
  while error.isEmpty && itr.hasNext do
    eval(itr.next(), res._2) match {
      case Left(err)    => error = Some(err)
      case Right(value) => res = value
    }
  if error.isDefined then Left(error.get)
  else Right(res)
}

def handleRecord(
    values: Seq[(Name, Expression)],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  boundary:
    val results = ListBuffer[(Name, WanderValue)]()
    values.foreach((name, value) =>
      eval(value, environment) match {
        case Left(err)         => break(Left(err))
        case Right((value, _)) => results.append((name, value))
      }
    )
    Right((WanderValue.Record(results.toSeq), environment))

def handleBinding(
    name: TaggedName,
    value: Expression,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  eval(value, environment) match {
    case Left(err) => Left(err)
    case Right(value) =>
      environment.bindVariable(name, value._1) match {
        case Left(err) => Left(err)
        case Right(environment) =>
          Right((value._1, environment))
      }
  }

def handleApplication(
    expression: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  expression.head match {
    case Expression.NameExpression(name) =>
      environment.read(name) match {
        case Left(err) => Left(err)
        case Right(value) =>
          val arguments = expression.tail
          value match {
            case WanderValue.Function(Lambda(Expression.Lambda(parameters, body))) => callLambda(arguments, parameters, body, environment)
            case WanderValue.Function(fn: HostFunction) => callHostFunction(fn, arguments, environment)
            case WanderValue.Function(PartialFunction(args, Lambda(Expression.Lambda(parameters, body)))) => callPartialLambda(args, arguments, parameters, body, environment)
            case WanderValue.Function(PartialFunction(args, fn: HostFunction)) => callPartialHostFunction(args, fn, arguments, environment)
            case _ => Left(WanderError(s"Could not call function ${name.name}."))
          }
      }
    case _ => ???
  }

def callLambda(arguments: Seq[Expression], parameters: Seq[Name], body: Expression, environment: Environment) = {
  if (arguments.size == parameters.size) {
    var fnScope = environment.newScope()
    parameters.zipWithIndex.foreach { (param, index) =>
      val argument = eval(arguments(index), environment) match {
        case Left(value) => ???
        case Right(value) =>
          fnScope.bindVariable(TaggedName(param, Tag.Untagged), value._1) match {
            case Left(err) => ???
            case Right(value) => fnScope = value
          }
      }
    }
    eval(body, fnScope)
  } else if (arguments.size < parameters.size) {
    val args = ListBuffer[WanderValue]()
    arguments.zipWithIndex.foreach { (arg, index) =>
      val argument = eval(arg, environment) match {
        case Left(value) => ???
        case Right(value) =>
          args.append(value._1)
      }
    }
    Right((WanderValue.Function(dev.ligature.wander.PartialFunction(args.toSeq, Lambda(Expression.Lambda(parameters, body))))), environment)
  } else {
    Left(WanderError("Too many arguments passed."))
  }
}

def callPartialLambda(values: Seq[WanderValue], 
    arguments: Seq[Expression], 
    parameters: Seq[Name], 
    body: Expression, 
    environment: Environment) =
  if (values.size + arguments.size == parameters.size) {
    var fnScope = environment.newScope()
    arguments.zipWithIndex.foreach { (arg, index) =>
      val diff = parameters.size - arguments.size
      val param = parameters(index + diff)
      val argument = eval(arg, environment) match {
        case Left(value) => ???
        case Right(value) =>
          fnScope.bindVariable(TaggedName(param, Tag.Untagged), value._1) match {
            case Left(err) => ???
            case Right(value) => fnScope = value
          }
      }
    }
    eval(body, fnScope)
  } else if (arguments.size < parameters.size) {
    val args = ListBuffer[WanderValue]()
    parameters.zipWithIndex.foreach { (param, index) =>
      val argument = eval(arguments(index), environment) match {
        case Left(value) => ???
        case Right(value) =>
          args.append(value._1)
      }
    }
    Right(WanderValue.Function(dev.ligature.wander.PartialFunction(args.toSeq, Lambda(Expression.Lambda(parameters, body)))), environment)
  } else {
    Left(WanderError("Too many arguments passed."))
  }

def callHostFunction(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  if (arguments.size == hostFunction.parameters.size) then
    callHostFunctionComplete(hostFunction, arguments, environment)
  else if arguments.size < hostFunction.parameters.size then
    callHostFunctionPartially(hostFunction, arguments, environment)
  else
    ???

private def callHostFunctionComplete(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  boundary:
    val args = ListBuffer[WanderValue]()
    arguments.zipWithIndex.foreach((arg, i) =>
      val argValue = eval(arg, environment) match
        case Left(err)    => break(Left(err))
        case Right(value) => value._1
      val tag = hostFunction.parameters(i).tag
      environment.checkTag(tag, argValue) match {
        case Left(err) => break(Left(err))
        case Right(value) => args.append(argValue)
      }        
    )
    hostFunction.fn(args.toSeq, environment)

private def callHostFunctionPartially(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  boundary:
    val args = ListBuffer[WanderValue]()
    arguments.zipWithIndex.foreach((arg, i) =>
      val argValue = eval(arg, environment) match
        case Left(err)    => break(Left(err))
        case Right(value) => value._1
      val tag = hostFunction.parameters(i).tag
      environment.checkTag(tag, argValue) match {
        case Left(err) => break(Left(err))
        case Right(value) => args.append(argValue)
      }        
    )
    Right((WanderValue.Function(PartialFunction(args.toSeq, hostFunction)), environment))

def callPartialHostFunction(
    values: Seq[WanderValue],
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  if (values.size + arguments.size == hostFunction.parameters.size) then
    callPartialHostFunctionComplete(values, hostFunction, arguments, environment)
  else if values.size + arguments.size < hostFunction.parameters.size then
    callPartialHostFunctionPartially(values, hostFunction, arguments, environment)
  else
    ???

private def callPartialHostFunctionComplete(
    values: Seq[WanderValue],
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  boundary:
    var args = ListBuffer[WanderValue]()
    args = args.concat(values)
    arguments.zipWithIndex.foreach((arg, _i) =>
      val i = _i + values.size
      val argValue = eval(arg, environment) match
        case Left(err)    => break(Left(err))
        case Right(value) => value._1
      val tag = hostFunction.parameters(i).tag
      environment.checkTag(tag, argValue) match {
        case Left(err) => break(Left(err))
        case Right(value) => args.append(argValue)
      }        
    )
    hostFunction.fn(args.toSeq, environment)

private def callPartialHostFunctionPartially(
    values: Seq[WanderValue],
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  boundary:
    var args = ListBuffer[WanderValue]()
    args = args.concat(values)
    arguments.zipWithIndex.foreach((arg, _i) =>
      val i = _i + values.size
      val argValue = eval(arg, environment) match
        case Left(err)    => break(Left(err))
        case Right(value) => value._1
      val tag = hostFunction.parameters(i).tag
      environment.checkTag(tag, argValue) match {
        case Left(err) => break(Left(err))
        case Right(value) => args.append(argValue)
      }        
    )
    Right((WanderValue.Function(PartialFunction(args.toSeq, hostFunction)), environment))

def handleWhenExpression(
    conditionals: Seq[(Expression, Expression)],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  boundary:
    conditionals.find { (conditional, _) =>
      eval(conditional, environment) match {
        case Right((value, _)) =>
          value match {
            case WanderValue.Bool(value) => value
            case _ => break(Left(WanderError("Conditionals must evaluate to Bool.")))
          }
        case Left(err) => break(Left(err))
      }
    } match {
      case None            => Left(WanderError("No matching cases."))
      case Some((_, body)) => eval(body, environment)
    }

def handleArray(
    expressions: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue.Array, Environment)] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, environment) match
      case Left(err)    => return Left(err)
      case Right(value) => res += value._1
  Right((WanderValue.Array(res.toList), environment))
}
