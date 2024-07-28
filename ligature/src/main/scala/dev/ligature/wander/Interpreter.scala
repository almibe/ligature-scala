/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.*
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

enum Expression:
  case IntegerValue(value: Long)
  case Bytes(value: Seq[Byte])
  case StringValue(value: String)
  case Word(value: String)
  case Array(value: Seq[Expression])
  case Network(expressions: Seq[Expression])
  case Application(expressions: Seq[Expression])
  case Grouping(expressions: Seq[Expression])
  case Slot(name: String)

def eval(
    expression: Expression,
): Either[WanderError, WanderValue] =
  expression match {
    case Expression.IntegerValue(value) => Right((WanderValue.Int(value)))
    case Expression.Bytes(value)        => Right((WanderValue.Bytes(value)))
    case Expression.StringValue(value) => Right((WanderValue.String(value)))
    case Expression.Word(value) =>
      Right((WanderValue.Word(LigatureValue.Word(value))))
    case Expression.Array(value)                   => handleArray(value)
    case Expression.Grouping(expressions)    => handleGrouping(expressions)
    case Expression.Application(expressions) => handleApplication(expressions)
    case Expression.Slot(name)               => Right((WanderValue.Slot(name)))
    case Expression.Network(expressions)       => handleNetwork(expressions)
  }

// def readFieldPath(
//     fieldPath: FieldPath,
//     environment: Environment
// ): Either[WanderError, (WanderValue, Environment)] =
//   environment.read(fieldPath) match
//     case Left(err)          => Left(err)
//     case Right(Some(value)) => Right((value))
//     case Right(None)        => Left(WanderError(s"Could not read $fieldPath."))

def handleGrouping(
    expressions: Seq[Expression],
): Either[WanderError, (WanderValue)] = ???//{
//   var error: Option[WanderError] = None
//   var res: (WanderValue, Environment) = (WanderValue.Module(Map()))
//   val itr = expressions.iterator
//   while error.isEmpty && itr.hasNext do
//     eval(itr.next(), res._2) match {
//       case Left(err)    => error = Some(err)
//       case Right(value) => res = value
//     }
//   if error.isDefined then Left(error.get)
//   else Right(res)
// }

// def handleModule(
//     values: Seq[(Field, Expression)],
//     environment: Environment
// ): Either[WanderError, (WanderValue, Environment)] =
//   boundary:
//     val results = collection.mutable.HashMap[Field, WanderValue]()
//     values.foreach((name, value) =>
//       eval(value) match {
//         case Left(err)         => break(Left(err))
//         case Right((value, _)) => results += name -> value
//       }
//     )
//     Right((WanderValue.Module(results.toMap)))

def handleNetwork(
    expressions: Seq[Expression],
): Either[WanderError, (WanderValue)] =
  expressions match
    case Seq(
          Expression.Word(entity),
          Expression.Word(attribute),
          Expression.Word(value)
        ) =>
      Right(
        (
          WanderValue.Network(
            Set(
              Statement(
                LigatureValue.Word(entity),
                LigatureValue.Word(attribute),
                LigatureValue.Word(value)
              )
            )
          )
        )
      )
    case _ => ???

def handleApplication(
    expression: Seq[Expression],
): Either[WanderError, (WanderValue)] =
  expression.head match {
    case Expression.Word(word) =>
      expression match
        case Seq(
              Expression.Word(entity),
              Expression.Word(attribute),
              value: Expression
            ) =>
          value match
            case Expression.Word(value) => ???
              // Right(
              //   (
              //     WanderValue.Statement(
              //       Statement(
              //         LigatureValue.Word(entity),
              //         LigatureValue.Word(attribute),
              //         LigatureValue.Word(value)
              //       )
              //     ),
              //     environment
              //   )
              // )
            case Expression.IntegerValue(value) => ???
              // Right(
              //   (
              //     WanderValue.Statement(
              //       Statement(
              //         LigatureValue.Word(entity),
              //         LigatureValue.Word(attribute),
              //         LigatureValue.IntegerValue(value)
              //       )
              //     ),
              //     environment
              //   )
              // )
            case Expression.Bytes(value) => ???
              // Right(
              //   (
              //     WanderValue.Statement(
              //       Statement(
              //         LigatureValue.Word(entity),
              //         LigatureValue.Word(attribute),
              //         LigatureValue.BytesValue(value)
              //       )
              //     ),
              //     environment
              //   )
              // )
            case stringValue: Expression.StringValue => ???
              // eval(stringValue) match {
              //   case Right((WanderValue.String(result), _)) =>
              //     Right(
              //       (
              //         WanderValue.Statement(
              //           Statement(
              //             LigatureValue.Word(entity),
              //             LigatureValue.Word(attribute),
              //             LigatureValue.StringValue(result)
              //           )
              //         ),
              //         environment
              //       )
              //     )
              //   case _ => ???
              // }
            case _ => Left(WanderError(s"Invalid Statement - ${expression}"))
        case _ => Left(WanderError(s"Invalid Statement - ${expression}"))
    case x => Left(WanderError(s"Unexpected start of application - $x"))
  }

def wanderToLigatureValue(value: WanderValue): LigatureValue =
  value match
    case WanderValue.Bytes(value)      => LigatureValue.BytesValue(value)
    case WanderValue.Int(value)        => LigatureValue.IntegerValue(value)
    case WanderValue.String(value)     => LigatureValue.StringValue(value)
    case WanderValue.Word(value) => value
    case _                             => ???

def callHostFunction(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
): Either[WanderError, (WanderValue)] = ???
  // if arguments.size == hostFunction.parameters.size then
  //   callHostFunctionComplete(hostFunction, arguments)
  // else if arguments.size < hostFunction.parameters.size then
  //   callHostFunctionPartially(hostFunction, arguments)
  // else ???


def handleArray(
    expressions: Seq[Expression],
): Either[WanderError, (WanderValue.Array)] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  val continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression) match
      case Left(err)    => return Left(err)
      case Right(value) => res += value
  Right((WanderValue.Array(res.toList)))
}
