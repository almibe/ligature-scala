/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.*
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

enum Expression:
  case Int(value: Long)
  case Bytes(value: Seq[Byte])
  case StringValue(value: String)
  case Word(value: String)
  case Array(value: Seq[Expression])
  case Triple(entity: Expression, attribute: Expression, value: Expression)
  case Network(expressions: Seq[Triple])
  case Application(expressions: Seq[Expression])
  case Grouping(expressions: Seq[Expression])
  case Slot(name: String)

def eval(
    expression: Expression,
): Either[WanderError, LigatureValue] =
  expression match {
    case Expression.Int(value) => Right((LigatureValue.Int(value)))
    case Expression.Bytes(value)        => Right((LigatureValue.Bytes(value)))
    case Expression.StringValue(value) => Right((LigatureValue.StringValue(value)))
    case Expression.Word(value) =>
      Right((LigatureValue.Word(value)))
    case Expression.Array(value)                   => handleArray(value)
    case Expression.Grouping(expressions)    => handleGrouping(expressions)
    case Expression.Application(expressions) => handleApplication(expressions)
    case Expression.Slot(name)               => Right((LigatureValue.Slot(name)))
    case Expression.Network(expressions)       => handleNetwork(expressions)
    case Expression.Triple(_, _, _) => ???
  }

// def readFieldPath(
//     fieldPath: FieldPath,
//     environment: Environment
// ): Either[WanderError, (LigatureValue, Environment)] =
//   environment.read(fieldPath) match
//     case Left(err)          => Left(err)
//     case Right(Some(value)) => Right((value))
//     case Right(None)        => Left(WanderError(s"Could not read $fieldPath."))

def handleGrouping(
    expressions: Seq[Expression],
): Either[WanderError, (LigatureValue)] = ???//{
//   var error: Option[WanderError] = None
//   var res: (LigatureValue, Environment) = (LigatureValue.Module(Map()))
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
// ): Either[WanderError, (LigatureValue, Environment)] =
//   boundary:
//     val results = collection.mutable.HashMap[Field, LigatureValue]()
//     values.foreach((name, value) =>
//       eval(value) match {
//         case Left(err)         => break(Left(err))
//         case Right((value, _)) => results += name -> value
//       }
//     )
//     Right((LigatureValue.Module(results.toMap)))

def handleTriple(triple: Expression.Triple): Triple =
  triple match
    case Expression.Triple(
          Expression.Word(entity),
          Expression.Word(attribute),
          value
        ) =>
          val ligatureValue = value match {
            case Expression.Word(word) => LigatureValue.Word(word)
            case Expression.Int(int) => LigatureValue.Int(int)
            case Expression.Bytes(_) => ???
            case Expression.StringValue(value) => LigatureValue.StringValue(value)
            case Expression.Array(_) => ???
            case Expression.Triple(_, _, _) => ???
            case Expression.Network(_) => ???
            case Expression.Application(_) => ???
            case Expression.Grouping(_) => ???
            case Expression.Slot(_) => ???
          }
          Triple(
            LigatureValue.Word(entity),
            LigatureValue.Word(attribute),
            ligatureValue
          )
    case _ => ???

def handleNetwork(
    triples: Seq[Expression.Triple],
): Either[WanderError, LigatureValue.Network] =
  Right(LigatureValue.Network(InMemoryNetwork(triples.map(handleTriple).toSet)))

def handleApplication(
    expression: Seq[Expression],
): Either[WanderError, (LigatureValue)] =
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
              //     LigatureValue.Triple(
              //       Triple(
              //         LigatureValue.Word(entity),
              //         LigatureValue.Word(attribute),
              //         LigatureValue.Word(value)
              //       )
              //     ),
              //     environment
              //   )
              // )
            case Expression.Int(value) => ???
              // Right(
              //   (
              //     LigatureValue.Triple(
              //       Triple(
              //         LigatureValue.Word(entity),
              //         LigatureValue.Word(attribute),
              //         LigatureValue.Int(value)
              //       )
              //     ),
              //     environment
              //   )
              // )
            case Expression.Bytes(value) => ???
              // Right(
              //   (
              //     LigatureValue.Triple(
              //       Triple(
              //         LigatureValue.Word(entity),
              //         LigatureValue.Word(attribute),
              //         LigatureValue.Bytes(value)
              //       )
              //     ),
              //     environment
              //   )
              // )
            case stringValue: Expression.StringValue => ???
              // eval(stringValue) match {
              //   case Right((LigatureValue.String(result), _)) =>
              //     Right(
              //       (
              //         LigatureValue.Triple(
              //           Triple(
              //             LigatureValue.Word(entity),
              //             LigatureValue.Word(attribute),
              //             LigatureValue.String(result)
              //           )
              //         ),
              //         environment
              //       )
              //     )
              //   case _ => ???
              // }
            case _ => Left(WanderError(s"Invalid Triple - ${expression}"))
        case _ => Left(WanderError(s"Invalid Triple - ${expression}"))
    case x => Left(WanderError(s"Unexpected start of application - $x"))
  }

def callHostFunction(
    hostFunction: HostFunction,
    arguments: Seq[Expression],
): Either[WanderError, (LigatureValue)] = ???
  // if arguments.size == hostFunction.parameters.size then
  //   callHostFunctionComplete(hostFunction, arguments)
  // else if arguments.size < hostFunction.parameters.size then
  //   callHostFunctionPartially(hostFunction, arguments)
  // else ???


def handleArray(
    expressions: Seq[Expression],
): Either[WanderError, (LigatureValue.Quote)] = {
  val res = ListBuffer[LigatureValue]()
  val itre = expressions.iterator
  val continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression) match
      case Left(err)    => return Left(err)
      case Right(value) => res += value
  Right((LigatureValue.Quote(res.toList)))
}
