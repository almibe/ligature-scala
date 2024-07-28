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

// def createLigatureModule(ligature: Ligature): LigatureValue.Module = LigatureValue.Module(
//   Map(
//     Field("datasets") -> LigatureValue.Function(
//       HostFunction(
//         "Get all datasets from this instance.",
//         Seq(TaggedField(Field("_"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           logger.info(s"Added ${ligature.allDatasets().toList}")
//           val datasets = ligature.allDatasets().map(g => LigatureValue.String(g.name))
//           Right((LigatureValue.Array(datasets.toSeq), env))
//       )
//     ),
//     Field("addDataset") -> LigatureValue.Function(
//       HostFunction(
//         "Add a new Dataset.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(LigatureValue.String(datasetName)) =>
//               logger.info(s"Creating dataset $datasetName")
//               ligature.createDataset(DatasetName(datasetName))
//               Right(LigatureValue.Module(Map()), env)
//             case _ => ???
//       )
//     ),
//     Field("removeDataset") -> LigatureValue.Function(
//       HostFunction(
//         "Remove a Dataset by name.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(LigatureValue.String(datasetName)) =>
//               ligature.deleteDataset(DatasetName(datasetName))
//               Right(LigatureValue.Module(Map()), env)
//             case _ => ???
//       )
//     ),
//     Field("datasetExists") -> LigatureValue.Function(
//       HostFunction(
//         "Check if Dataset exists.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (args, env) =>
//           args match
//             case Seq(LigatureValue.String(datasetName)) =>
//               Right(LigatureValue.Bool(ligature.networkExists(DatasetName(datasetName))), env)
//             case _ => ???
//       )
//     ),
//     Field("allTriples") -> LigatureValue.Function(
//       HostFunction(
//         "Get all Triples in a Dataset.",
//         Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
//         Tag.Untagged,
//         (arguments, environment) =>
//           arguments.head match
//             case LigatureValue.String(datasetName) =>
//               val res = ligature
//                 .allTriples(DatasetName(datasetName))
//                 .map(triple => LigatureValue.Triple(triple))
//                 .toList
//               Right((LigatureValue.Array(res), environment))
//             case _ => ???
//       )
//     ),
//     Field("addTriples") -> LigatureValue.Function(
//       HostFunction(
//         "Add a collection of Triples to a Dataset.",
//         Seq(
//           TaggedField(Field("datasetName"), Tag.Untagged),
//           TaggedField(Field("triples"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           (arguments(0), arguments(1)) match
//             case (LigatureValue.String(datasetName), LigatureValue.Array(tripleTerms)) =>
//               val dataset = DatasetName(datasetName)
//               wanderValuesToTriples(tripleTerms, ListBuffer()) match
//                 case Left(value) => ???
//                 case Right(edges) =>
//                   ligature.addTriples(dataset, edges.iterator)
//                   Right((LigatureValue.Module(Map()), environment))
//             case _ => ???
//       )
//     ),
//     Field("removeTriples") -> LigatureValue.Function(
//       HostFunction(
//         "Remove a collection of Triples from a Dataset.",
//         Seq(
//           TaggedField(Field("datasetName"), Tag.Untagged),
//           TaggedField(Field("triples"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments, environment) =>
//           (arguments(0), arguments(1)) match
//             case (LigatureValue.String(datasetName), LigatureValue.Array(triples)) =>
//               val dataset = DatasetName(datasetName)
//               wanderValuesToTriples(triples, ListBuffer()) match
//                 case Left(value) => ???
//                 case Right(triples) =>
//                   ligature.removeTriples(dataset, triples.iterator)
//                   Right((LigatureValue.Module(Map()), environment))
//             case _ => ???
//       )
//     ),
//     Field("query") -> LigatureValue.Function(
//       HostFunction(
//         "Query a Dataset.",
//         Seq(
//           TaggedField(Field("datasetName"), Tag.Untagged),
//           TaggedField(Field("entity"), Tag.Untagged),
//           TaggedField(Field("attribute"), Tag.Untagged),
//           TaggedField(Field("value"), Tag.Untagged)
//         ),
//         Tag.Untagged,
//         (arguments: Seq[LigatureValue], environment: Environment) =>
//           arguments match
//             case Seq(LigatureValue.String(datasetName), entity, attribute, value) =>
//               handleQuery(ligature, environment, datasetName, entity, attribute, value)
//             case _ => ???
//       )
//     )
//   )
// )

// def wanderValuesToTriples(
//     values: Seq[LigatureValue],
//     edges: ListBuffer[Triple]
// ): Either[LigatureError, Seq[Triple]] =
//   if values.nonEmpty then
//     values.head match
//       case LigatureValue.Triple(triple) =>
//         wanderValuesToTriples(values.tail, edges += triple)
//       case err => Left(LigatureError(s"Invalid triple - $err"))
//   else Right(edges.toSeq)

// def handleQuery(
//     ligature: Ligature,
//     environment: Environment,
//     datasetName: String,
//     entityArg: LigatureValue,
//     attributeArg: LigatureValue,
//     valueArg: LigatureValue
// ): Either[WanderError, (LigatureValue, Environment)] =
//   boundary:
//     val entity = entityArg match
//       case LigatureValue.Word(word) => Some(word)
//       case LigatureValue.QuestionMark           => None
//       case _                                => ???
//     val attribute = attributeArg match
//       case LigatureValue.Word(word) => Some(word)
//       case LigatureValue.QuestionMark           => None
//       case _                                => ???
//     val value = valueArg match
//       case LigatureValue.Word(word) => Some(word)
//       case LigatureValue.Bytes(value)           => Some(LigatureValue.Bytes(value))
//       case LigatureValue.Int(value)             => Some(LigatureValue.Int(value))
//       case LigatureValue.String(value)          => Some(LigatureValue.String(value))
//       case LigatureValue.QuestionMark           => None
//       case _                                => ???
//     val dataset = DatasetName(datasetName)
//     ligature.query(dataset) { tx =>
//       val res = tx
//         .matchTriples(entity, attribute, value)
//         .map(triple => LigatureValue.Triple(triple))
//       Right((LigatureValue.Array(res.toSeq), environment))
//     }
