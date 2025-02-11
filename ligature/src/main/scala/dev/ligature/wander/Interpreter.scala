/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.*
//import scala.collection.mutable.ListBuffer
import cats.effect.IO

def eval(
    terms: Seq[LigatureValue],
    stack: List[LigatureValue]
): IO[List[LigatureValue]] =
    terms.foldLeft[IO[List[LigatureValue]]](IO.pure(stack))((state, value) =>
      state.flatMap(state => eval(value, state)))

def eval(
    term: LigatureValue,
    stack: List[LigatureValue]
): IO[List[LigatureValue]] =
  term match {
    case term: LigatureValue.Element => ???
    case term: LigatureValue.Literal => IO.pure(term :: stack)
    case term: LigatureValue.NetworkRef => IO.pure(term :: stack)
    case term: LigatureValue.Quote => IO.pure(term :: stack)
    case _ => ???
  }

// def readFieldPath(
//     fieldPath: FieldPath,
//     environment: Environment
// ): Either[WanderError, (LigatureValue, Environment)] =
//   environment.read(fieldPath) match
//     case Left(err)          => Left(err)
//     case Right(Some(value)) => Right((value))
//     case Right(None)        => Left(WanderError(s"Could not read $fieldPath."))

// def handleGrouping(
//     expressions: Seq[Expression],
// ): Either[WanderError, (LigatureValue)] = ???//{
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

// def handleTriple(triple: Expression.Triple, runtimeNetwork: INetwork): Triple =
//   triple match
//     case Expression.Triple(
//           Expression.Word(entity),
//           Expression.Word(attribute),
//           value
//         ) =>
//           val ligatureValue = value match {
//             case Expression.Word(word) => LigatureValue.Word(word)
//             case Expression.Int(int) => LigatureValue.Int(int)
//             case Expression.Bytes(_) => ???
//             case Expression.StringValue(value) => LigatureValue.StringValue(value)
//             case Expression.Quote(quote) => handleQuote(quote, runtimeNetwork)
//             case Expression.Triple(_, _, _) => ???
//             case Expression.Network(_) => ???
//             case Expression.Application(_) => ???
//             case Expression.Grouping(_) => ???
//             case Expression.Slot(_) => ???
//           }
//           Triple(
//             LigatureValue.Word(entity),
//             LigatureValue.Word(attribute),
//             ligatureValue
//           )
//     case _ => ???

// def handleNetwork(
//     triples: Seq[LigatureValue.Triple],
//     runtimeNetwork: INetwork
// ): Either[WanderError, LigatureValue.Network] =
//   val network = triples.map(triple => handleTriple(triple, runtimeNetwork)).toSet
//   Right(LigatureValue.Network(runtimeNetwork.union(InMemoryNetwork(network))))

def handleApplication(
    expression: Seq[LigatureValue]
): Either[WanderError, LigatureValue] = ???
// expression.head match {
//   case Expression.Word(word) =>
//     expression match
//       case Seq(
//             Expression.Word(entity),
//             Expression.Word(attribute),
//             value: Expression
//           ) =>
//         value match
//           case Expression.Word(value) => ???
//             // Right(
//             //   (
//             //     LigatureValue.Triple(
//             //       Triple(
//             //         LigatureValue.Word(entity),
//             //         LigatureValue.Word(attribute),
//             //         LigatureValue.Word(value)
//             //       )
//             //     ),
//             //     environment
//             //   )
//             // )
//           case Expression.Int(value) => ???
//             // Right(
//             //   (
//             //     LigatureValue.Triple(
//             //       Triple(
//             //         LigatureValue.Word(entity),
//             //         LigatureValue.Word(attribute),
//             //         LigatureValue.Int(value)
//             //       )
//             //     ),
//             //     environment
//             //   )
//             // )
//           case Expression.Bytes(value) => ???
//             // Right(
//             //   (
//             //     LigatureValue.Triple(
//             //       Triple(
//             //         LigatureValue.Word(entity),
//             //         LigatureValue.Word(attribute),
//             //         LigatureValue.Bytes(value)
//             //       )
//             //     ),
//             //     environment
//             //   )
//             // )
//           case stringValue: Expression.StringValue => ???
//             // eval(stringValue) match {
//             //   case Right((LigatureValue.String(result), _)) =>
//             //     Right(
//             //       (
//             //         LigatureValue.Triple(
//             //           Triple(
//             //             LigatureValue.Word(entity),
//             //             LigatureValue.Word(attribute),
//             //             LigatureValue.String(result)
//             //           )
//             //         ),
//             //         environment
//             //       )
//             //     )
//             //   case _ => ???
//             // }
//           case _ => Left(WanderError(s"Invalid Triple - ${expression}"))
//       case _ => Left(WanderError(s"Invalid Triple - ${expression}"))
//   case x => Left(WanderError(s"Unexpected start of application - $x"))
// }

// def callHostFunction(
//     hostFunction: HostFunction,
//     arguments: Seq[Expression],
// ): Either[WanderError, (LigatureValue)] = ???
//   // if arguments.size == hostFunction.parameters.size then
//   //   callHostFunctionComplete(hostFunction, arguments)
//   // else if arguments.size < hostFunction.parameters.size then
//   //   callHostFunctionPartially(hostFunction, arguments)
//   // else ???
