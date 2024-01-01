/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.*
import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break
import scala.collection.mutable

enum Expression:
  case Import(name: Name)
  case NameExpression(value: Name)
  case IntegerValue(value: Long)
  case StringValue(value: String, interpolated: Boolean = false)
  case BooleanValue(value: Boolean)
  case Nothing
  case Array(value: Seq[Expression])
  case Binding(name: TaggedName, value: Expression, exportName: Boolean = false)
  case Record(values: Seq[(Name, Expression)])
  case Lambda(parameters: Seq[Name], body: Expression)
  case WhenExpression(conditionals: Seq[(Expression, Expression)])
  case Application(expressions: Seq[Expression])
  case Grouping(expressions: Seq[Expression])
  case QuestionMark

/** Runs a sequences of Expressions and returns a Record that holds all of the
  * exported names.
  */
def load(
    script: String,
    environment: Environment
): Either[WanderError, Map[Name, WanderValue]] =
  val result = collection.mutable.HashMap[Name, WanderValue]()
  var currentEnvironemnt = environment
  val expressions: Seq[Expression] = introspect(script).expression.getOrElse(???)
  boundary:
    expressions.foreach(expression =>
      eval(expression, currentEnvironemnt) match {
        case Left(err) => break(Left(err))
        case Right((value, environment)) =>
          currentEnvironemnt = environment
          expression match
            case Expression.Binding(TaggedName(name, tag), expression, exportName) =>
              eval(expression, currentEnvironemnt) match
                case Left(err) => Left(err)
                case Right((value, _)) =>
                  result += (name -> value)
            case _ => ()
      }
    )
  Right(result.toMap)

def eval(
    expression: Expression,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  expression match {
    case Expression.Import(name)        => Right((WanderValue.Nothing, environment))
    case Expression.Nothing             => Right((WanderValue.Nothing, environment))
    case Expression.BooleanValue(value) => Right((WanderValue.Bool(value), environment))
    case Expression.IntegerValue(value) => Right((WanderValue.Int(value), environment))
    case Expression.StringValue(value, interpolated) =>
      if interpolated then interpolateString(value, environment)
      else Right((WanderValue.String(value), environment))
    case Expression.Array(value)                     => handleArray(value, environment)
    case Expression.NameExpression(name)             => readName(name, environment)
    case Expression.Binding(name, value, exportName) => handleBinding(name, value, environment)
    case lambda: Expression.Lambda => Right((WanderValue.Function(Lambda(lambda)), environment))
    case Expression.WhenExpression(conditionals) =>
      handleWhenExpression(conditionals, environment)
    case Expression.Grouping(expressions)    => handleGrouping(expressions, environment)
    case Expression.Application(expressions) => handleApplication(expressions, environment)
    case Expression.QuestionMark             => Right((WanderValue.QuestionMark, environment))
    case Expression.Record(values)           => handleRecord(values, environment)
  }

def readName(
    name: Name,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  val parts = name.name.split('.')
  if parts.length == 0 then environment.read(name).map((_, environment))
  else
    environment.read(Name(parts(0))) match
      case Left(err) => Left(WanderError(s"Could not read ${parts(0)}"))
      case Right(value) =>
        var lastValue = value
        parts.tail.foreach { part =>
          lastValue match
            case WanderValue.Record(values) =>
              values.get(Name(part)) match
                case None        => ???
                case Some(value) => lastValue = value
            case _ => ???
        }
        Right((lastValue, environment))

def interpolateString(
    value: String,
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  val sb = StringBuffer()
  val it = value.iterator
  while it.hasNext do
    it.next() match
      case '$' =>
        it.next() match
          case '(' =>
            val contents = boundary:
              val contents = mutable.StringBuilder()
              while it.hasNext do
                it.next() match
                  case ')' =>
                    break(contents.toString())
                  case c => contents.append(c)
            contents match
              case _: Unit => Left(WanderError("Should never reach"))
              case contents: String =>
                run(contents, environment) match
                  case Left(err) =>
                    Left(err)
                  case Right((value, _)) =>
                    sb.append(printWanderValue(value, true))
          case _ =>
            Left(
              WanderError("Syntax error, in an interpolated String `$` must be followed by `(`.")
            )
      case c => sb.append(c)
  Right((WanderValue.String(sb.toString), environment))

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
    val results = collection.mutable.HashMap[Name, WanderValue]()
    values.foreach((name, value) =>
      eval(value, environment) match {
        case Left(err)         => break(Left(err))
        case Right((value, _)) => results += name -> value
      }
    )
    Right((WanderValue.Record(results.toMap), environment))

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
            case WanderValue.Function(Lambda(Expression.Lambda(parameters, body))) =>
              callLambda(arguments, parameters, body, environment)
            case WanderValue.Function(fn: HostFunction) =>
              callHostFunction(fn, arguments, environment)
            case WanderValue.Function(
                  PartialFunction(args, Lambda(Expression.Lambda(parameters, body)))
                ) =>
              callPartialLambda(args, arguments, parameters, body, environment)
            case WanderValue.Function(PartialFunction(args, fn: HostFunction)) =>
              callPartialHostFunction(args, fn, arguments, environment)
            case WanderValue.Array(values)  => callArray(values, arguments, environment)
            case WanderValue.Record(values) => callRecord(values, arguments, environment)
            case _ => Left(WanderError(s"Could not call function ${name.name}."))
          }
      }
    case _ => ???
  }

def callArray(
    values: Seq[WanderValue],
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  arguments match
    case Seq(value: Expression) =>
      eval(value, environment) match
        case Left(err) => Left(err)
        case Right((WanderValue.Int(index), _)) =>
          if values.size > index then Right((values(index.toInt), environment))
          else
            Left(
              WanderError(
                s"Error indexing Array, index $index greater than Array's length of ${values.size}."
              )
            )
        case _ => Left(WanderError("Error attempting to index Array."))
    case _ => Left(WanderError("Error attempting to index Array."))

def callRecord(
    values: Map[Name, WanderValue],
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  arguments match
    case Seq(value: Expression) =>
      eval(value, environment) match
        case Left(err) => Left(err)
        case Right((WanderValue.String(name), _)) =>
          if values.contains(Name(name)) then Right(values(Name(name)), environment)
          else Left(WanderError(s"Could not read $name from Record."))
        case _ => Left(WanderError("Error attempting to read Record."))
    case _ => Left(WanderError("Error attempting to read Record."))

def callLambda(
    arguments: Seq[Expression],
    parameters: Seq[Name],
    body: Expression,
    environment: Environment
) =
  if (arguments.size == parameters.size) {
    var fnScope = environment.newScope()
    parameters.zipWithIndex.foreach { (param, index) =>
      val argument = eval(arguments(index), environment) match {
        case Left(value) => ???
        case Right(value) =>
          fnScope.bindVariable(TaggedName(param, Tag.Untagged), value._1) match {
            case Left(err)    => ???
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
    Right(
      WanderValue.Function(
        dev.ligature.wander.PartialFunction(args.toSeq, Lambda(Expression.Lambda(parameters, body)))
      ),
      environment
    )
  } else {
    Left(WanderError("Too many arguments passed."))
  }

def callPartialLambda(
    values: Seq[WanderValue],
    arguments: Seq[Expression],
    parameters: Seq[Name],
    body: Expression,
    environment: Environment
) =
  if (values.size + arguments.size == parameters.size) {
    var fnScope = environment.newScope()
    arguments.zipWithIndex.foreach { (arg, index) =>
      val diff = parameters.size - arguments.size
      val param = parameters(index + diff)
      val argument = eval(arg, environment) match {
        case Left(value) => ???
        case Right(value) =>
          fnScope.bindVariable(TaggedName(param, Tag.Untagged), value._1) match {
            case Left(err)    => ???
            case Right(value) => fnScope = value
          }
      }
    }
    eval(body, fnScope)
  } else if (values.size + arguments.size < parameters.size) {
    val args = ListBuffer[WanderValue]().concat(values)
    arguments.foreach { argument =>
      eval(argument, environment) match {
        case Left(value) => ???
        case Right(value) =>
          args.append(value._1)
      }
    }
    Right(
      WanderValue.Function(
        dev.ligature.wander.PartialFunction(args.toSeq, Lambda(Expression.Lambda(parameters, body)))
      ),
      environment
    )
  } else {
    Left(WanderError("Too many arguments passed."))
  }

def callHostFunction(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[WanderError, (WanderValue, Environment)] =
  if arguments.size == hostFunction.parameters.size then
    callHostFunctionComplete(hostFunction, arguments, environment)
  else if arguments.size < hostFunction.parameters.size then
    callHostFunctionPartially(hostFunction, arguments, environment)
  else ???

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
        case Left(err)    => break(Left(err))
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
        case Left(err)    => break(Left(err))
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
  if values.size + arguments.size == hostFunction.parameters.size then
    callPartialHostFunctionComplete(values, hostFunction, arguments, environment)
  else if values.size + arguments.size < hostFunction.parameters.size then
    callPartialHostFunctionPartially(values, hostFunction, arguments, environment)
  else ???

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
        case Left(err)    => break(Left(err))
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
    arguments.zipWithIndex.foreach((arg, index) =>
      val paramIndex = index + values.size
      val argValue = eval(arg, environment) match
        case Left(err)    => break(Left(err))
        case Right(value) => value._1
      val tag = hostFunction.parameters(paramIndex).tag
      environment.checkTag(tag, argValue) match {
        case Left(err)    => break(Left(err))
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
