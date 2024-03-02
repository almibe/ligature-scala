/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend

import dev.ligature.bend.*
import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break
import scala.collection.mutable
import dev.ligature.LigatureValue

enum Expression:
  case FieldExpression(field: dev.ligature.bend.Field)
  case FieldPathExpression(fieldPath: dev.ligature.bend.FieldPath)
  case IntegerValue(value: Long)
  case Bytes(value: Seq[Byte])
  case StringValue(value: String, interpolated: Boolean = false)
  case Label(value: String)
  case BooleanValue(value: Boolean)
  case Array(value: Seq[Expression])
  case Binding(name: Field, tag: Option[FieldPath], value: Expression)
  case Module(values: Seq[(dev.ligature.bend.Field, Expression)])
  case Lambda(parameters: Seq[Field], body: Expression)
  case WhenExpression(conditionals: Seq[(Expression, Expression)])
  case Application(expressions: Seq[Expression])
  case Grouping(expressions: Seq[Expression])
  case QuestionMark

def eval(
    expression: Expression,
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  expression match {
    case Expression.BooleanValue(value) => Right((BendValue.Bool(value), environment))
    case Expression.IntegerValue(value) => Right((BendValue.Int(value), environment))
    case Expression.Bytes(value)        => Right((BendValue.Bytes(value), environment))
    case Expression.StringValue(value, interpolated) =>
      if interpolated then interpolateString(value, environment)
      else Right((BendValue.String(value), environment))
    case Expression.Label(value) =>
      Right((BendValue.Label(LigatureValue.Identifier(value)), environment))
    case Expression.Array(value)                   => handleArray(value, environment)
    case Expression.FieldExpression(field)         => readField(field, environment)
    case Expression.FieldPathExpression(fieldPath) => readFieldPath(fieldPath, environment)
    case Expression.Binding(name, tag, value) =>
      handleBinding(name, tag, value, environment)
    case lambda: Expression.Lambda => Right((BendValue.Function(Lambda(lambda)), environment))
    case Expression.WhenExpression(conditionals) =>
      handleWhenExpression(conditionals, environment)
    case Expression.Grouping(expressions)    => handleGrouping(expressions, environment)
    case Expression.Application(expressions) => handleApplication(expressions, environment)
    case Expression.QuestionMark             => Right((BendValue.QuestionMark, environment))
    case Expression.Module(values)           => handleModule(values, environment)
  }

def readField(
    field: Field,
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  environment.read(field) match
    case Left(err)          => Left(err)
    case Right(Some(value)) => Right((value, environment))
    case Right(None)        => ???

def readFieldPath(
    fieldPath: FieldPath,
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  environment.read(fieldPath) match
    case Left(err)          => Left(err)
    case Right(Some(value)) => Right((value, environment))
    case Right(None)        => Left(BendError(s"Could not read $fieldPath."))

def interpolateString(
    value: String,
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  boundary:
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
                case _: Unit => break(Left(BendError("Should never reach")))
                case contents: String =>
                  run(contents, environment) match
                    case Left(err) =>
                      break(Left(err))
                    case Right((value, _)) =>
                      val _ = sb.append(printBendValue(value, true))
            case _ =>
              break(
                Left(
                  BendError("Syntax error, in an interpolated String `$` must be followed by `(`.")
                )
              )
        case c => val _ = sb.append(c)
    Right((BendValue.String(sb.toString), environment))

def handleGrouping(
    expressions: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] = {
  var error: Option[BendError] = None
  var res: (BendValue, Environment) = (BendValue.Module(Map()), environment)
  val itr = expressions.iterator
  while error.isEmpty && itr.hasNext do
    eval(itr.next(), res._2) match {
      case Left(err)    => error = Some(err)
      case Right(value) => res = value
    }
  if error.isDefined then Left(error.get)
  else Right(res)
}

def handleModule(
    values: Seq[(Field, Expression)],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  boundary:
    val results = collection.mutable.HashMap[Field, BendValue]()
    values.foreach((name, value) =>
      eval(value, environment) match {
        case Left(err)         => break(Left(err))
        case Right((value, _)) => results += name -> value
      }
    )
    Right((BendValue.Module(results.toMap), environment))

def handleBinding(
    field: Field,
    tag: Option[FieldPath],
    value: Expression,
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  eval(value, environment) match {
    case Left(err) => Left(err)
    case Right(value) =>
      environment
        .bindVariable(TaggedField(field, Tag.Untagged), value._1) match { // TODO tag is wrong here
        case Left(err) => Left(err)
        case Right(environment) =>
          Right((value._1, environment))
      }
  }

def handleApplication(
    expression: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  expression.head match {
    case Expression.FieldPathExpression(fieldPath) =>
      environment.read(fieldPath) match {
        case Left(err)   => Left(err)
        case Right(None) => Left(BendError(s"Error: Could not read $fieldPath."))
        case Right(Some(value)) =>
          val arguments = expression.tail
          value match {
            case BendValue.Function(Lambda(Expression.Lambda(parameters, body))) =>
              callLambda(arguments, parameters, body, environment)
            case BendValue.Function(fn: HostFunction) =>
              callHostFunction(fn, arguments, environment)
            case BendValue.Function(
                  PartialFunction(args, Lambda(Expression.Lambda(parameters, body)))
                ) =>
              callPartialLambda(args, arguments, parameters, body, environment)
            case BendValue.Function(PartialFunction(args, fn: HostFunction)) =>
              callPartialHostFunction(args, fn, arguments, environment)
            case BendValue.Array(values)  => callArray(values, arguments, environment)
            case BendValue.Module(values) => callModule(values, arguments, environment)
            case _                        => Left(BendError(s"Could not call function."))
          }
      }
    case x => Left(BendError(s"Unexpected value - $x"))
  }

def callArray(
    values: Seq[BendValue],
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  arguments match
    case Seq(value: Expression) =>
      eval(value, environment) match
        case Left(err) => Left(err)
        case Right((BendValue.Int(index), _)) =>
          if values.size > index then Right((values(index.toInt), environment))
          else
            Left(
              BendError(
                s"Error indexing Array, index $index greater than Array's length of ${values.size}."
              )
            )
        case _ => Left(BendError("Error attempting to index Array."))
    case _ => Left(BendError("Error attempting to index Array."))

def callModule(
    values: Map[Field, BendValue],
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  arguments match
    case Seq(value: Expression) =>
      eval(value, environment) match
        case Left(err) => Left(err)
        case Right((BendValue.String(fieldName), _)) =>
          if values.contains(Field(fieldName)) then Right(values(Field(fieldName)), environment)
          else Left(BendError(s"Could not read $fieldName from Module."))
        case _ =>
          Left(
            BendError(s"When calling a Module only pass a single String argument.\n$arguments")
          )
    case _ =>
      Left(BendError(s"When calling a Module only pass a single String argument.\n$arguments"))

def callLambda(
    arguments: Seq[Expression],
    parameters: Seq[Field],
    body: Expression,
    environment: Environment
) =
  if (arguments.size == parameters.size) {
    var fnScope = environment.newScope()
    parameters.zipWithIndex.foreach { (param, index) =>
      eval(arguments(index), environment) match {
        case Left(value) => ???
        case Right(value) =>
          fnScope.bindVariable(TaggedField(param, Tag.Untagged), value._1) match {
            case Left(err)    => ???
            case Right(value) => fnScope = value
          }
      }
    }
    eval(body, fnScope)
  } else if (arguments.size < parameters.size) {
    val args = ListBuffer[BendValue]()
    arguments.zipWithIndex.foreach { (arg, _) =>
      eval(arg, environment) match {
        case Left(value) => throw RuntimeException(s"Error - $value")
        case Right(value) =>
          args.append(value._1)
      }
    }
    Right(
      BendValue.Function(
        dev.ligature.bend.PartialFunction(args.toSeq, Lambda(Expression.Lambda(parameters, body)))
      ),
      environment
    )
  } else {
    Left(BendError("Too many arguments passed."))
  }

def callPartialLambda(
    values: Seq[BendValue],
    arguments: Seq[Expression],
    parameters: Seq[Field],
    body: Expression,
    environment: Environment
) =
  if (values.size + arguments.size == parameters.size) {
    var fnScope = environment.newScope()
    arguments.zipWithIndex.foreach { (arg, index) =>
      val diff = parameters.size - arguments.size
      val param = parameters(index + diff)
      eval(arg, environment) match {
        case Left(value) => ???
        case Right(value) =>
          fnScope.bindVariable(TaggedField(param, Tag.Untagged), value._1) match {
            case Left(err)    => ???
            case Right(value) => fnScope = value
          }
      }
    }
    eval(body, fnScope)
  } else if (values.size + arguments.size < parameters.size) {
    val args = ListBuffer[BendValue]().concat(values)
    arguments.foreach { argument =>
      eval(argument, environment) match {
        case Left(value) => ???
        case Right(value) =>
          args.append(value._1)
      }
    }
    Right(
      BendValue.Function(
        dev.ligature.bend.PartialFunction(args.toSeq, Lambda(Expression.Lambda(parameters, body)))
      ),
      environment
    )
  } else {
    Left(BendError("Too many arguments passed."))
  }

def callHostFunction(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  if arguments.size == hostFunction.parameters.size then
    callHostFunctionComplete(hostFunction, arguments, environment)
  else if arguments.size < hostFunction.parameters.size then
    callHostFunctionPartially(hostFunction, arguments, environment)
  else ???

private def callHostFunctionComplete(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  boundary:
    val args = ListBuffer[BendValue]()
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
): Either[BendError, (BendValue, Environment)] =
  boundary:
    val args = ListBuffer[BendValue]()
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
    Right((BendValue.Function(PartialFunction(args.toSeq, hostFunction)), environment))

def callPartialHostFunction(
    values: Seq[BendValue],
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  if values.size + arguments.size == hostFunction.parameters.size then
    callPartialHostFunctionComplete(values, hostFunction, arguments, environment)
  else if values.size + arguments.size < hostFunction.parameters.size then
    callPartialHostFunctionPartially(values, hostFunction, arguments, environment)
  else ???

private def callPartialHostFunctionComplete(
    values: Seq[BendValue],
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  boundary:
    var args = ListBuffer[BendValue]()
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
    values: Seq[BendValue],
    hostFunction: HostFunction,
    arguments: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  boundary:
    var args = ListBuffer[BendValue]()
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
    Right((BendValue.Function(PartialFunction(args.toSeq, hostFunction)), environment))

def handleWhenExpression(
    conditionals: Seq[(Expression, Expression)],
    environment: Environment
): Either[BendError, (BendValue, Environment)] =
  boundary:
    conditionals.find { (conditional, _) =>
      eval(conditional, environment) match {
        case Right((value, _)) =>
          value match {
            case BendValue.Bool(value) => value
            case _ => break(Left(BendError("Conditionals must evaluate to Bool.")))
          }
        case Left(err) => break(Left(err))
      }
    } match {
      case None            => Left(BendError("No matching cases."))
      case Some((_, body)) => eval(body, environment)
    }

def handleArray(
    expressions: Seq[Expression],
    environment: Environment
): Either[BendError, (BendValue.Array, Environment)] = {
  val res = ListBuffer[BendValue]()
  val itre = expressions.iterator
  val continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, environment) match
      case Left(err)    => return Left(err)
      case Right(value) => res += value._1
  Right((BendValue.Array(res.toList), environment))
}
