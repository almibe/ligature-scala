// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

// import dev.ligature.wander.*
// import dev.ligature.*
import com.typesafe.scalalogging.Logger
// import scala.collection.mutable.ListBuffer
// import scala.util.boundary

val logger = Logger("LigatureModule")

// def createLigatureModule(ligature: Ligature): WanderValue.Module = WanderValue.Module(
//   Map(
//     Field("datasets") -> WanderValue.Function(
//       HostFunction(
//         "Get all datasets from this instance.",
//         Seq(TaggedField(Field("_"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           logger.info(s"Added ${ligature.allDatasets().toList}")
//           val datasets = ligature.allDatasets().map(g => WanderValue.String(g.name))
//           Right((WanderValue.Array(datasets.toSeq), env))
//       )
//     ),
//     Field("addDataset") -> WanderValue.Function(
//       HostFunction(
//         "Add a new Dataset.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(WanderValue.String(datasetName)) =>
//               logger.info(s"Creating dataset $datasetName")
//               ligature.createDataset(DatasetName(datasetName))
//               Right(WanderValue.Module(Map()), env)
//             case _ => ???
//       )
//     ),
//     Field("removeDataset") -> WanderValue.Function(
//       HostFunction(
//         "Remove a Dataset by name.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(WanderValue.String(datasetName)) =>
//               ligature.deleteDataset(DatasetName(datasetName))
//               Right(WanderValue.Module(Map()), env)
//             case _ => ???
//       )
//     ),
//     Field("datasetExists") -> WanderValue.Function(
//       HostFunction(
//         "Check if Dataset exists.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(WanderValue.String(datasetName)) =>
//               Right(WanderValue.Bool(ligature.networkExists(DatasetName(datasetName))), env)
//             case _ => ???
//       )
//     ),
//     Field("allStatements") -> WanderValue.Function(
//       HostFunction(
//         "Get all Statements in a Dataset.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (arguments, environment) =>
//           arguments.head match
//             case WanderValue.String(datasetName) =>
//               val res = ligature
//                 .allStatements(DatasetName(datasetName))
//                 .map(statement => WanderValue.Statement(statement))
//                 .toList
//               Right((WanderValue.Array(res), environment))
//             case _ => ???
//       )
//     ),
//     Field("addStatements") -> WanderValue.Function(
//       HostFunction(
//         "Add a collection of Statements to a Dataset.",
//         Seq(
//           TaggedField(Field("datasetName"), Tag.Untagged),
//           TaggedField(Field("statements"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           (arguments(0), arguments(1)) match
//             case (WanderValue.String(datasetName), WanderValue.Array(statementTerms)) =>
//               val dataset = DatasetName(datasetName)
//               wanderValuesToStatements(statementTerms, ListBuffer()) match
//                 case Left(value) => ???
//                 case Right(edges) =>
//                   ligature.addStatements(dataset, edges.iterator)
//                   Right((WanderValue.Module(Map()), environment))
//             case _ => ???
//       )
//     ),
//     Field("removeStatements") -> WanderValue.Function(
//       HostFunction(
//         "Remove a collection of Statements from a Dataset.",
//         Seq(
//           TaggedField(Field("datasetName"), Tag.Untagged),
//           TaggedField(Field("statements"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           (arguments(0), arguments(1)) match
//             case (WanderValue.String(datasetName), WanderValue.Array(statements)) =>
//               val dataset = DatasetName(datasetName)
//               wanderValuesToStatements(statements, ListBuffer()) match
//                 case Left(value) => ???
//                 case Right(statements) =>
//                   ligature.removeStatements(dataset, statements.iterator)
//                   Right((WanderValue.Module(Map()), environment))
//             case _ => ???
//       )
//     ),
//     Field("query") -> WanderValue.Function(
//       HostFunction(
//         "Query a Dataset.",
//         Seq(
//           TaggedField(Field("datasetName"), Tag.Untagged),
//           TaggedField(Field("entity"), Tag.Untagged),
//           TaggedField(Field("attribute"), Tag.Untagged),
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments: Seq[WanderValue], environment: Environment) =>
//           arguments match
//             case Seq(WanderValue.String(datasetName), entity, attribute, value) =>
//               handleQuery(ligature, environment, datasetName, entity, attribute, value)
//             case _ => ???
//       )
//     )
//   )
// )

// def wanderValuesToStatements(
//     values: Seq[WanderValue],
//     edges: ListBuffer[Statement]
// ): Either[LigatureError, Seq[Statement]] =
//   if values.nonEmpty then
//     values.head match
//       case WanderValue.Statement(statement) =>
//         wanderValuesToStatements(values.tail, edges += statement)
//       case err => Left(LigatureError(s"Invalid statement - $err"))
//   else Right(edges.toSeq)

// def handleQuery(
//     ligature: Ligature,
//     environment: Environment,
//     datasetName: String,
//     entityArg: WanderValue,
//     attributeArg: WanderValue,
//     valueArg: WanderValue
// ): Either[WanderError, (WanderValue, Environment)] =
//   boundary:
//     val entity = entityArg match
//       case WanderValue.Identifier(identifier) => Some(identifier)
//       case WanderValue.QuestionMark           => None
//       case _                                => ???
//     val attribute = attributeArg match
//       case WanderValue.Identifier(identifier) => Some(identifier)
//       case WanderValue.QuestionMark           => None
//       case _                                => ???
//     val value = valueArg match
//       case WanderValue.Identifier(identifier) => Some(identifier)
//       case WanderValue.Bytes(value)           => Some(LigatureValue.BytesValue(value))
//       case WanderValue.Int(value)             => Some(LigatureValue.IntegerValue(value))
//       case WanderValue.String(value)          => Some(LigatureValue.StringValue(value))
//       case WanderValue.QuestionMark           => None
//       case _                                => ???
//     val dataset = DatasetName(datasetName)
//     ligature.query(dataset) { tx =>
//       val res = tx
//         .matchStatements(entity, attribute, value)
//         .map(statement => WanderValue.Statement(statement))
//       Right((WanderValue.Array(res.toSeq), environment))
//     }
