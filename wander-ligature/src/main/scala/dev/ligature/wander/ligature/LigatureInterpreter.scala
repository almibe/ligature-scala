/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.ligature

import dev.ligature.wander.*
import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break
import dev.ligature.Ligature
import dev.ligature.Graph
import dev.ligature.Edge
import dev.ligature.Label
import dev.ligature.Value

class LigatureInterpreter(instance: Ligature) {
  private var defaultGraph: Option[Graph] = None

//   override def eval(
//       expressions: Seq[Expression],
//       environment: Environment
//   ): Either[WanderError, (WanderValue, Environment)] =
//     expressions match {
//       case Seq(expression) => eval(expression, environment)
//       case Seq(
//             source: Expression.IdentifierValue,
//             edge: Expression.IdentifierValue,
//             target: Expression
//           ) =>
//         val value =
//           Edge(Label(source.value.name), Label(edge.value.name), toValue(target).getOrElse(???))
//         this.defaultGraph match {
//           case None => Left(WanderError("Graph not set with `use graphName`."))
//           case Some(graph) =>
//             instance.addEdges(graph, Seq(value).iterator)
//             Right((WanderValue.Nothing, environment)) // TODO return triple
//         }
//       case Seq(
//             source,
//             edge,
//             target
//           ) =>
//         ???
//         this.defaultGraph match {
//           case None => Left(WanderError("Graph not set with `use graphName`."))
//           case Some(graph) =>
//             ??? // instance.addEdges(graph, Seq(value).iterator)
//             Right((WanderValue.Nothing, environment)) // TODO return triple
//         }
//       case _ =>
//         expressions.head match {
//           case Expression.NameExpression(value) =>
//             eval(expressions.head, environment) match {
//               case Left(value) => Left(value)
//               case Right((value, _)) =>
//                 value match {
//                   case WanderValue.HostFunction(hostFunction) =>
//                     hostFunction.fn(expressions.tail, environment)
//                   case WanderValue.Lambda(lambda) =>
//                     var newScope = environment.newScope()
//                     expressions.tail.zipWithIndex.foreach { (arg, i) =>
//                       eval(arg, environment) match {
//                         case Left(value) => ???
//                         case Right(value) =>
//                           newScope = newScope.bindVariable(lambda.parameters(i), value._1)
//                       }
//                     }
//                     dev.ligature.wander.eval(lambda.body, newScope)
//                   case _ => ???
//                 }
//             }
//           case _ => Left(WanderError(s"Could not eval ${expressions}"))
//         }
//     }

  def use(graph: Graph) =
    this.defaultGraph = Some(graph)

//   def toValue(expression: Expression): Either[WanderError, Value] =
//     expression match {
//       case Expression.IdentifierValue(Identifier(value)) => Right(Label(value))
//       case _                                             => ???
//     }

//   def handleName(
//       name: Name,
//       environment: Environment
//   ): Either[WanderError, (WanderValue, Environment)] =
//     environment.read(name) match {
//       case Left(value) => Left(value)
//       case Right(value) =>
//         value match {
//           case WanderValue.HostProperty(hostProperty) => hostProperty.read(environment)
//           case v                                      => Right((v, environment))
//         }
//     }

//   def handleQuery(
//       entity: WanderValue,
//       attribute: WanderValue,
//       value: WanderValue,
//       graphName: String,
//       environment: Environment
//   ): Either[WanderError, (WanderValue, Environment)] = {
//     val e = entity match {
//       case WanderValue.QuestionMark      => None
//       case WanderValue.Identifier(value) => Some(value)
//       case _                             => ???
//     }
//     val a = attribute match {
//       case WanderValue.QuestionMark      => None
//       case WanderValue.Identifier(value) => Some(value)
//       case _                             => ???
//     }
//     val v = value match {
//       case WanderValue.QuestionMark => None
//       case value                    => Some(value)
//     }
//     if false then // (environment.graphs.contains(graphName)) {
//       ??? /// environment.graphs(graphName)
//     else Right((WanderValue.Array(Seq()), environment))
//   }

// //   def handleTriple(
// //       entity: Expression,
// //       attribute: Expression,
// //       value: Expression,
// //       environment: Environment
// //   ): Either[WanderError, (WanderValue, Environment)] = {
// //     val res = for {
// //       entityRes <- eval(entity, environment)
// //       attributeRes <- eval(attribute, environment)
// //       valueRes <- eval(value, environment)
// //     } yield (entityRes._1, attributeRes._1, valueRes._1)
// //     res match {
// //       case Left(value) => Left(value)
// //       case Right(value) =>
// //         value match {
// //           case (
// //                 WanderValue.Identifier(entity),
// //                 WanderValue.Identifier(attribute),
// //                 value: WanderValue
// //               ) =>
// //             val triple: WanderValue.Triple = WanderValue.Triple(entity, attribute, value)
// //               ??? /// environment.addTriple(triple)
// //                 Right(triple, environment)
// //           case (e: WanderValue, a: WanderValue, v: WanderValue) =>
// //             handleQuery(e, a, v, "", environment)
// //         }
// //     }
// //   }

// //   def handleQuad(
// //       entity: Expression,
// //       attribute: Expression,
// //       value: Expression,
// //       graph: Expression,
// //       environment: Environment
// //   ): Either[WanderError, (WanderValue.Quad, Environment)] =
// //     for {
// //       entityRes <- eval(entity, environment)
// //       attributeRes <- eval(attribute, environment)
// //       valueRes <- eval(value, environment)
// //       graphRes <- eval(graph, environment)
// //     } yield (entityRes._1, attributeRes._1, valueRes._1, graphRes._1) match {
// //       case (
// //             WanderValue.Identifier(entity),
// //             WanderValue.Identifier(attribute),
// //             value,
// //             WanderValue.Identifier(graph)
// //           ) =>
// //         /// val quad: WanderValue.Quad = WanderValue.Quad(entity, attribute, value, graph)
// //         ???
// //       // environment.addQuad(quad)
// //       // (quad, environment)
// //  = WanderValue.Quad(entity, attribute, value, graph)
// //         ???
// //         //environment.addQuad(quad)
// //         //(quad, environment)
// // uad(entity, attribute, value, graph)
// //         ???
// //         //environment.addQuad(quad)
// //         //(quad, environment)
// //       case _ => ???
// //     }
}
