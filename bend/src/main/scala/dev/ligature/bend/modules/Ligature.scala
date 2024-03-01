// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.*
import dev.ligature.*
import com.typesafe.scalalogging.Logger
import scala.collection.mutable.ListBuffer

val logger = Logger("LigatureModule")

def createLigatureModule(ligature: Ligature): BendValue.Module = BendValue.Module(
  Map(
    Field("graphs") -> BendValue.Function(
      HostFunction(
        "Get all graphs from this instance.",
        Seq(TaggedField(Field("_"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          logger.info(s"Added ${ligature.allGraphs().toList}")
          val graphs = ligature.allGraphs().map(g => BendValue.String(g.name))
          Right((BendValue.Array(graphs.toSeq), env))
      )
    ),
    Field("addGraph") -> BendValue.Function(
      HostFunction(
        "Add a new Graph.",
        Seq(TaggedField(Field("graphName"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(BendValue.String(graphName)) =>
              logger.info(s"Creating graph $graphName")
              ligature.createGraph(GraphName(graphName))
              Right(BendValue.Module(Map()), env)
            case _ => ???
      )
    ),
    Field("removeGraph") -> BendValue.Function(
      HostFunction(
        "Remove a Graph by name.",
        Seq(TaggedField(Field("graphName"), Tag.Untagged)),
        Tag.Untagged,
        (args, env) =>
          args match
            case Seq(BendValue.String(graphName)) =>
              ligature.deleteGraph(GraphName(graphName))
              Right(BendValue.Module(Map()), env)
            case _ => ???
      )
    ),
    // Field("graphExists") -> BendValue.Function(
    //   HostFunction(
    //     "Remove a Graph by name.",
    //     Seq(TaggedField(Field("graphName"), Tag.Untagged)),
    //     Tag.Untagged,
// //     (arguments: Seq[Term], environment: Environment) =>
// //       arguments.head match
// //         case Term.StringValue(datasetName) =>
// //           instance.datasetExists(Dataset.fromString(datasetName).getOrElse(???)).map(res => BendValue.BooleanValue(res))
// //         case _ => ???
    //     (args, env) =>
    //       args match
    //         case Seq(BendValue.String(graphName)) =>
    //           ligature.deleteGraph(GraphName(graphName))
    //           Right(BendValue.Module(Map()), env)
    //         case _ => ???
    //   )
    // )
    Field("allEdges") -> BendValue.Function(
      HostFunction(
        "Get all Edges in a Graph.",
        Seq(TaggedField(Field("graphName"), Tag.Untagged)),
        Tag.Untagged,
        (arguments, environment) =>
          arguments.head match
            case BendValue.String(graphName) =>
              val res = ligature
                .allEdges(GraphName(graphName))
                .map(statementToBendValue).toList
              Right((BendValue.Array(res), environment))
            case _ => ???
      )
    ),
    Field("addEdges") -> BendValue.Function(
      HostFunction(
        "Add a collection of Edges to a Graph.",
        Seq(
          TaggedField(Field("graphName"), Tag.Untagged),
          TaggedField(Field("edges"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          (arguments(0), arguments(1)) match
            case (BendValue.String(graphName),
                  BendValue.Array(statementTerms)) =>
                    val graph = GraphName(graphName)
                    bendValuesToEdges(statementTerms, ListBuffer()) match
                      case Left(value) => ???
                      case Right(edges) =>
                        ligature.addEdges(graph, edges.iterator)
                        Right((BendValue.Module(Map()), environment))
            case _ => ???
      )
    ),
    Field("removeEdges") -> BendValue.Function(
      HostFunction(
        "Remove a collection of Edges from a Graph.",
        Seq(
          TaggedField(Field("graphName"), Tag.Untagged),
          TaggedField(Field("edges"), Tag.Untagged)
        ),
        Tag.Untagged,
        (arguments, environment) =>
          (arguments(0), arguments(1)) match
            case (BendValue.String(graphName),
                  BendValue.Array(edges)) =>
                    val graph = GraphName(graphName)
                    bendValuesToEdges(edges, ListBuffer()) match
                      case Left(value) => ???
                      case Right(edges) =>
                        ligature.removeEdges(graph, edges.iterator)
                        Right((BendValue.Module(Map()), environment))
            case _ => ???
      )
    ),
  )
)

// //   def termToIdentifierOption(term: Term): Option[Identifier] =
// //     term match
// //       case Term.NothingLiteral | Term.QuestionMark => None
// //       case Term.IdentifierLiteral(identifier) => Some(identifier)
// //       case _ => ???

// //   def termToValueOption(term: Term): Option[Value] =
// //     term match
// //       case Term.NothingLiteral | Term.QuestionMark => None
// //       case Term.IdentifierLiteral(identifier) => Some(identifier)
// //       case _ => ???

// //   environment = environment.bindVariable(Name("query"), BendValue.NativeFunction(
// //     (arguments: Seq[Term], environment: Environment) =>
// //       (arguments(0), arguments(1), arguments.lift(2), arguments.lift(3)) match
// //         case (Term.StringValue(datasetName), entityTerm, Some(attributeTerm), Some(valueTerm)) =>
// //           val dataset = Dataset.fromString(datasetName).getOrElse(???)
// //           val entity = termToIdentifierOption(entityTerm)
// //           val attribute = termToIdentifierOption(attributeTerm)
// //           val value = termToValueOption(valueTerm)
// //           instance.query(dataset) { tx =>
// //             tx.matchStatements(entity, attribute, value)
// //               .map(statementToBendValue)
// //               .compile.toList.map(BendValue.ListValue(_))
// //           }
// //         case (Term.StringValue(datasetName), query: Term.BendFunction, None, None) =>
// //           query match
// //             case Term.BendFunction(name :: Nil, body) =>
// //               val dataset = Dataset.fromString(datasetName).getOrElse(???)
// //               instance.query(dataset) { tx =>
// //                 val matchFunction = BendValue.NativeFunction((arguments, environment) =>
// //                   (arguments.lift(0), arguments.lift(1), arguments.lift(2)) match
// //                     case (Some(entityTerm), Some(attributeTerm), Some(valueTerm)) =>
// //                       val dataset = Dataset.fromString(datasetName).getOrElse(???)
// //                       val entity = termToIdentifierOption(entityTerm)
// //                       val attribute = termToIdentifierOption(attributeTerm)
// //                       val value = termToValueOption(valueTerm)
// //                       tx.matchStatements(entity, attribute, value)
// //                         .map(statementToBendValue)
// //                         .compile.toList.map(BendValue.ListValue(_))
// //                     case _ => ???)
// //                 val newEnvironment = environment.bindVariable(name, matchFunction).getOrElse(???)
// //                 eval(query.body, newEnvironment).map(_.result)
// //               }
// //             case _ => ???
// //         case _ => ???
// //   )).getOrElse(???)
// //   environment
// // }

def bendValuesToEdges(values: Seq[BendValue], edges: ListBuffer[Edge]): Either[LigatureError, Seq[Edge]] =
  if values.nonEmpty then
    values.head match
      case BendValue.Array(statementTerms) =>
        bendsValuesToEdge(statementTerms) match
          case Right(statement) =>
            bendValuesToEdges(values.tail, edges += statement)
          case Left(err) => Left(err)
      case _ => println(values.head); ???
  else
    Right(edges.toSeq)

def bendsValuesToEdge(values: Seq[BendValue]): Either[LigatureError, Edge] =
  if values.size == 3 then
    val source = values(0)
    val edge = values(1)
    val target = values(2)
    (source, edge, target) match
      case (BendValue.Label(source), BendValue.Label(edge), target: BendValue) =>
        val value: LigatureValue = target match {
          case BendValue.Bytes(value) => LigatureValue.BytesValue(value.toArray) //TODO fix Seq/Array
          case BendValue.Int(value) => LigatureValue.IntegerValue(value)
          case BendValue.String(value) => LigatureValue.StringValue(value)
          case BendValue.Label(value) => value
          case _ => ???
        }
        Right(Edge(source, edge, value))
      case _ => ???
  else
    ???

def statementToBendValue(edge: Edge): BendValue =
  BendValue.Array(Seq(
    BendValue.Label(edge.source),
    BendValue.Label(edge.label),
    edge.target match
      case LigatureValue.BytesValue(value) => BendValue.Bytes(value.toIndexedSeq)
      case LigatureValue.GraphValue(value) => ???
      case LigatureValue.IntegerValue(value) => BendValue.Int(value)
      case LigatureValue.Label(value) => BendValue.Label(LigatureValue.Label(value))
      case LigatureValue.StringValue(value) => BendValue.String(value)
  ))

// // def datasetModeEnvironment(environment: Environment, dataset: Dataset): Environment =
// //   environment

// //def instanceLibraryEnvironment(environment: Environment): Environment = {
// //// function instanceScope(scope: ExecutionScope, environment: Environment) {
// ////     // allDatasets(): Promise<Array<Dataset>>;
// ////     environment.bind(Name("allDatasets"), NativeFunction([], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // datasetExists(dataset: Dataset): Promise<boolean>;
// ////     environment.bind(Name("datasetExists"), NativeFunction(["dataset"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // matchDatasetPrefix(prefix: string): Promise<Array<Dataset>>;
// ////     environment.bind(Name("matchDatasetPrefix"), NativeFunction(["prefix"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // matchDatasetRange(start: string, end: string): Promise<Array<Dataset>>;
// ////     environment.bind(Name("matchDatasetRange"), NativeFunction(["start", "end"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // createDataset(dataset: Dataset): Promise<Dataset>;
// ////     environment.bind(Name("createDataset"), NativeFunction(["dataset"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // deleteDataset(dataset: Dataset): Promise<Dataset>;
// ////     environment.bind(Name("deleteDataset"), NativeFunction(["dataset"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // query<T>(dataset: Dataset, fn: (readTx: ReadTx) => Promise<T>): Promise<T>;
// ////     environment.bind(Name("query"), NativeFunction(["dataset", "fn"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// ////     // write<T>(dataset: Dataset, fn: (writeTx: WriteTx) => Promise<T>): Promise<T>;
// ////     environment.bind(Name("write"), NativeFunction(["dataset", "fn"], (_environment: Environment) => {
// ////         return TODO()
// ////     }))
// //// }
// //
// //  environment
// //}
