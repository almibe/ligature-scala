// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.*
import dev.ligature.*
import com.typesafe.scalalogging.Logger
import scala.collection.mutable.ListBuffer
import scala.util.boundary

val logger = Logger("LigatureModule")

def createLigatureModule(ligature: Ligature): BendValue.Module = BendValue.Module(
  Map(
    Field("datasets") -> BendValue.Function(
      HostFunction(
        "Get all datasets from this instance.",
        Seq(TaggedField(Field("_"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          logger.info(s"Added ${ligature.allDatasets().toList}")
          val datasets = ligature.allDatasets().map(g => BendValue.String(g.name))
          Right((BendValue.Array(datasets.toSeq), env))
      )
    ),
    Field("addDataset") -> BendValue.Function(
      HostFunction(
        "Add a new Dataset.",
        Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(BendValue.String(datasetName)) =>
              logger.info(s"Creating dataset $datasetName")
              ligature.createDataset(DatasetName(datasetName))
              Right(BendValue.Module(Map()), env)
            case _ => ???
      )
    ),
    Field("removeDataset") -> BendValue.Function(
      HostFunction(
        "Remove a Dataset by name.",
        Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(BendValue.String(datasetName)) =>
              ligature.deleteDataset(DatasetName(datasetName))
              Right(BendValue.Module(Map()), env)
            case _ => ???
      )
    ),
    Field("datasetExists") -> BendValue.Function(
      HostFunction(
        "Check if Dataset exists.",
        Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(BendValue.String(datasetName)) =>
              Right(BendValue.Bool(ligature.graphExists(DatasetName(datasetName))), env)
            case _ => ???
      )
    ),
    Field("allStatements") -> BendValue.Function(
      HostFunction(
        "Get all Statements in a Dataset.",
        Seq(TaggedField(Field("datasetName"), Tag.Untagged)),
        Tag.Untagged,
        (arguments, environment) =>
          arguments.head match
            case BendValue.String(datasetName) =>
              val res = ligature
                .allStatements(DatasetName(datasetName))
                .map(statement => BendValue.Statement(statement))
                .toList
              Right((BendValue.Array(res), environment))
            case _ => ???
      )
    ),
    Field("addStatements") -> BendValue.Function(
      HostFunction(
        "Add a collection of Statements to a Dataset.",
        Seq(
          TaggedField(Field("datasetName"), Tag.Untagged),
          TaggedField(Field("statements"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          (arguments(0), arguments(1)) match
            case (BendValue.String(datasetName), BendValue.Array(statementTerms)) =>
              val dataset = DatasetName(datasetName)
              bendValuesToStatements(statementTerms, ListBuffer()) match
                case Left(value) => ???
                case Right(edges) =>
                  ligature.addStatements(dataset, edges.iterator)
                  Right((BendValue.Module(Map()), environment))
            case _ => ???
      )
    ),
    Field("removeStatements") -> BendValue.Function(
      HostFunction(
        "Remove a collection of Statements from a Dataset.",
        Seq(
          TaggedField(Field("datasetName"), Tag.Untagged),
          TaggedField(Field("statements"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          (arguments(0), arguments(1)) match
            case (BendValue.String(datasetName), BendValue.Array(statements)) =>
              val dataset = DatasetName(datasetName)
              bendValuesToStatements(statements, ListBuffer()) match
                case Left(value) => ???
                case Right(statements) =>
                  ligature.removeStatements(dataset, statements.iterator)
                  Right((BendValue.Module(Map()), environment))
            case _ => ???
      )
    ),
    Field("query") -> BendValue.Function(
      HostFunction(
        "Query a Dataset.",
        Seq(
          TaggedField(Field("datasetName"), Tag.Untagged),
          TaggedField(Field("entity"), Tag.Untagged),
          TaggedField(Field("attribute"), Tag.Untagged),
          TaggedField(Field("value"), Tag.Untagged),
        ),
        Tag.Untagged,
        (arguments: Seq[BendValue], environment: Environment) =>
          arguments match
            case Seq(BendValue.String(datasetName), entity, attribute, value) =>
              handleQuery(ligature, environment, datasetName, entity, attribute, value)
            case _ => ???
      )
    )
  )
)

def bendValuesToStatements(
    values: Seq[BendValue],
    edges: ListBuffer[Statement]
): Either[LigatureError, Seq[Statement]] =
  if values.nonEmpty then
    values.head match
      case BendValue.Statement(statement) =>
        bendValuesToStatements(values.tail, edges += statement)
      case err => Left(LigatureError(s"Invalid statement - $err"))
  else Right(edges.toSeq)

def handleQuery(ligature: Ligature, environment: Environment, datasetName: String, entityArg: BendValue, attributeArg: BendValue, valueArg: BendValue):
    Either[BendError, (BendValue, Environment)] =
  boundary:
    val entity = entityArg match
      case BendValue.Identifier(identifier) => Some(identifier)
      case BendValue.QuestionMark => None
      case _ => ???
    val attribute = attributeArg match
      case BendValue.Identifier(identifier) => Some(identifier)
      case BendValue.QuestionMark => None
      case _ => ???
    val value = valueArg match
      case BendValue.Identifier(identifier) => Some(identifier)
      case BendValue.Bytes(value) => Some(LigatureValue.BytesValue(value))
      case BendValue.Int(value) => Some(LigatureValue.IntegerValue(value))
      case BendValue.String(value) => Some(LigatureValue.StringValue(value))
      case BendValue.QuestionMark => None
      case _ => ???
    val dataset = DatasetName(datasetName)
    ligature.query(dataset) { tx =>
      val res = tx.matchStatements(entity, attribute, value)
        .map(statement => BendValue.Statement(statement))
      Right((BendValue.Array(res.toSeq), environment))
    }
