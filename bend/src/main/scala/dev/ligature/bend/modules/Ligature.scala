// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.*
import dev.ligature.*
import com.typesafe.scalalogging.Logger

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
    )
  )
)

// //   environment = environment.bindVariable(Name("datasetExists"), BendValue.NativeFunction(
// //     (arguments: Seq[Term], environment: Environment) =>
// //       arguments.head match
// //         case Term.StringValue(datasetName) =>
// //           instance.datasetExists(Dataset.fromString(datasetName).getOrElse(???)).map(res => BendValue.BooleanValue(res))
// //         case _ => ???
// //   )).getOrElse(???)

// //   environment = environment.bindVariable(Name("allStatements"), BendValue.NativeFunction(
// //     (arguments: Seq[Term], environment: Environment) =>
// //       arguments.head match
// //         case Term.StringValue(datasetName) =>
// //           instance
// //             .allStatements(Dataset.fromString(datasetName).getOrElse(???))
// //             .map(statementToBendValue)
// //             .compile.toList.map(BendValue.ListValue(_))
// //         case _ => ???
// //   )).getOrElse(???)

// //   environment = environment.bindVariable(Name("addStatements"), BendValue.NativeFunction(
// //     (arguments: Seq[Term], environment: Environment) =>
// //       (arguments(0), arguments(1)) match
// //         case (Term.StringValue(datasetName),
// //               Term.List(statementTerms)) =>
// //                 val dataset = Dataset.fromString(datasetName).getOrElse(???)
// //                 termsToStatements(statementTerms, ListBuffer()) match
// //                   case Left(value) => ???
// //                   case Right(statements) =>
// //                     instance
// //                       .addStatements(dataset, Stream.emits(statements))
// //                       .map { _ => BendValue.Nothing }
// //         case _ => ???
// //   )).getOrElse(???)

// //   environment = environment.bindVariable(Name("removeStatements"), BendValue.NativeFunction(
// //     (arguments: Seq[Term], environment: Environment) =>
// //       (arguments(0), arguments(1)) match
// //         case (Term.StringValue(datasetName),
// //               Term.List(statementTerms)) =>
// //                 val dataset = Dataset.fromString(datasetName).getOrElse(???)
// //                 termsToStatements(statementTerms, ListBuffer()) match
// //                   case Left(value) => ???
// //                   case Right(statements) =>
// //                     instance
// //                       .removeStatements(dataset, Stream.emits(statements))
// //                       .map { _ => BendValue.Nothing }
// //         case _ => ???
// //   )).getOrElse(???)

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

// // def termsToStatements(terms: Seq[Term], statements: ListBuffer[Statement]): Either[LigatureError, Seq[Statement]] =
// //   if terms.nonEmpty then
// //     terms.head match
// //       case Term.List(statementTerms) =>
// //         termsToStatement(statementTerms) match
// //           case Right(statement) =>
// //             termsToStatements(terms.tail, statements += statement)
// //           case Left(err) => Left(err)
// //       case _ => println(terms.head); ???
// //   else
// //     Right(statements.toSeq)

// // def termsToStatement(terms: Seq[Term]): Either[LigatureError, Statement] =
// //   if terms.size == 3 then
// //     val entity = terms(0)
// //     val attribute = terms(1)
// //     val value = terms(2)
// //     (entity, attribute, value) match
// //       case (entity: Term.IdentifierLiteral, attribute: Term.IdentifierLiteral, value: Term.IdentifierLiteral) =>
// //         Right(Statement(entity.value, attribute.value, value.value))
// //       case _ => ???
// //   else
// //     ???

// // def statementToBendValue(statement: Statement): BendValue =
// //   BendValue.ListValue(Seq(
// //     BendValue.LigatureValue(statement.entity),
// //     BendValue.LigatureValue(statement.attribute),
// //     BendValue.LigatureValue(statement.value)
// //   ))

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
